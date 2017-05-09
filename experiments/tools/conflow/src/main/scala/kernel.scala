
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
					println("Other candidates:")
					ct.getDeclaredMethods(name).foreach { println _ }
					None
			}
		}

		val indexAsByte = (code: CodeIterator, index: Int) =>
			Seq(code.byteAt(index))

		val indexAsWord = (code: CodeIterator, index: Int) =>
			Seq(code.u16bitAt(index))

		val indexAsWide = (code: CodeIterator, index: Int) =>
			Seq(code.s32bitAt(index))

		val instructionArguments = Map[String, (CodeIterator, Int) => Seq[Int]](
			("aload" → indexAsByte),
			("anewarray" → indexAsWord),
			("astore" → indexAsByte),
			("bipush" → indexAsWord),
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
				val index = ((prePadding / 4) + 1) * 4
				val defaults = code.s32bitAt(index)
				val npairs = code.s32bitAt(index + 4)
				Seq(defaults, npairs) ++ (0 until npairs).map(x => code.s32bitAt((index + 8) + 4 * x))
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
				val index = ((prePadding / 4) + 1) * 4
				val defaults = code.s32bitAt(index)
				val lowbyte = code.s32bitAt(index + 4)
				val highbyte = code.s32bitAt(index + 8)
				val jumps = highbyte - lowbyte + 1
				Seq(defaults, lowbyte, highbyte) ++ (0 until jumps).map(x => code.s32bitAt((index + 8) + 4 * x))
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

		case class CodePoint(index: Int, mnemonic: String, 
			args: Seq[Int], constPoolEntry: Option[conflow.Descriptor]) {
			
			override def toString = {
				var result = ""
				result += s"[ ${index} ] ${mnemonic} "
				if(!args.isEmpty)
					result += s"${args} "

				if(constPoolEntry.isDefined)
					result += s"${constPoolEntry.get}"

				result.trim
			}
		}

		case class Instructions(c: CtClass, m: CtMethod) {
			println(s"Entry point: ${c.getName}#${m.getName}...")

			val code = m.getMethodInfo.getCodeAttribute.iterator
			var instructions = Seq[CodePoint]()

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
			}

			val graph = graphs.ProgramGraph.from(instructions)

			val gotosFixed = (graph: graphs.ProgramGraph[Unit]) => {
				graph.mapEdges { case (old, node) => node match {
					case CodePoint(_, "goto", Seq(jump), _) => Set((graph.nodes(jump), ()))
					case CodePoint(_, "goto_w", Seq(jump), _) => Set((graph.nodes(jump), ()))
					case _ => old
				} }
			}

			val ifsFixed = (graph: graphs.ProgramGraph[Unit]) => {
				graph.mapEdges { case (old, node) => node match {
					case CodePoint(_, op, Seq(jump), _) if op.startsWith("if") => 
						old ++ Set((graph.nodes(jump), ()))
					case _ => old
				} }
			}

			println(ifsFixed(gotosFixed(graph)))
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