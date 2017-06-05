import sys.process._

package conflow {
	package object graphs {
		import conflow.Kernel._
		import conflow.constraints._

		case class Node[+T, +S](
			val value: T,
			incoming: Seq[(S, Int)],
			outgoing: Seq[(S, Int)])

		type Graph[T, S] = Seq[Node[T, S]]

		object Node {
			def apply[T, S](v: T): Node[T, S] = Node(v, Seq(), Seq())
		}

		object Graph {
			def dot[T, S](graph: Graph[T, S], fn: T => Int, write: Option[String], as: String = "pdf"): String = {
				val parts = graph.map { case n@Node(k, in, vs) =>
					vs.map { case (s, i) => s"\t${fn(k)} -> ${i}" + "[label=\"" + s + "\"]" }.mkString("\n")
				}.mkString("\n")

				val g = s"digraph G {\n${parts}}"

				if(!write.isEmpty) {
				  s"echo ${g}" #| s"dot -o ${write.get}.dot" !

                                  s"echo ${g}" #| s"dot -T${as} -o ${write.get}.${as}" !
                                }

				g
			}

			def json[T, S](graph: Graph[T, S], write: Option[String]): String = {
				val whole = graph.map {
					case Node(cp, in, out) =>
						val id_prop = "\"id\" : \"" + cp + "\", "
						val incoming_prop = "\"incoming\" : { " +
							in.map { case (node, cost) => "\"" + cost + "\" : \"" + node + "\"" }.mkString(", ") + " }, "

						val outgoing_prop = "\"outgoing\" : { " +
							out.map { case (node, cost) => "\"" + cost + "\" : \"" + node + "\"" }.mkString(", ") + " }"

						"{ " + id_prop + incoming_prop + outgoing_prop + " }"
				}.mkString(", \n")

				val nodes = "[ " + whole + " ]"
				println(nodes)

				if(!write.isEmpty) {
					val writer = new java.io.PrintWriter(new java.io.File(write.get + ".json"))

					writer.write(nodes)
					writer.close()
				}

				nodes
			}
		}

	}
}
