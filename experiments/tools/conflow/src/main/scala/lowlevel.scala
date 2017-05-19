
package conflow {
	import conflow.graphs._
	import conflow.Kernel._
	import conflow.constraints._

	object Lowlevel {
		val transformGotos = (graph: ProgramGraph[CodePoint, Constraint]) => 
			graph.mapEdges {
				case (old, CodePoint(position, "goto", Seq(jump), _)) => Set((graph.get(position + jump), stack.Implicit))
				case (old, CodePoint(position, "goto_w", Seq(jump), _)) => Set((graph.get(position + jump), stack.Implicit))
				case (old, _) => old
			}
		
		def implicitToNegationOfPredicate = (old: Iterable[(CodePoint, Constraint)], req: Constraint) =>
			old.map { 
				case (cp, stack.Implicit) => 
					(cp, req.reverse)

				case rest => rest
			}

		val transformIfs = (graph: ProgramGraph[CodePoint, Constraint]) =>
			graph.mapEdges { 
				case (old, CodePoint(position, op, Seq(jump), _)) if op startsWith "if" =>
					val req = op match {
						case "if_acmpeq" => Requires(2, Eq(IsRef(stack.Entry(1)), IsRef(stack.Entry(2))))
						case "if_acmpne" => Requires(2, Ne(IsRef(stack.Entry(1)), IsRef(stack.Entry(2))))

						case "if_icmpeq" => Requires(2, Eq(IsInt(stack.Entry(1)), IsInt(stack.Entry(2))))
						case "if_icmpne" => Requires(2, Ne(IsInt(stack.Entry(1)), IsInt(stack.Entry(2))))
						case "if_icmplt" => Requires(2, Lt(IsInt(stack.Entry(1)), IsInt(stack.Entry(2))))
						case "if_icmpge" => Requires(2, Ge(IsInt(stack.Entry(1)), IsInt(stack.Entry(2))))
						case "if_icmpgt" => Requires(2, Gt(IsInt(stack.Entry(1)), IsInt(stack.Entry(2))))
						case "if_icmple" => Requires(2, Le(IsInt(stack.Entry(1)), IsInt(stack.Entry(2))))

						case "ifeq" => Requires(1, Eq(IsInt(stack.Entry(1)), Nat(0)))
						case "ifne" => Requires(1, Ne(IsInt(stack.Entry(1)), Nat(0)))
						case "iflt" => Requires(1, Lt(IsInt(stack.Entry(1)), Nat(0)))
						case "ifge" => Requires(1, Ge(IsInt(stack.Entry(1)), Nat(0)))
						case "ifgt" => Requires(1, Gt(IsInt(stack.Entry(1)), Nat(0)))
						case "ifle" => Requires(1, Le(IsInt(stack.Entry(1)), Nat(0)))

						case "ifnull" => Requires(1, Eq(IsRef(stack.Entry(1)), Null))
						case "ifnonnull" => Requires(1, Ne(IsRef(stack.Entry(1)), Null))
					}

					implicitToNegationOfPredicate(old, req) ++ Set((graph.get(position + jump), req))

				case (old, _) => old
			}

		val transformSwitches = (graph: ProgramGraph[CodePoint, Constraint]) => 
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
							(graph.get(index + offset), Requires(1, Eq(IsInt(stack.Entry(1)), Nat(condition))))
						}

						pairs ++ Seq((graph.get(index + default), 
							pairs.map { _._2 }.foldLeft(AllOf(Seq()))((old: AllOf, el: Constraint) => old.and(el.reverse))))

					case cp@CodePoint(index, "tableswitch", args@Seq(default, low, high, offsets@_*), _) =>
						(0 until (high - low + 1) zip offsets).map { case (i, offset) =>
							(graph.get(index + offset), Requires(1, Eq(IsInt(stack.Entry(1)), Nat(i))))
						} ++ Seq((graph.get(index + default), Requires(1, Eq(IsInt(stack.Entry(1)), 
								AllOf(Seq(
									Lt(IsInt(stack.Entry(1)), Nat(low)),
									Gt(IsInt(stack.Entry(1)), Nat(low))))))))

					case _ => old
				}
			}

		def apply(g: ProgramGraph[CodePoint, Constraint]) = 
			transformSwitches(transformIfs(transformGotos(g)))

		def graphFrom(nodes: Seq[CodePoint]) = {
 			ProgramGraph[CodePoint, Constraint](nodes.map { case cp@CodePoint(i, _, _, _) => (i → cp) }.toMap, 
				(nodes zip nodes.drop(1)).map (tup => (tup._1 → Set((tup._2, stack.Implicit)))).toMap)
		}
	}
}