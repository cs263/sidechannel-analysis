
package conflow {
	object Main {
		import socks._
		import socks.Implicits._

		def main(args: Array[String]): Unit = {
			Kernel.append("tests/passwords.jar")

			val versions = Seq("ConstantTime", "ConstantNoLengthCheck", "Insecure", "InsecureNoLengthCheck", 
				"Possibly", "PossiblyNoLengthCheck", "PossiblyTwo", "PossiblyTwoNoLengthCheck")

			versions.map { v => 
				val cfg = Kernel.Instructions("tests.password.Password" + v, "checkPassword", "(Ljava/lang/String;Ljava/lang/String;)Z").get.graph
				val ll = GetLowlevelJumps(cfg)
				val graph = BuildBlocks(ll)

				conflow.graphs.Graph.dot(graph, conflow.Kernel.CodeBlock.asInt, Option(v))
				conflow.graphs.Graph.json(graph, Option(v))
			}
		}
	}
}

