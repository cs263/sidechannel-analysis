
package conflow {
	package object graphs {
		import conflow.Kernel._
		import conflow.constraints._

		case class Node[+T, +S](
			val value: T,
			incoming: Seq[(S, Int)],
			outgoing: Seq[(S, Int)])

		object Node {
			def apply[T, S](v: T): Node[T, S] = Node(v, Seq(), Seq())
		}

		type Graph[T, S] = Seq[Node[T, S]]
	}
}
