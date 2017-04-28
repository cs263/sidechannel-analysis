
package conflow {
	object Kernel {
		import javassist._
		import scala.util.{Try, Success, Failure}

		private[this] val classPool = new ClassPool()
		private[this] var classPath = classPool.appendSystemPath

		def append(name: String) = {
			classPath = classPool.insertClassPath(name)
		}

		def path = classPath.toString

		trait Contextual {
			def apply(msg: String): Try[Contextual]			
		}

		trait Bottom extends Contextual {
			def apply(msg: String): Try[Contextual] = Failure(⊥)
		}

		object ⊥ extends Throwable with Bottom

		abstract class Context[P <: Contextual, N <: Contextual](t: T) extends Contextual {
			def pop = t
			def apply(msg: String): N
		}

		var context: Contextual = ⊥

		private[this] def getClass(name: String): Option[CtClass] = {
			Try(classPool.get(name)) match {
				case Success(klass) => Some(klass)
				case Failure(e) => None
			}
		}

		private[this] def getMethod(ct: CtClass, name: String, desc: String): Option[CtClass] = {
			Try(ct.getMethod(name, desc)) match {
				case Success(meth) => Some(meth)
				case Failure(e) => None
			}
		}

		def apply(msg: String) = context.apply(msg) match {
			case Success(ctx: Contextual) => 
				context = ctx
			case Failure(e) =>
				println(s"Error: #{e}")
		}

		case class LoaderContext() extends Context[Bottom, ClassContext](⊥) {
			def apply(msg: String): Try[Contextual] =
				getClass(msg) match {
					case Some(klass) => Success(ClassContext(this, klass))
					case None => Failure(⊥)
				}
		}

		case class ClassContext(l: LoaderContext, c: CtClass) extends Context[LoaderContext, MethodContext](l) {
			def apply(msg: String): Try[Contextual] = {
				val (name, desc) = msg.split(":")
				getMethod(c, name, desc) match {
					case Some(meth) => Success(MethodContext(meth))
					case None => Failure(⊥)
				}
			}
		}

		case class MethodContext(c: ClassContext, m: CtMethod) extends Context[ClassContext, Bottom](c) {
			def apply(msg: String): Try[Contextual] = Failure(⊥)
		}
	}
}