
package conflow {
	import conflow.graphs._
	import conflow.Kernel._
	import conflow.constraints._

	object G {
		val imaginaryEnd = 1001001
	}

	object BuildBlocks {
		val endings = Set("if_acmpeq", "if_acmpne", "if_icmpeq", "if_icmpne",
			"if_icmplt", "if_icmpge", "if_icmpgt", "if_icmple", "ifeq", "ifne",
			"iflt", "ifge", "ifgt", "ifle", "ifnull", "ifnonnull", "gogto", "goto_w",
			"lookupswitch", "tableswitch", "return", "invokedynamic", "invokestatic",
			"invokeinterface", "invokevirtual", "invokespecial", "areturn", "freturn",
			"lreturn", "dreturn", "ireturn", "return")

		def apply(graph: conflow.graphs.Graph[CodePoint, Constraint]): conflow.graphs.Graph[CodeBlock, Int] = {
			val assigned = scala.collection.mutable.Map[Int, Int]()			

			{
				var jump = false
				var length: Int = 1

				var currentBlock: Int = 0
				val worklist = scala.collection.mutable.Stack[Int]()
				val done = scala.collection.mutable.Set[Int]()

				assigned += (0 → currentBlock)
				worklist push 0

				while(!worklist.isEmpty) {
					val current = worklist pop

					if(jump) {
						jump = false
						currentBlock += 1
					}

					length += 1

					if(!done.contains(current)) {
						if(current != G.imaginaryEnd) {
							val location = CodeLocations.absoluteToRelative(current)
							if(location != G.imaginaryEnd) 
								graph(location) match {
									case Node(CodePoint(position, op, _, _), _, outgoing) =>
										if(endings contains op) {	// this is a jump, assign new block
											outgoing.map { case (constraint, index) =>
												if(!assigned.contains(index)) {
													currentBlock += 1
													assigned += (index → currentBlock)
													length = 1

													worklist push index
												}
											}
										} else { // this is not a jump, use this block to mark next
											outgoing.map { case (constraint, index) =>
												if(!assigned.contains(index)) {
													val key = assigned(current)
													assigned += (index → key)

													worklist push index
												}
											}
										}
								}

							done.add(current)
						}
					} else {
						jump = true
					}
				}
			}

			val groups = assigned.groupBy { case (k, v) => v }.map { case (block, m) => block → m.keys }

			val annotated: conflow.graphs.Graph[(CodePoint, Int), Constraint] = graph.map {
				case Node(cp@CodePoint(position, _, _, _), incoming, outgoing) => 
					Node((cp, assigned(position)), incoming, outgoing)
			}

			val blocks = scala.collection.mutable.Map[Int, Seq[CodePoint]]()
			val block_connections = scala.collection.mutable.Map[Int, Set[Int]]()
			val inverse_connections = scala.collection.mutable.Map[Int, Set[Int]]()

			groups.map { case (k, v) =>
				blocks += (k → Seq())
				block_connections += (k → Set())
				inverse_connections += (k → Set())
			}

			{
				val worklist = scala.collection.mutable.Stack[Int]()
				val done = scala.collection.mutable.Set[Int]()

				worklist push 0

				while(!worklist.isEmpty) {
					val current: Int = worklist pop

					annotated(current) match {
						case Node(cp@(CodePoint(position, _, _, _), block_index), incoming, outgoing) if !(done.contains(position)) =>
							done.add(position)

							val block_contents: Seq[CodePoint] = blocks(block_index)
							blocks(block_index) = block_contents ++ Seq(cp._1)

							val outgoing_blocks = outgoing.map {
								case (constraint, address) => 
									if(address != G.imaginaryEnd) {
										val loc = CodeLocations.absoluteToRelative(address)
										worklist push loc
									}
									(constraint, assigned(address))
							} // (constraint, block) instead of (constraint, index)

							val to_others = outgoing_blocks.filter { _._2 != block_index }.map { _._2 } 

							val connecting = !to_others.isEmpty

							if(connecting) {
								val connections = block_connections(block_index)

								block_connections(block_index) = connections ++ to_others.toSet
							}
						case already_done =>
					}
				}				
			}

			block_connections.map(_.swap).map { case (keys, value) => 
				keys.map { key =>
					inverse_connections(key) += value
				}
			}

			val output = blocks.map { case (k, v) =>
				val outgoing_with_costs = block_connections(k).map { value =>
					(v.size, value)
				}.toSeq

				val incoming_with_costs = inverse_connections(k).map { index =>
					(blocks(index).size, index)
				}.toSeq

				val len = v.size
				Node(CodeBlock(k, v), incoming_with_costs, outgoing_with_costs)
			}

			output.toSeq
		}
	}

	object GetLowlevelJumps {
		def apply(graphx: conflow.graphs.Graph[CodePoint, Constraint]) = {
			val graph = graphx :+ Node(CodePoint(G.imaginaryEnd, "end", Seq(), None), Seq(), Seq())

			def makeJumps(node: Node[CodePoint, Constraint]): Seq[(Constraint, Int)] = node match {
				case Node(cp@CodePoint(position, op, args, cpe), incoming, outgoing) if op endsWith "return" => 
					Seq((stack.Return, G.imaginaryEnd))

				case Node(cp@CodePoint(position, op, args, cpe), incoming, outgoing) if op startsWith "invoke" =>
					val next = CodeLocations.ref(position, 1)	
					Seq((stack.Invoke, next))

				case Node(cpe@CodePoint(position, "goto", Seq(jump), cp), incoming, outgoing) =>
					Seq((stack.Jump, position + jump))

				case Node(cpe@CodePoint(position, "goto_w", Seq(jump), cp), incoming, outgoing) =>
					Seq((stack.Jump, CodeLocations.relativeToAbsolute(position + jump)))

				case Node(cpe@CodePoint(position, op, Seq(jump), cp), incoming, outgoing) if op startsWith "if" =>
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

					val branches = Seq((req.reverse, CodeLocations.ref(position, 1)), (req, position + jump))
					branches

				case Node(cp@CodePoint(index, "lookupswitch", args@Seq(default, npairs, pairIndices@_*), _), incoming, outgoing) =>
					val realPairs = for {
						i <- 0 until pairIndices.length by 2
					} yield (pairIndices(i), pairIndices(i + 1))

					val pairs = realPairs.map { case (condition, offset) =>
						(Requires(1, Eq(IsInt(stack.Entry(1)), Nat(condition))), index + offset)
					}

					val diff: Constraint = pairs.map { _._1 }.foldLeft(AllOf(Seq()))((all, el) => all and el.reverse)
					Seq((diff, index + default)) ++ pairs

				case Node(cp@CodePoint(index, "tableswitch", args@Seq(default, low, high, offsets@_*), _), incoming, outgoing) =>
					val allPairs = (0 until (high - low + 1) zip offsets).map { case (i, offset) =>
						(Requires(1, Eq(IsInt(stack.Entry(1)), Nat(i))), index + offset)
					} ++ Seq((Requires(1, Eq(IsInt(stack.Entry(1)),
								AllOf(Seq(
									Lt(IsInt(stack.Entry(1)), Nat(low)),
									Gt(IsInt(stack.Entry(1)), Nat(low)))))), index + default))

					allPairs
				case Node(cp@CodePoint(id, _, _, _), in, Seq()) if id != G.imaginaryEnd =>
					Seq((stack.Return, G.imaginaryEnd))
				case n@Node(cp, in, out) =>
					out
			}

			graph.zipWithIndex.map { case (node: Node[CodePoint, Constraint], n: Int) => 
				node match {
					case Node(cp, in, out) =>
						Node(cp, in, out.filter { _._1 != stack.Implicit } ++ makeJumps(node))
				}
		} }
	}
}
