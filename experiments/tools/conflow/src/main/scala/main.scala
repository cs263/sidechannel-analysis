
package conflow {
	object Main {
		import socks._
		import socks.Implicits._

		def main(args: Array[String]): Unit = {
			// add library to Kernel
			Kernel.append("tests/Switches.jar")
//			Kernel.append("tests/PasswordInsecure.jar")
			Kernel.Instructions("Switches", "doStuff", "()V")

			// send to channel helloworld
			//"helloworld" << Kernel("javassist.CtClass")
		}
	}
}