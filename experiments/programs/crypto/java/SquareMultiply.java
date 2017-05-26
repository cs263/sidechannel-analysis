package experiments.programs.crypto.java;

//Implementation of the square and multiply algorithm for modular exponeniation .*.
import java.math.*;

public class SquareMultiply {
	public static BigInteger squareMultiply(final BigInteger base, final BigInteger exponent, final BigInteger modulus) {
		BigInteger s = BigInteger.valueOf(1L);
		int width = exponent.bitLength();
		for (int i = 0; i < width; i++){
			s = s.multiply(s).mod(modulus);
			if (exponent.testBit(width -i - 1)){
				s = Multiplier.fastMultiply(s, base).mod(modulus);
			}
		}
		return s; 
	}
}

