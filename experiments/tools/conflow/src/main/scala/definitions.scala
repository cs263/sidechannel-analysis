
/*

Reading material: 	http://doc.utwente.nl/79610/1/FoVeOOS11-PreProceeding.pdf
					https://www.nada.kth.se/~dilian/Papers/sefm12.pdf

*/

import scala.reflect._

package conflow {
	trait Lang
	trait Expr extends Lang 

	case class C(value: Any) extends Expr
	case class Null() extends Expr
	case class Arith(first: Expr, op: String, second: Expr) extends Expr

	trait Target extends Expr
	case class TVar(index: Int) extends Target
	case class Field(subject: Expr, field: String) extends Target

	trait Local extends Target
	case class LVar(index: Int) extends Local
	case class This() extends Local

	trait Instruction extends Lang
	case class Assignment(target: Target, assign: Expr) extends Instruction
	case class Return(returning: Option[Expr]) extends Instruction
	case class MethodCall(target: Option[Target], subject: Expr, methodName: String, arguments: Seq[Expr]) extends Instruction
	case class NewObject(target: Target, className: String, arguments: Seq[Expr]) extends Instruction

	class Assertion[T <: Throwable](implicit t: ClassTag[T]) extends Instruction {
		def fail =
			throw t.runtimeClass.newInstance.asInstanceOf[Throwable]
	}

	case class AssertNotNull(expr: Expr) extends Assertion[NullPointerException]
	case class AssertNotZero(expr: Expr) extends Assertion[ArithmeticException]
	case class AssertCheckBound(expr: Expr) extends Assertion[IndexOutOfBoundsException]
	case class AssertNotNegative(expr: Expr) extends Assertion[NegativeArraySizeException]
	case class AssertCheckCast(expr: Expr) extends Assertion[ClassCastException]
	case class AssertCheckStore(expr: Expr) extends Assertion[ArrayStoreException]

	case class If(cond: Expr, pc: Int) extends Instruction
	case class Goto(pc: Int) extends Instruction
	case class Throw(throwable: Expr) extends Instruction
	case class MayInit(className: String) extends Instruction
	case class Nop() extends Instruction

	trait JVM
	
	case class $push(c: Any) extends JVM
	case class $dup() extends JVM
	case class $load(i: Any) extends JVM
	case class $add() extends JVM
	case class $nop() extends JVM
	case class $if(p: Int) extends JVM
	case class $goto(p: Int) extends JVM
	case class $return() extends JVM
	case class $vreturn(p: Any) extends JVM
	case class $div() extends JVM
	case class $athrow() extends JVM
	case class $new(c: Any) extends JVM
	case class $store(x: Any) extends JVM
	case class $getfield(f: Any) extends JVM
	case class $putfiled(f: Any) extends JVM
	case class $invokevirtual(ns: Any) extends JVM
	case class $invokespecial(ns: Any) extends JVM


}
