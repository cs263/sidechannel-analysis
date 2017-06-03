import sys
import numpy as np 
import operator

#Very hacked code to get quick results. Just pipe the output of your run java V=.... run > file1 and run python analyzer.py file1. 
results = {}

def update_res(num, word):
	k = word[0]
	if k in results:
		results[k].append(int(num))
	else:
		results[k] = [int(num)]

def process():
	for key in results.keys():
		results[key] = np.mean(results[key])

if __name__ == "__main__":
	input_file = sys.argv[1]
	for i,line in enumerate(open(input_file)):
		if i < 3:
			if i == 1:
				print("first letter is " + line[0])
			continue
		in_out = line.split()
		if in_out:
			num = in_out[0].strip(',')
			word = in_out[1]
			update_res(num, word)
	process()
	sorted_res = sorted(results.items(), key=operator.itemgetter(1), reverse =True)
	print(sorted_res)