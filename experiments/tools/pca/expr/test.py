from Expression import *
from Operation import *


if __name__ == "__main__":
	a1 = IntConstant(1)
	a2 = IntConstant(3)
	b1 = BooleanVariable("k")
	b2 = IntConstant(3)
	b = Operation(Operator.TIMES, [b1, b2])
	a = Operation(Operator.ADD, [a1, b])
	print(a.toString())