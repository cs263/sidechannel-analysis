from node import *
from expr import Expression, Operation



class loop:
	def __init__(self, start, end, index):
		self.nodes = set()
		self.start = start
		self.end = end
		self.nested_loops = []
		self.cost = Expression.IntConstant(0)
		self.annotated = False
		self.index = index
		self.sub_index = 0 

	def toString(self):
		return (self.start.getId(), self.end.getId())

	def getId(self):
		return (self.start, self.end)

	def setBody(self, body):
		self.nodes = body 

	def body(self):
		return self.nodes 

	def addNestedLoop(self, nested):
		self.nested_loops.append(nested)

	def nestedLoops(self):
		return self.nested_loops

	def setCost(self, cost):
		self.cost = cost
		self.annotated = True 

	def getCost(self):
		return self.cost

	def isAnnotated(self):
		return self.annotated

	def getIndex(self):
		return self.index

	def updateOp(self, expr_, loop_var, loop_var_branch):
		if isinstance(expr_, Expression.BooleanVariable):
			return loop_var_branch 
		elif isinstance(expr_, Expression.LoopVariable):
			print("nested loops are not yest supported")
			return 
		else:
			op = expr_.getOperator()
			if op == Operation.Operator.SUB:
				return Operation.Operation(Operation.Operator.SUB, [loop_var, loop_var_branch])
			else:
				print("???")


	def updateLeaves(self, expr1, loop_var, loop_var_branch, ind):
		if isinstance(expr1, Expression.IntConstant):
			return Operation.Operation(Operation.Operator.TIMES, [loop_var, expr1])
		else:
			#print(expr1.toString())
			op = expr1.getOperator()
			if op == Operation.Operator.TIMES:
				ops = expr1.getOperands()
				t1 = self.updateOp(ops[0], loop_var, loop_var_branch)
				name = str(self.index) + "_" + str(ind)
				ind+=1
				t2 = self.updateLeaves(ops[1], t1, Expression.LoopVariable(name))
				return Operation.Operation(Operation.Operator.TIMES, [t1, t2])
			elif op == Operation.Operator.PLUS:
				ops = expr1.getOperands()
				t1 = self.updateLeaves(ops[0], loop_var, loop_var_branch)
				t2 = self.updateLeaves(ops[1], loop_var, loop_var_branch)
				return Operation.Operation(Operation.Operator.PLUS, [t1, t2])
			else:
				print("?????????")

	def updateWeight(self):
		last_cost = self.end.getCost()
		name = "k_" +str(self.index)
		ind = 0 
		name_branch = name + "_" +str(ind)
		ind+=1
		loop_var = Operation.LoopVariable(name)
		loop_var_branch = Operation.LoopVariable(name_branch)
		cost_loop = self.updateLeaves(last_cost, loop_var, loop_var_branch, ind)
		self.setCost(cost_loop)
