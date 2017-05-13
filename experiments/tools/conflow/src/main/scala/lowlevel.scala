
package conflow {
	import conflow.graphs._
	import conflow.Kernel._
	import conflow.constraints._

	object Lowlevel {
		val transformGotos = (graph: ProgramGraph[StackConstraint]) => 
			graph.mapEdges {
				case (old, node) => node match {
					case CodePoint(position, "goto", Seq(jump), _) => Set((graph.get(position + jump), stack.Implicit))
					case CodePoint(position, "goto_w", Seq(jump), _) => Set((graph.get(position + jump), stack.Implicit))
					case _ => old
				} 
			}
		
		val transformIfs = (graph: ProgramGraph[StackConstraint]) =>
			graph.mapEdges { 
				case (old, node) => node match {
					// separate cases here
					case CodePoint(position, op, Seq(jump), _) if op.startsWith("if") => 
						old ++ Set((graph.get(position + jump), stack.Implicit /* TODO: FIX! */))
					case _ => old
				} 
			}

		val transformSwitches = (graph: ProgramGraph[StackConstraint]) => 
			graph.mapEdges {
				case (old, node) => node match {
					case cp@CodePoint(index, "lookupswitch", args@Seq(default, npairs, pairIndices@_*), _) =>							
						val pairs = pairIndices.foldLeft((Seq[(Int, Int)](), None: Option[Int]))((all, el) => {
							if(all._2.isDefined) {
								(all._1 ++ Seq((all._2.get, el)), None)
							} else {
								(all._1, Option(el))
							}
						})._1.map { case (condition, offset) =>
							(graph.get(index + offset), stack.TopIs(Nat(condition)))
						} ++ Seq((graph.get(index + default), stack.Else))

						pairs

					/* add tableswitch here */

					case _ => old
				}
			}

		def apply(g: ProgramGraph[StackConstraint]) = 
			transformSwitches(transformIfs(transformGotos(g)))

		def graphFrom(nodes: Seq[CodePoint]) = {
 			ProgramGraph(nodes.map { case cp@CodePoint(i, _, _, _) => (i → cp) }.toMap, 
				(nodes zip nodes.drop(1)).map (tup => (tup._1 → Set((tup._2, stack.Implicit)))).toMap)
		}

	}
}