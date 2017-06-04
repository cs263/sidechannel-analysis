
package conflow {
	object Main {
		import socks._
		import socks.Implicits._

		def main(args: Array[String]): Unit = {
//			Kernel.append("tests/Switches.jar")
			Kernel.append("tests/passwords.jar")

//			val cfg = Kernel.Instructions("Switches", "doStuff", "()V").get.graph

			val versions = Seq(
				"ConstantTime", "ConstantNoLengthCheck", "Insecure", "InsecureNoLengthCheck", 
				"Possibly", "PossiblyNoLengthCheck", "PossiblyTwo", "PossiblyTwoNoLengthCheck")

			versions.map { v => 
				val cfg = Kernel.Instructions("tests.password.Password" + v, "checkPassword", "(Ljava/lang/String;Ljava/lang/String;)Z").get.graph
				val ll = GetLowlevelJumps(cfg)
				val graph = BuildBlocks(ll)
				graph.map { println _ }
				val diag = conflow.graphs.Graph.dot(graph, conflow.Kernel.CodeBlock.asInt, Option(v))
			}
//			println(ll)

		}
	}
}

