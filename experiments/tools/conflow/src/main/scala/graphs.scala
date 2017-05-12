
package conflow {
	package graphs {
		import conflow.Kernel._

		case class ProgramGraph[Tag](
			nodes: Map[Int, CodePoint], 
			connections: Map[CodePoint, Set[(CodePoint, Tag)]]) {

			def mapNodes(fn : CodePoint => CodePoint): ProgramGraph[Tag] = {
				val mappedNodes = nodes.map { case (i, x) => (x, fn(x)) }
				val newNodes = nodes.mapValues { fn(_) }
				val newConnections = nodes.map { case (index, node) =>
					val fromNode = connections(node)
					val toNodes = fromNode.map { case (cp, tag) =>
						(mappedNodes(cp), tag)
					}

					(fromNode → toNodes)
				}.toMap

				ProgramGraph[Tag](newNodes., newConnections)
				// val oldAndNew = nodes.map { case (index, oldNode) =>
				// 	val newNode = fn(oldNode)
				// 	(oldNode → (index, newNode))
				// }.toMap

				// ProgramGraph[Tag](
				// 	oldAndNew.values.toMap, 
				// 	connections.map { case (n: CodePoint, c: Set[(CodePoint, Tag)]) =>
				// 		(oldAndNew(n)._2 → (c.map { case (cp, t) => (oldAndNew(cp), t) }).toSet)
				// 	})
			}

			def mapEdges[T](fn: (Set[(CodePoint, Tag)], CodePoint) => Set[(CodePoint, T)]): ProgramGraph[T] =
				ProgramGraph[T](nodes, connections.map { case (k, v) => (k → fn(v, k)) })

			override def toString: String = {
				nodes.map { node => s"$node: ${connections.get(node).map { conn => s"\n\t$conn" }.mkString("\n")}" }.mkString("\n")
			}
		}

		object ProgramGraph {
			def from(nodes: Seq[CodePoint]) = 
				ProgramGraph(nodes.map { case cp@CodePoint(i, _, _, _) => (i → cp) }.toMap, 
					(nodes zip nodes.drop(1)).map(tup => (tup._1 → Set((tup._2, ())))).toMap)
		}
	}
}