
package conflow {
	package graphs {
		import conflow.Kernel._
		import conflow.constraints._

		case class ProgramGraph[+Tag](
			nodes: Map[Int, CodePoint], 
			connections: Map[CodePoint, Iterable[(CodePoint, Tag)]]) {

			def mapNodes(fn : CodePoint => CodePoint): ProgramGraph[Tag] = {
				val mappedNodes = nodes.map { case (i, x) => (x, fn(x)) }
				val newNodes = nodes.mapValues { fn(_) }
				val newConnections = nodes.map { case (index, node) =>
					val fromNode = node
					val toNodes = connections(fromNode).map { case (cp, tag) =>
						(mappedNodes(cp), tag)
					}

					(fromNode → toNodes)
				}.toMap

				ProgramGraph(newNodes, newConnections)
			}

			def mapEdges[T](fn: (Iterable[(CodePoint, Tag)], CodePoint) => Iterable[(CodePoint, T)]): ProgramGraph[T] =
				ProgramGraph[T](nodes, connections.map { case (k, v) => (k → fn(v, k)) })

			override def toString: String = {
				def connsToString(key: Int) = {
					connections.get(nodes(key)) match {
						case Some(s: Iterable[(CodePoint, Tag)]) => s.toSeq.map { case (cp, t) => s"\t${cp}\n\t\t${t}" }.mkString("\n")
						case None => ""
					}
				}
				nodes.keys.toList.sorted.map { key => s"${nodes(key)}\n" + connsToString(key) }.mkString("\n")
			}
			//	nodes.mapValues { node => s"$node: ${connections.get(node).map { conn => s"\n\t$conn" }.mkString("\n")}" }.mkString("\n")
			

			def get(i: Int) = nodes(i)

			def transform[U](t: ProgramGraph[Tag] => ProgramGraph[U]): graphs.ProgramGraph[U] = t(this)
		}
	}
}