from expr import Expression, Operation

class MultiplicationVisitor:

	def __init__(self, index):
		self.stop = False
		self.solo = True 
		self.index = index


	def visit(self, expr, loop_var, loop_var_branch, ind):
		if isinstance(expr, Expression.IntConstant):
			if self.solo:
				return Operation.Operation(Operation.Operator.TIMES, [loop_var, expr])
			return expr
		if isinstance(expr, Expression.LoopVariable):
			#Basically need to set a stop here!
			self.stop = True
			return Operation.Operation(Operation.Operator.LOOP, [loop_var, expr])
		if isinstance(expr, Expression.BooleanVariable):
			return loop_var_branch 
		op = expr.getOperator()
		if op == Operation.Operator.ADD:
			self.stop = False
			self.solo = True 
			ops = expr.getOperands()
			t1 = self.visit(ops[0], loop_var, loop_var_branch, ind)
			t2 = self.visit(ops[1], loop_var, loop_var_branch, ind)
			return Operation.Operation(Operation.Operator.ADD, [t1, t2])
		elif op == Operation.Operator.TIMES:
			self.solo = False
			ops = expr.getOperands()
			t1 = self.visit(ops[0], loop_var, loop_var_branch,ind)
			name = "k_" + str(self.index) + "_" + str(ind)
			ind+=1
			t2 = self.visit(ops[1], t1, Expression.LoopVariable(name), ind)
			return Operation.Operation(Operation.Operator.TIMES, [t1, t2])
		elif op == Operation.Operator.SUB:
			return Operation.Operation(Operation.Operator.SUB, [loop_var, loop_var_branch])
		elif op == Operation.Operator.LOOP:
			return Operation.Operation(Operation.Operator.LOOP, [loop_var, expr])
		else:
			print("?")