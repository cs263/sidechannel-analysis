from graph import node, loop
from visitors import branch_visitor, loop_visitor, nesting_visitor, acyclic_visitor
from visitors.expr_visitors import multiplication_visitor
from expr import Operation

class cfg:
	def __init__(self):
		self.origin = None
		self.nodes = []
		self.loops = []
		self.output_nodes = []

	def readFromFile(self, filename):
		def updateEdges(_from, _to):
			_from.updateOutgoing(_to)
			_to.updateIncoming(_from)

		def getNodeById(node_id):
			if node_id in node_id_to_node:
				return node_id_to_node[node_id]
			else:
				n = node.node(node_id)
				node_id_to_node[node_id] = n 
				return n 

		node_id_to_node = {}
		node_num_id = {}
		for line in open(filename):
			inputs = line.split('\t')
			method = inputs[0]
			position = int(inputs[1])
			length = int(inputs[2])
			if not (method, position) in node_num_id:
				node_num_id[(method, position)] = 0 
				num = 0 
			else:
				num = node_num_id[(method, position)]+1
				node_num_id[(method, position)]+=1
			current_identity = (method, position, num)
			current = getNodeById(current_identity)
			if self.origin == None:
				self.origin = current
			self.nodes.append(current)
			current.setLength(length)
			if len(self.nodes) > 1:
				if method != past.identity[0]:
					updateEdges(past, current)
			if inputs[4][0] == "o":
				current.setOutput()
				self.output_nodes.append(current)
				target_id = (method, position+length, num)
				target = getNodeById(target_id)
				updateEdges(current, target)
			elif inputs[4][0]!='f':
				for neighbor in inputs[4].split():
					n = neighbor.strip('[],')
					if n != '':
						target_id = (method, int(n), num)
						target = getNodeById(target_id)
						updateEdges(current, target)
			past = current

	def markBranching(self):
		visitor = branch_visitor.BranchVisitor()
		visitor.visit(self.origin)
		loops = visitor.getLoops()
		index = 0 
		for l in loops:
			start = l[0]
			tail = l[1]
			loop_ = loop.loop(start, tail, index)
			index += 1 
			start.setLoop(loop_)
			tail.setLoop(loop_)
			self.findBody(loop_)
			self.loops.append(loop_)
		for l in self.loops:
			if not l.nestedLoops():
				visitor = nesting_visitor.NestingVisitor(l)
				visitor.visit(l.getId()[1])
		

	def findBody(self, l):
		start, end = l.getId()
		visitor = loop_visitor.LoopVisitor(start)
		visitor.visit(end)
		body = visitor.getBody()
		l.setBody(body)

	def annotateLoop(self, l):
		def updateWeight(l):
			start, end = l.getId()
			c = end.getCost()
			index = l.getIndex()
			name = "k_" +str(index)
			ind = 0 
			name_branch = name + "_" +str(ind)
			ind+=1
			loop_var = Operation.LoopVariable(name)
			loop_var_branch = Operation.LoopVariable(name_branch)
			visitor = multiplication_visitor.MultiplicationVisitor(index)
			res = visitor.visit(c, loop_var, loop_var_branch, ind)
			print("Cost of the loop is ")
			print(res.toString())
			l.setCost(res) 


		for ll in l.nestedLoops():
			self.annotateLoop(ll)
		s, e = l.getId()
		visitor = acyclic_visitor.AcyclicVisitor(s, e)
		print("visiting!")
		visitor.visit(e)
		print("done visiting")
		updateWeight(l)






if __name__ == "__main__":
    graph = cfg()
    graph.readFromFile("input/Cat6test.txt")
    graph.markBranching()
    for l in graph.loops:
    	print(l.toString())
    graph.annotateLoop(graph.loops[0])

    
