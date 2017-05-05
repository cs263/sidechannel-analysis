
package conflow {
	object Kernel {
		import javassist._
		import javassist.bytecode._
		import scala.util.{ Success, Failure, Try }

		private[this] val classPool = new ClassPool()
		private[this] var classPath = classPool.appendSystemPath

		def append(name: String) =
			classPath = classPool.insertClassPath(name)

		def path = classPath.toString

		private[this] def fetchClass(name: String) = {
			println("Fetching class...")
			Try(classPool.get(name)) match {
				case Success(c) => Option(c)
				case Failure(e) =>
					println(e)
					None
			}
		}

		private[this] def fetchMethod(ct: CtClass, name: String, desc: String) = {
			println("Fetching method...")
			Try(ct.getMethod(name, desc)) match {
				case Success(m) => Option(m)
				case Failure(e) =>
					println("Other candidates:")
					ct.getDeclaredMethods(name).foreach { println _ }
					None
			}
		}

		case class Point(c: CtClass, m: CtMethod) {
			println(s"Point ${c.getName}#${m.getName} made...")

			val indexAsByte = (code: CodeIterator, index: Int) =>
				Seq(code.byteAt(index))

			val indexAsWord = (code: CodeIterator, index: Int) =>
				Seq(code.u16bitAt(index))

			val indexAsWide = (code: CodeIterator, index: Int) =>
				Seq(code.s32bitAt(index))

			val instructionArguments = Map(
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
				("iinc" → ((code: CodeIterator, index: Int) =>
					Seq(indexAsByte(code, index), code.signedByteAt(index + 1)))),
				("iload" → indexAsByte),
				("instanceof" → indexAsWord),
				("invokedynamic" → ((code: CodeIterator, index: Int) =>
					Seq(indexAsByte(code, index), 0, 0))),
				("invokeinterface" → ((code: CodeIterator, index: Int) =>
					Seq(indexAsWord(code, index), indexAsByte(code, index + 2), 0))),
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
					val index = ((prePadding / 4) + 1) * 4 // todo: should be offset from start of method with multiple of 4
					val defaults = code.s32bitAt(index)
					val npairs = code.s32bitAt(index + 4)
					Seq(defaults, npairs) ++ (0 until npairs).map(x => code.s32bitAt((index + 8) + 4 * x))
				})),
				("lstore" → indexAsByte),
				("multianewarray" → ((code: CodeIterator, index: Int) =>
					Seq(indexAsWord(code, index), indexAsByte(code, index + 2), 0))),
				("new" → indexAsWord),
				("newarray" → indexAsByte),
				("putfield" → indexAsWord),
				("putstatic" → indexAsWord),
				("ret" → indexAsByte),
				("sipush" → indexAsWord),
				("tableswitch" → ((code: CodeIterator, prePadding: Int) => {
					val index = ((prePadding / 4) + 1) * 4 // todo: should be offset from start of method with multiple of 4
					val defaults = code.s32bitAt(index)
					val lowbyte = code.s32bitAt(index + 4)
					val highbyte = code.s32bitAt(index + 8)
					val jumps = highbyte - lowbyte + 1
					Seq(defaults, lowbyte, highbyte) ++ (0 until jumps).map(x => code.s32bitAt((index + 8) + 4 * x))
				})),
				("wide" → ((code: CodeIterator, index: Int) => throw new Exception("Wide not implemented")))
			)

			val code = m.getMethodInfo.getCodeAttribute.iterator
			while(code.hasNext) {
				val index = code.next
				val lookahead = code.lookAhead
				val opcode = code.byteAt(index)
				val mnemonic = Mnemonic.OPCODE(opcode)
				val args = instructionArguments.get(mnemonic).map(fn => fn(code, index + 1))
				println(s"${index} ${mnemonic} ${args}")
			}
		}

		object Point {
			def apply(klass: String, method: String, desc: String): Option[Point] = 
				for {
					c <- fetchClass(klass)
					m <- fetchMethod(c, method, desc)
				} yield Point(c, m)
		}

	}
}