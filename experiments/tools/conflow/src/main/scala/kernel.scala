
object Names {
	val letters = Seq("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
					  "l", "m", "n", "o", "p", "r", "s", "t", "u", "v", "w",
					  "x", "y", "z")
}


package conflow.constraints {
	sealed trait Constraint {
		def reverse: Constraint = Not(this)

		def and(o: Constraint): AllOf = AllOf.from(this, o)
		def or(o: Constraint): AnyOf = AnyOf.from(this, o)
	}

	sealed trait Expression extends Constraint
	case object Null extends Constraint

	case class Nat(n: Int) extends Expression
	case class Var(n: Int) extends Expression

	sealed trait Operation extends Expression

	case class Not(e: Constraint) extends Operation {
		override def reverse = e
	}

	case class AllOf(es: Seq[Constraint]) extends Operation {
		override def reverse = AnyOf(es.map { _.reverse })
	}

	object AllOf {
		def from(a: Constraint, b: Constraint): AllOf = (a, b) match {
			case (AllOf(x), AllOf(y)) => AllOf(x ++ y)
			case (x, AllOf(y)) => AllOf(Seq(x) ++ y)
			case (AllOf(x), y) => AllOf(x ++ Seq(y))
			case (x, y) => AllOf(Seq(x, y))
		}
	}

	case class AnyOf(es: Seq[Constraint]) extends Operation {
		override def reverse = AllOf(es.map { _.reverse })
	}

	object AnyOf {
		def from(a: Constraint, b: Constraint): AnyOf = (a, b) match {
			case (AnyOf(x), AnyOf(y)) => AnyOf(x ++ y)
			case (x, AnyOf(y)) => AnyOf(Seq(x) ++ y)
			case (AnyOf(x), y) => AnyOf(x ++ Seq(y))
			case (x, y) => AnyOf(Seq(x, y))
		}
	}

	case class Eq(a: Constraint, b: Constraint) extends Operation {
		override def reverse = Ne(a, b)
	}

	case class Ne(a: Constraint, b: Constraint) extends Operation {
		override def reverse = Eq(a, b)
	}

	case class Lt(a: Constraint, b: Constraint) extends Operation {
		override def reverse = Ge(a, b)
	}

	case class Gt(a: Constraint, b: Constraint) extends Operation {
		override def reverse = Le(a, b)
	}

	case class Le(a: Constraint, b: Constraint) extends Operation {
		override def reverse = Gt(a, b)
	}

	case class Ge(a: Constraint, b: Constraint) extends Operation {
		override def reverse = Lt(a, b)
	}

	case class Requires(n: Int, e: Constraint) extends Constraint {
		override def reverse = Requires(n, e.reverse)
	}

	case class IsRef(e: Constraint) extends Constraint
	case class IsInt(e: Constraint) extends Constraint

	sealed trait StackConstraint extends Constraint

	package stack {
		case object Implicit extends StackConstraint
		case object Exception extends StackConstraint
		case object Invoke extends StackConstraint
		case object Jump extends StackConstraint
		case class Entry(n: Int) extends StackConstraint
	}
}

