
package conflow {
	object Main {
		import socks._
		import socks.Implicits._

		def main(args: Array[String]): Unit = {
			Kernel.append("tests/Switches.jar")
			Kernel.append("tests/PasswordInsecure.jar")

			val cfg = Kernel.Instructions("Switches", "doStuff", "()V").get.graph

//			val cfg = Kernel.Instructions("tests.password.PasswordInsecure", "main", "([Ljava/lang/String;)V").get.graph
			val ll = GetLowlevelJumps(cfg)
			val graph = BuildBlocks(ll)
			val diag = conflow.graphs.Graph.dot(graph, Option("password_insecure"))

			println(diag)
		}
	}
}

