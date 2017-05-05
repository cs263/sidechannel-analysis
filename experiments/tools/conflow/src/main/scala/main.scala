
package conflow {
	object Main {
		import socks._
		import socks.Implicits._

		def main(args: Array[String]): Unit = {
			// add library to Kernel
			Kernel.append("tests/PasswordInsecure.jar")
			Kernel.Point("tests.password.PasswordInsecure", "main", "([Ljava/lang/String;)V")

			// send to channel helloworld
			//"helloworld" << Kernel("javassist.CtClass")
		}
	}
}