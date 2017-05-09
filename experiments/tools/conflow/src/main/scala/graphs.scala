
package conflow {
	package graphs {
		import conflow.Kernel._

		case class ProgramGraph[Tag](
			nodes: Seq[CodePoint], 
			connections: Map[CodePoint, Set[(CodePoint, Tag)]]) {

			def mapNodes(fn : CodePoint => CodePoint): ProgramGraph[Tag] = {
				val oldAndNew = nodes.map { oldNode =>
					val newNode = fn(oldNode)
					(oldNode, newNode)
				}.toMap

				ProgramGraph[Tag](
					oldAndNew.map { _._2 }.toSeq, 
					connections.map { case (n: CodePoint, c: Set[(CodePoint, Tag)]) =>
						(oldAndNew(n) → c.map { case (cp, t) => (oldAndNew(cp), t) })
					})
			}

			def mapEdges[T](fn: (Set[(CodePoint, Tag)], CodePoint) => Set[(CodePoint, T)]): ProgramGraph[T] =
				ProgramGraph[T](nodes, connections.map { case (k, v) => (k → fn(v, k)) })

			override def toString: String = {
				nodes.map { node => s"$node: ${connections.get(node).map { conn => s"\n\t$conn" }.mkString("\n")}" }.mkString("\n")
			}
		}

		object ProgramGraph {
			def from(nodes: Seq[CodePoint]) = 
				ProgramGraph(nodes, (nodes zip nodes.drop(1)).map(tup =>
					(tup._1 → Set((tup._2, ())))
				).toMap)
		}
	}
}