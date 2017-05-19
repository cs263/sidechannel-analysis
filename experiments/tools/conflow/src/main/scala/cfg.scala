
import conflow._
import conflow.Kernel._
import conflow.graphs._
import conflow.constraints._

case class BasicBlock(incoming: Seq[Int], outgoing: Seq[Int], isOutputNode: Boolean)

trait ControlFlowGraph extends ProgramGraph[BasicBlock, Int]

object ControlFlowGraph {
	def apply(program: ProgramGraph[Constraint, CodePoint]): ControlFlowGraph = {
	}
}