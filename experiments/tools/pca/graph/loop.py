from node import *

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