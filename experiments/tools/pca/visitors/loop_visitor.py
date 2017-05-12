
class LoopVisitor():
	'''Finds the body of the loop'''
	def __init__(self, target):
		self.path = []
		self.target = target

	def visit(self, node):
		if node in self.path:
			return 
		self.path.append(node)
		if node == self.target:
			return
		for node in node.incoming:
			self.visit(node)

	def getBody(self):
		return self.path
