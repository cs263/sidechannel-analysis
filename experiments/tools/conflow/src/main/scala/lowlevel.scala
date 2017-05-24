
package conflow {
	import conflow.graphs._
	import conflow.Kernel._
	import conflow.constraints._

	object LowLevelEnumeration {
		val endings = Set("if_acmpeq", "if_acmpne", "if_icmpeq", "if_icmpne",
			"if_icmplt", "if_icmpge", "if_icmpgt", "if_icmple", "ifeq", "ifne",
			"iflt", "ifge", "ifgt", "ifle", "ifnull", "ifnonnull", "gogto", "goto_w",
			"lookupswitch", "tableswitch", "return")

		def apply(graph: conflow.graphs.Graph[CodePoint, Constraint]): conflow.graphs.Graph[Int, Constraint] = {
			val worklist = scala.collection.mutable.Stack[Int]()
			val assigned = scala.collection.mutable.Map[Int, Int]()
			val done = scala.collection.mutable.Set[Int]()

			var jump = false
			var currentBlock: Int = 0

			assigned += (0 → currentBlock)
			worklist push 0

			while(!worklist.isEmpty) {
				val current = worklist pop

				if(jump) {
					jump = false
					currentBlock += 1
				}

				if(!done.contains(current)) {
					graph(CodeLocations.absoluteToRelative(current)) match {
						case Node(CodePoint(position, op, _, _), _, outgoing) =>
							if(endings contains op) {	// this is a jump, assign new block
								outgoing.map { case (constraint, index) =>
									if(!assigned.contains(index)) {
										currentBlock += 1
										assigned += (index → currentBlock)
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
				} else {
					jump = true
				}
			}

			val groups = assigned.groupBy { case (k, v) => v }.map { case (k, m) => k → m.keys }

			val blocks = groups.map { case (k, v) =>
				k → v.flatMap { w => graph(CodeLocations.absoluteToRelative(w)) match {
						case Node(CodePoint(position, op, _, _), _, outgoing) =>
							outgoing.map { case (constraint, location) => (constraint, assigned(location)) }.filter { _ != (stack.Implicit, k) }
					} }.filter { _ != List() }.toSeq
			}

			blocks.toSeq.map { case (k: Int, v) => Node(k, Seq(), v) }
		}
	}

	object LowlevelJumps {
		def apply(graph: conflow.graphs.Graph[CodePoint, Constraint]) = {
			graph.zipWithIndex.map { case (node: Node[CodePoint, Constraint], n: Int) => node match {
				case Node(CodePoint(position, "goto", Seq(jump), cp), incoming, outgoing) =>
					Node(CodePoint(position, "goto", Seq(jump), cp), incoming, Seq((stack.Jump, position + jump)))

				case Node(CodePoint(position, "goto_w", Seq(jump), cp), incoming, outgoing) =>
					Node(CodePoint(position, "goto_w", Seq(jump), cp), incoming, Seq((stack.Jump, CodeLocations.relativeToAbsolute(position + jump))))

				case Node(CodePoint(position, op, Seq(jump), cp), incoming, outgoing) if op startsWith "if" =>
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

					val branches = Seq((req.reverse, CodeLocations.relativeToAbsolute(position + 1)), (req, position + jump))
					Node(CodePoint(position, op, Seq(jump), cp), incoming, branches)

				case Node(cp@CodePoint(index, "lookupswitch", args@Seq(default, npairs, pairIndices@_*), _), incoming, outgoing) =>
					val realPairs = for {
						i <- 0 until pairIndices.length by 2
					} yield (pairIndices(i), pairIndices(i + 1))

					val pairs = realPairs.map { case (condition, offset) =>
						(Requires(1, Eq(IsInt(stack.Entry(1)), Nat(condition))), index + offset)
					}

					val diff: Constraint = pairs.map { _._1 }.foldLeft(AllOf(Seq()))((all, el) => all and el.reverse)
					Node(cp, incoming, Seq((diff, index + default)) ++ pairs)

				case Node(cp@CodePoint(index, "tableswitch", args@Seq(default, low, high, offsets@_*), _), incoming, outgoing) =>
					val allPairs = (0 until (high - low + 1) zip offsets).map { case (i, offset) =>
						(Requires(1, Eq(IsInt(stack.Entry(1)), Nat(i))), index + offset)
					} ++ Seq((Requires(1, Eq(IsInt(stack.Entry(1)),
								AllOf(Seq(
									Lt(IsInt(stack.Entry(1)), Nat(low)),
									Gt(IsInt(stack.Entry(1)), Nat(low)))))), index + default))

					Node(cp, incoming, allPairs)
				case whatever =>
					whatever
			}
		} }
	}
}
