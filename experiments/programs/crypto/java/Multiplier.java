package experiments.programs.crypto.java;

//Implementation of optimized multiplication 
import java.math.*;

public class Multiplier {
	public static BigInteger normalMultiply(final BigInteger x, final BigInteger y){
		BigInteger res = BigInteger.ZERO;
		for (int i = 0; i < y.bitLength(); i++){
			if (y.testBit(i)){
				res = res.add(x.shiftLeft(i));
			}
		}
		return res; 
	}


	public static BigInteger fastMultiply(final BigInteger x, final BigInteger y){
		if (x.equals(BigInteger.ONE)){
			return y;
		}
		if (y.equals(BigInteger.ONE)){
			return x;
		}
		int xLen = x.bitLength();
		int yLen = y.bitLength();
		int N = Math.max(xLen, yLen);
		BigInteger res = BigInteger.ZERO;
		if (N <= 800){
			res = x.multiply(y); 
		}
		else if (Math.abs(xLen - yLen) >= 32){
			res = normalMultiply(x,y);
		}
		else{
			N = N/2 + N %2; 
			BigInteger b = x.shiftRight(N);
			BigInteger a = x.subtract(b.shiftLeft(N));
			BigInteger d = y.shiftRight(N);
			BigInteger c = y.subtract(d.shiftLeft(N));
			BigInteger ac = fastMultiply(a, c);
			BigInteger bd = fastMultiply(b, d);
			BigInteger cross = fastMultiply(a.add(b), c.add(d));
			res = ac.add(cross.subtract(ac).subtract(bd).shiftLeft(N)).add(bd.shiftLeft(2*N));
		}
		return res; 
	}
}