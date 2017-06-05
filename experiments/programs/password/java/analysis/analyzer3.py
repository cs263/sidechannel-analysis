import sys
import numpy as np 
import operator
import numpy as np
import matplotlib.pyplot as plt 

#Very hacked code to get quick results. Just pipe the output of your run java V=.... run > file1 and run python analyzer.py file1. 
results = {}
secret = None
secret_old = None

def update_res(num, word):
	k = word[0]
	if k == secret:

		if word[1] == secret2:
		
			if "both" in results:
				results["both"].append(int(num))
			else:
				results["both"] = [int(num)]
			return  

	if k in results:
		results[k].append(int(num))
	else:
		results[k] = [int(num)]

def plot():
	x = []
	y = []
	for key in results.keys():
		if key!=secret:
			y.append(results[key])
			x.append(1)

	plt.scatter(x,y,c='blue')
	plt.scatter(1,results[secret],c='red')
	plt.title("First letter not second")
	plt.show()

def process():
	for key in results.keys():
		if (len(results[key]) < 100):
			del results[key]
			continue
		results[key] = np.mean(results[key])

if __name__ == "__main__":
	input_file = sys.argv[1]
	for i,line in enumerate(open(input_file)):
		if i < 3:
			if i == 1:
				print("first letter is " + line[0])
				print("second letter is " + line[1])
				secret = line[0]
				secret2 = line[1]
			continue
		if i < 100000:
			continue
		in_out = line.split()
		if in_out:
			num = in_out[0].strip(',')
			word = in_out[1]
			update_res(num, word)
	process()
	sorted_res = sorted(results.items(), key=operator.itemgetter(1), reverse =True)
	print(sorted_res)
	plot()