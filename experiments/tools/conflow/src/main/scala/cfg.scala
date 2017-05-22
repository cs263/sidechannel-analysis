
import conflow._
import conflow.Kernel._
import conflow.graphs._
import conflow.constraints._

/*
case class BasicBlock(incoming: Seq[Int], outgoing: Seq[Int], isOutputNode: Boolean)

object ControlFlowGraph {
	def apply(program: ProgramGraph[Constraint, CodePoint]): ProgramGraph[Int, BasicBlock] = {
		var workList = Seq[CodePoint]()
		if(!program.nodes.isEmpty) {
			workList = workList :+ program.nodes(program.nodes.keys.toSeq.sorted.head)
		}
		ProgramGraph[Int, BasicBlock](Map(), Map())
	}
}
*/
