
package conflow {
	object Main {
		import socks._
		import socks.Implicits._

		def main(args: Array[String]): Unit = {
			// add library to Kernel
			Kernel.append("lib/javassist-3.20.0-GA.jar")

			// send to channel helloworld
			"helloworld" << Kernel("javassist.CtClass")
		}
	}
}