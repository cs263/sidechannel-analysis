
package conflow {
	object Main {
		import socks._
		import socks.Implicits._

		def main(args: Array[String]): Unit = {
			Kernel.append("tests/passwords.jar")

//			val versions = Seq("ConstantTime", "ConstantNoLengthCheck", "Insecure", "InsecureNoLengthCheck", 
//				"Possibly", "PossiblyNoLengthCheck", "PossiblyTwo", "PossiblyTwoNoLengthCheck")

			val versions = Seq("Insecure", "InsecureNoSleep")

			versions.map { v => 
				val cfg = Kernel.Instructions("tests.password.Password" + v, "checkPassword", "(Ljava/lang/String;Ljava/lang/String;)Z").get.graph
				val ll = GetLowlevelJumps(cfg)
//				val logic = ConstraintsToLogic(ll)
				val graph = BuildBlocks(ll)

				graph.map { println _ }
				conflow.graphs.Graph.dot(graph, conflow.Kernel.CodeBlock.asInt, Some(v), "pdf")
				conflow.graphs.Graph.json(ll, None)
			}
		}
	}
}