package conflow {
	trait Descriptor
	case class ValueDesc(value: Any) extends Descriptor {
		override def toString = s"$value"
	}

	case class FieldDesc(itsClass: String, itsName: String, itsType: String) extends Descriptor {
		override def toString = s"$itsClass#$itsName : $itsType"
	}

	case class MethodDesc(itsClass: String, itsName: String, itsType: String) extends Descriptor {
		override def toString = s"$itsClass#$itsName : $itsType"
	}

	case object FailedDesc extends Descriptor {
		override def toString = throw new Exception("Failed description of instruction")
	}

	package object Kernel {
		import javassist._
		import javassist.bytecode._
		import scala.util.{ Success, Failure, Try }

		private[this] val classPool = new ClassPool()
		private[this] var classPath = classPool.appendSystemPath

		def append(name: String) =
			classPath = classPool.insertClassPath(name)

		def path = classPath.toString

		private[this] def fetchClass(name: String) = {
			Try(classPool.get(name)) match {
				case Success(c) => Option(c)
				case Failure(e) =>
					println(e)
					None
			}
		}

		private[this] def fetchMethod(ct: CtClass, name: String, desc: String) = {
			Try(ct.getMethod(name, desc)) match {
				case Success(m) => Option(m)
				case Failure(e) =>
					ct.getDeclaredMethods(name).foreach { println _ }
					None
			}
		}

		val indexAsByte = (code: CodeIterator, index: Int) =>
			Seq(code.byteAt(index))

		val indexAsWord = (code: CodeIterator, index: Int) => {
			val n = code.u16bitAt(index)
			Seq(if(n > 32000) -(65536 - n) else n)
		}

		val indexAsWide = (code: CodeIterator, index: Int) =>
			Seq(code.s32bitAt(index))

		val instructionArguments = Map[String, (CodeIterator, Int) => Seq[Int]](
			("aload" → indexAsByte),
			("anewarray" → indexAsWord),
			("astore" → indexAsByte),
			("bipush" → indexAsByte),
			("checkcast" → indexAsWord),
			("dload" → indexAsByte),
			("dstore" → indexAsByte),
			("fload" → indexAsByte),
			("fstore" → indexAsByte),
			("getfield" → indexAsWord),
			("getstatic" → indexAsWord),
			("goto" → indexAsWord),
			("goto_w" → indexAsWide),
			("if_acmpeq" → indexAsWord),
			("if_acmpne" → indexAsWord),
			("if_icmpeq" → indexAsWord),
			("if_icmpne" → indexAsWord),
			("if_icmplt" → indexAsWord),
			("if_icmpge" → indexAsWord),
			("if_icmpgt" → indexAsWord),
			("if_icmple" → indexAsWord),
			("ifeq" → indexAsWord),
			("ifne" → indexAsWord),
			("iflt" → indexAsWord),
			("ifge" → indexAsWord),
			("ifgt" → indexAsWord),
			("ifle" → indexAsWord),
			("ifnonnull" → indexAsWord),
			("ifnull" → indexAsWord),
			("iinc" → ((code: CodeIterator, index: Int) => {
				indexAsByte(code, index) ++ Seq(code.signedByteAt(index + 1))
			})),
			("iload" → indexAsByte),
			("instanceof" → indexAsWord),
			("invokedynamic" → ((code: CodeIterator, index: Int) =>
				indexAsByte(code, index) ++ Seq(0, 0))),
			("invokeinterface" → ((code: CodeIterator, index: Int) =>
				indexAsWord(code, index) ++ indexAsByte(code, index + 2) ++ Seq(0))),
			("invokespecial" → indexAsWord),
			("invokestatic" → indexAsWord),
			("invokevirtual" → indexAsWord),
			("istore" → indexAsByte),
			("jsr_w" → indexAsWide),
			("ldc" → indexAsByte),
			("ldc_w" → indexAsWord),
			("ldc2_w" → indexAsWord),
			("lload" → indexAsByte),
			("lookupswitch" → ((code: CodeIterator, prePadding: Int) => {
				val index = if(prePadding % 4 == 0) prePadding else ((prePadding / 4) + 1) * 4
				val defaults = code.s32bitAt(index)
				val npairs = code.s32bitAt(index + 4)
				Seq(defaults, npairs) ++ (0 to npairs + 1).map(x => code.s32bitAt((index + 8) + 4 * x))
			})),
			("lstore" → indexAsByte),
			("multianewarray" → ((code: CodeIterator, index: Int) =>
				indexAsWord(code, index) ++ indexAsByte(code, index + 2) ++ Seq(0))),
			("new" → indexAsWord),
			("newarray" → indexAsByte),
			("putfield" → indexAsWord),
			("putstatic" → indexAsWord),
			("ret" → indexAsByte),
			("sipush" → indexAsWord),
			("tableswitch" → ((code: CodeIterator, prePadding: Int) => {
				val index = if(prePadding % 4 == 0) prePadding else ((prePadding / 4) + 1) * 4
				val defaults = code.s32bitAt(index)
				val lowbyte = code.s32bitAt(index + 4)
				val highbyte = code.s32bitAt(index + 8)
				val jumps = highbyte - lowbyte + 1
				Seq(defaults, lowbyte, highbyte) ++ (0 to jumps + 1).map(x => code.s32bitAt((index + 8) + 4 * x))
			})),
			("wide" → ((code: CodeIterator, index: Int) => throw new Exception("Wide not implemented")))
		)

		type ConstPoolDescriptor = (ConstPool, Int) => conflow.Descriptor

		val dope: ConstPoolDescriptor = (cp: ConstPool, arg: Int) => FailedDesc

		val fetchField: ConstPoolDescriptor = (cp: ConstPool, arg: Int) => {
			val className = cp.getFieldrefClassName(arg)
			val fname = cp.getFieldrefName(arg)
			val ftype = cp.getFieldrefType(arg)
			FieldDesc(className, fname, ftype)
		}

		val fetchMethod: ConstPoolDescriptor = (cp: ConstPool, arg: Int) => {
			val className = cp.getMethodrefClassName(arg)
			val mname = cp.getMethodrefName(arg)
			val mtype = cp.getMethodrefType(arg)
			MethodDesc(className, mname, mtype)
		}

		val fetchInterfaceMethod: ConstPoolDescriptor = (cp: ConstPool, arg: Int) => {
			val className = cp.getInterfaceMethodrefClassName(arg)
			val mname = cp.getInterfaceMethodrefName(arg)
			val mtype = cp.getInterfaceMethodrefType(arg)
			MethodDesc(className, mname, mtype)
		}

		val usesConstPool = Map[String, ConstPoolDescriptor](
			("ldc", (cp: ConstPool, arg: Int) => ValueDesc(cp.getLdcValue(arg))),
			("ldc_w", (cp: ConstPool, arg: Int) => ValueDesc(cp.getLdcValue(arg))),
			("ldc2_w", (cp: ConstPool, arg: Int) => ValueDesc(cp.getLdcValue(arg))),
			("new", (cp: ConstPool, arg: Int) => ValueDesc(cp.getClassInfo(arg))),

			("putfield", fetchField),
			("putstatic", fetchField),
			("getfield", fetchField),
			("getstatic", fetchField),

			("invokedynamic", fetchMethod),
			("invokeinterface", fetchInterfaceMethod),
			("invokevirtual", fetchMethod),
			("invokestatic", fetchMethod),
			("invokespecial", fetchMethod),

			("multianewarray", dope),
			("instanceof", dope),
			("anewarray", dope),
			("checkcast", dope)
		)

		object CodeLocations {
				var relativeToAbsolute = Map[Int, Int]()
				var absoluteToRelative = Map[Int, Int]()

				def ref(a: Int, b: Int): Int = 
					CodeLocations.relativeToAbsolute(CodeLocations.absoluteToRelative(a) + b)
		}

		case class CodePoint(
			val index: Int,
			val mnemonic: String,
			val args: Seq[Int],
			val constPoolEntry: Option[conflow.Descriptor]) {

			override def toString = {
				var result = ""
				result += s"[ ${index} ] ${mnemonic} "
				if(!args.isEmpty)
					result += s"${args.map { _.toString }.mkString(" ")} "

				if(constPoolEntry.isDefined)
					result += s"${constPoolEntry.get}"

				result.trim
			}
		}

		case class CodeBlock(val id: Int = 0, cps: Seq[CodePoint]) {
			override def toString = s"${id}"
		}

		object CodeBlock {
			def asInt = (x: conflow.Kernel.CodeBlock) => x.id
		}

		import conflow.graphs.Node
		import conflow.constraints._

		case class Instructions(c: CtClass, m: CtMethod) {
			val code = m.getMethodInfo.getCodeAttribute.iterator
			var instructions = Seq[CodePoint]()
			private var constructedGraph = Seq[Node[CodePoint, Constraint]]()

			while(code.hasNext) {
				val index = code.next
				val opcode = code.byteAt(index)
				val mnemonic = Mnemonic.OPCODE(opcode)
				val args = instructionArguments.get(mnemonic).map(fn => fn(code, index + 1))
				val fromConstPool = if(usesConstPool contains mnemonic)
					Some(usesConstPool(mnemonic)(c.getClassFile.getConstPool, (args.get)(0)))
				else None

				val codepoint = CodePoint(index, mnemonic, args.getOrElse(Seq()), fromConstPool)
				instructions = instructions :+ codepoint

				val place = constructedGraph.length
				constructedGraph = constructedGraph :+ Node(codepoint)
				CodeLocations.relativeToAbsolute += (place → index)
				CodeLocations.absoluteToRelative += (index → place)
			}

			val exceptionTable = m.getMethodInfo.getCodeAttribute.getExceptionTable

			val exceptions = (0 until exceptionTable.size).map { i =>
				val start = CodeLocations.absoluteToRelative(exceptionTable.startPc(i))
				val end = CodeLocations.absoluteToRelative(exceptionTable.endPc(i))
				val handler = exceptionTable.handlerPc(i)
				((start to end), handler)
			}.toSeq

			constructedGraph = for(
				i <- constructedGraph.indices;
				atI: Node[CodePoint, Constraint] = constructedGraph(i);
				augmented: Node[CodePoint, Constraint] = 
					if(i + 1 < constructedGraph.length)
						Node(atI.value, Seq(), Seq((stack.Implicit, CodeLocations.relativeToAbsolute(i + 1))))
					else
						atI) yield augmented

			val exceptionalGraph = constructedGraph.map { case Node(cp@CodePoint(i, _, _, _), in, out) => 
				val inside_error_handler = exceptions.flatMap {
					case(ab, h) => 
						if(ab.contains(i)) {
							Seq((stack.Exception, h))
						} else Seq()
				}.toSeq

				Node(cp, in, out ++ inside_error_handler)
			}

			def graph = exceptionalGraph
		}

		object Instructions {
			def apply(klass: String, method: String, desc: String): Option[Instructions] =
				for {
					c <- fetchClass(klass)
					m <- fetchMethod(c, method, desc)
				} yield Instructions(c, m)
		}
	}
}
