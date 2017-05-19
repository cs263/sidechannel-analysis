from abc import ABCMeta, abstractmethod

class Expression:
	__metaclass__ = ABCMeta

	@abstractmethod
	def toString(self):
		pass

class IntConstant(Expression):

	def __init__(self, cons):
		self.cons = cons

	def toString(self):
		return str(self.cons)

class LoopVariable(Expression):

	def __init__(self, var_name):
		self.var_name = var_name
		self.is_secret = False

	def setSecret(self):
		self.is_secret =True

	def isSecret(self):
		return self.is_secret

	def toString(self):
		return self.var_name

	def maxValue(self, maxValue):
		self.maxValue = maxValue

	def getMaxValue(self):
		return self.maxValue

class BooleanVariable(Expression):

	def __init__(self, var_name, node_):
		self.var_name = var_name
		self.is_secret = False
		self.node = node_

	def setSecret(self):
		self.is_secret=True

	def isSecret(self):
		return self.is_secret

	def toString(self):
		return self.var_name

	def toNode(self):
		return self.node