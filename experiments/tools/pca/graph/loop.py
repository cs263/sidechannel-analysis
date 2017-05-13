from node import *
from expr import Expression


class loop:
	def __init__(self, start, end):
		self.nodes = set()
		self.start = start
		self.end = end
		self.nested_loops = []
		self.cost = Expression.IntConstant(0)

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

	def getCost(self):
		return self.cost
