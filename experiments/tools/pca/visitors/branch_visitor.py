class BranchVisitor:
	'''Determines which nodes are merges, branches and loops'''

	def __init__(self):
		self.visited = []
		self.loops = []

	def visit(self, node):
		self.visited.append(node)
		id_node = node.getId()
		incoming = node.incoming
		for n in incoming:
			id_n = n.getId()
			if id_n[0] == id_node[0] and id_n[1] > id_node[1]:
				node.loopHead()
				self.loops.append((node, n))
		if len(incoming) > 1 and  not node.isLoopHead():
			node.setMerge()
		outgoing = node.outgoing
		for n in outgoing:
			id_n = n.getId()
			if id_n[0] == id_node[0] and id_n[1] < id_node[1]:
				node.loopTail()
		if len(outgoing) > 1 and not node.isLoopTail():
			node.setBranch()
		for n in outgoing:
			if n not in self.visited:
				self.visit(n)

	def getLoops(self):
		return self.loops

