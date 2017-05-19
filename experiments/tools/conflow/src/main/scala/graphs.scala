
package conflow {
	package graphs {
		import conflow.Kernel._
		import conflow.constraints._

		case class ProgramGraph[Node, Connection](
			nodes: Seq[Node], 
			connections: Map[Int, Iterable[(Int, Connection)]]) {

			type TaggedVector = (Node, Connection, Node)

			def 
		}

		case class ProgramGraph[+Point, +Tag](
			nodes: Map[Int, Point], 
			connections: Map[Point, Iterable[(Point, Tag)]]) {			

			def mapNodes(fn : Point => Point): ProgramGraph[Tag, Point] = {
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

			def mapEdges[T](fn: (Iterable[(Point, Tag)], Point) => Iterable[(Point, T)]): ProgramGraph[Point, T] =
				ProgramGraph[Point, T](nodes, connections.map { case (k, v) => (k → fn(v, k)) })

			override def toString: String = {
				def connsToString(key: Int) = {
					connections.get(nodes(key)) match {
						case Some(s: Iterable[(Point, Tag)]) => 
							s.toSeq.map { case (cp, t) => s"\t${cp}\n\t\t${t}" }.mkString("\n")
						case None => ""
					}
				}

				nodes.keys.toList.sorted.map { key => s"${nodes(key)}\n" + connsToString(key) }.mkString("\n")
			}

			def get(i: Int) = nodes(i)

			def transform[T, U](t: ProgramGraph[Point, Tag] => ProgramGraph[T, U]): graphs.ProgramGraph[T, U] = t(this)
		}
	}
}