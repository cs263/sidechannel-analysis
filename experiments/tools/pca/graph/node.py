from expr import Expression

class node:
	def __init__(self, identity):
		self.identity = identity
		self.outgoing = []
		self.incoming = [] 
		self.back_edge = []
		self.loop_head = (False, None)
		self.loop_tail = (False, None)
		self.cost = Expression.IntConstant(0)
		self.output = False
		self.branch = False
		self.merge = False
		self.variable = None
	
	def getId(self):
		return self.identity

	def updateIncoming(self, incoming):
		self.incoming.append(incoming)

	def updateOutgoing(self, outgoing):
		self.outgoing.append(outgoing) 

	def setLength(self, length):
		self.length = length

	def setCost(self, cost):
		self.cost = weight

	def getCost(self):
		return self.cost

	def setOutput(self):
		self.output = True

	def isOutput(self):
		return self.output

	def loopHead(self):
		self.loop_head = (True, )

	def isLoopHead(self):
		return self.loop_head[0]

	def loopTail(self):
		self.loop_tail = (True, )

	def isLoopTail(self):
		return self.loop_tail[0]

	def markBackEdges(self, back_edge):
		self.back_edge = back_edge

	def getBackEdges(self):
		return self.back_edge

	def setMerge(self):
		self.merge = True

	def isMerge(self):
		return self.merge

	def setBranch(self):
		self.branch = True

	def isBranch(self):
		return self.branch 

	def setVariable(self, var):
		self.variable = var

	def getVariable(self):
		return self.variable
