from Expression import *

class Operator:

	def __init__(self, rep, max_arity):
		self.rep = rep
		self.max_arity = max_arity

	def toString(self):
		return self.rep

Operator.ADD = Operator("+", 2)
Operator.SUB = Operator("-", 2)
Operator.TIMES = Operator("*", 2)
Operator.EQ = Operator("==", 2)
Operator.NEQ = Operator("!=", 2)
Operator.LOOP = Operator("#", 2)


class Operation(Expression):

	def __init__(self,  op, operands):
		self.operator = op
		self.operands = operands

	def getOperator(self):
		return self.operator

	def getOperands(self):
		return self.operands

	def toString(self):
		ops = self.operands
		res = "(" + ops[0].toString() + " " + self.operator.toString() + " " + ops[1].toString() + ")"
		'''res = "( " + self.operator.toString() + " "
		for op in self.operands:
			res += op.toString() + " "
		res += ")"'''
		return res
