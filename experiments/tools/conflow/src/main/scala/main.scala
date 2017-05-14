
package conflow {
	object Main {
		import socks._
		import socks.Implicits._

		def main(args: Array[String]): Unit = {
			// add library to Kernel
			Kernel.append("tests/Switches.jar")
			Kernel.append("tests/PasswordInsecure.jar")

//			val cfg = Kernel.Instructions("Switches", "doStuff", "()V").get.graph
			val cfg = Kernel.Instructions("tests.password.PasswordInsecure", "main", "([Ljava/lang/String;)V").get.graph

			println(Lowlevel(cfg))

			// send to channel helloworld
			//"helloworld" << Kernel("javassist.CtClass")
		}
	}
}