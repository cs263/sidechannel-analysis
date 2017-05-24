
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
			def dot[T, S](graph: Graph[T, S]): String = {
				val parts = graph.map { case n@Node(k, in, vs) =>
					vs.map { case (s, i) => s"\t${k} -> ${i}" } + "[label=\"" + s + "\"]" }.mkString(";\n")
				}.mkString(";\n")

				s"digraph G {\n${parts}}"
			}
		}

	}
}
