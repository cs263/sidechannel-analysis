
from expr import Expression, Operation

def updateLeaves(expr1, expr_int):
	if isinstance(expr1, Expression.IntConstant):
		return Expression.IntConstant(expr1.cons+expr_int)
	elif isinstance(expr1, Expression.BooleanVariable):
		return expr1
	elif isinstance(expr1, Expression.LoopVariable):
		return expr1
	else:
		#print(expr1.toString())
		op = expr1.getOperator()
		if op == Operation.Operator.SUB:
			return expr1
		ops = expr1.getOperands()
		t1 = updateLeaves(ops[0], expr_int)
		t2 = updateLeaves(ops[1], expr_int)
		return Operation.Operation(op, [t1,t2])

def updateWeight(expr_old, expr_new):
	#check if it's just an int type
	if isinstance(expr_old, Expression.IntConstant):
		return Expression.IntConstant(expr_old.cons + expr_new)
	else:
		return updateLeaves(expr_old, expr_new) 


class AcyclicVisitor():

	def __init__(self, target, last):
		self.visited = []
		self.index = 0 
		self.target = target
		self.last = last

	def getIncoming(self, node):
		end = None
		if node.isLoopTail() and node!=self.last:
			l = node.getLoop()
			start, end = l.getId()
			return [start]
		if node.isLoopHead():
			l = node.getLoop()
			start, end = l.getId()
		incoming_neighbors = []
		for n in node.incoming:
			if n != end:
				incoming_neighbors.append(n)
		return incoming_neighbors

	def getCostofNode(self, node):
		if not node.isLoopHead() or node == self.target:
			return node.getCost()
		l = node.getLoop()
		cost_loop = l.getCost()
		return Operation.Operation(Operation.Operator.ADD, [cost_loop, node.getCost()])

	def getOutgoing(self, node):
		outgoing_neighbors = []
		for n in node.outgoing:
			if n != self.target:
				if n.isLoopHead():
					l = n.getLoop()
					start, end = l.getId()
					if end not in self.visited:
						outgoing_neighbors.append(end)
				else:
					if n not in self.visited:
						outgoing_neighbors.append(n)
		return outgoing_neighbors

	def visit(self, node):
		print(node.getId())
		incoming_neighbors = self.getIncoming(node)
		self.visited.append(node)
		if len(incoming_neighbors) == 0 or node == self.target:
			w = Expression.IntConstant(node.length)
			node.setCost(w)
		elif len(incoming_neighbors) == 1:
			if (incoming_neighbors[0]) not in self.visited:
				self.visit(incoming_neighbors[0])
			weight = updateWeight(self.getCostofNode(incoming_neighbors[0]), node.length)
			node.setCost(weight)
		elif len(incoming_neighbors) == 2:
			if incoming_neighbors[0] not in self.visited:
				self.visit(incoming_neighbors[0]) 
			if incoming_neighbors[1] not in self.visited:
				self.visit(incoming_neighbors[1]) 
			var = node.getVariable()
			if not var:
				var = Expression.BooleanVariable("b_" +str(self.index), node)
				self.index+=1
				node.setVariable(var)
			
			b1 = self.getCostofNode(incoming_neighbors[0])
			term1 = Operation.Operation(Operation.Operator.TIMES, [var, b1])

			b2 = self.getCostofNode(incoming_neighbors[1])

			var_ = Operation.Operation(Operation.Operator.SUB, [Expression.IntConstant(1), var])
			term2 = Operation.Operation(Operation.Operator.TIMES, [var_, b2])
			weight = Operation.Operation(Operation.Operator.ADD, [term1, term2])
			node.setCost(weight)
		else:
			print("??????")
		print(node.getId())
		print(node.getCost().toString())

		def getFinalWeight(self):
			cost_last = self.last.getCost()
			return cost_last
