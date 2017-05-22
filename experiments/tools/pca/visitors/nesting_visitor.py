
class NestingVisitor():
	'''Finds the nested loops'''
	def __init__(self, loop):
		self.loop = loop
		self.path = []
		self.target = loop.getId()[0]
		self.start = loop.getId()[1]

	def visit(self, node):

		if node in self.path:
			return 
		if node == self.target:
			return
		self.path.append(node)
		
		if node.isLoopTail() and node != self.start:
			#make a new nesting visitor
			loop = node.getLoop()
			visitor = NestingVisitor(loop)
			tail_node = loop.getId()[1]
			self.visit(node)
			self.loop.addNestedLoop(loop)
			self.visit(tail_node)

		else:
			for n in node.incoming:
				self.visit(n)