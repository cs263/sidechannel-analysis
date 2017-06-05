
package conflow {
	import conflow.graphs._
	import conflow.Kernel._
	import conflow.constraints._

	object ConstraintsToLogic {
		def apply[T](graph: conflow.graphs.Graph[T, Constraint]) : conflow.graphs.Graph[T, String] = {
			import conflow.constraints._
			import conflow.constraints.stack._

			def stringify(constraint: Constraint): String = {
				val sub = constraint match {
					case Not(e) => s"¬ ${stringify(e)}"
					case Requires(n, c) => s"pop ${n} . ${stringify(c)}"
					case Eq(a, b) => s"${stringify(a)} = ${stringify(b)}"
					case Ne(a, b) => s"${stringify(a)} \\neq ${stringify(b)}"
					case Lt(a, b) => s"${stringify(a)} < ${stringify(b)}"
					case Gt(a, b) => s"${stringify(a)} > ${stringify(b)}"
					case Le(a, b) => s"${stringify(a)} \\leq ${stringify(b)}"
					case Ge(a, b) => s"${stringify(a)} \\geq ${stringify(b)}"
					case IsInt(a) => s"int(${stringify(a)})"
					case IsRef(a) => s"ref(${stringify(a)})"
					case Entry(a) => """\\uparrow_{""" + a + "}"
					case AllOf(es) => "(" + es.map { stringify _ }.mkString(" \\wedge ") + ")"
					case AnyOf(es) => "(" + es.map { stringify _ }.mkString(" \\vee ") + ")"
					case Nat(n) => s"${n}"
					case Var(n) => "v_{" + n + "} \\in Vars"
					case Exception => "exception"
					case Implicit => ""
					case Return => "return"
					case Invoke => "invoke"
					case Jump => "goto"
					case Null => "null"
				}

				sub
			}

			def stringify_pairs(constriants: Seq[(Constraint, Int)]) : Seq[(String, Int)] = {
				constriants.map {
					case (Implicit, n) => ("", n)
					case (c, n) => (stringify(c), n)
				}
			}

			graph.map { case n@Node(cp, in, out) => 
				Node(cp, stringify_pairs(in), stringify_pairs(out))
			}
		}
	}
}
