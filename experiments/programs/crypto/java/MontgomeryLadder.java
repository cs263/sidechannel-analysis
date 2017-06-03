package experiments.programs.crypto.java;

//Implementation of the Montgomery Powering Ladder for modular exponentiation 
//See this paper -- https://link.springer.com/chapter/10.1007/3-540-36400-5_22 and this link https://tlseminar.github.io/timing-attacks/
import java.math.*;

public class MontgomeryLadder {
	public static BigInteger ladder(BigInteger base, BigInteger exponent, BigInteger modulus) {
		BigInteger r0 = BigInteger.valueOf(1);
		BigInteger r1 = base;
		int width = exponent.bitLength();
		for (int i = 0; i < width; i++){
			if (exponent.testBit(width - i - 1)){
				r0 = Multiplier.fastMultiply(r0, r1).mod(modulus);
				r1 = r1.multiply(r1).mod(modulus);
			}
			else {
				r1 = Multiplier.fastMultiply(r0, r1).mod(modulus);
				r0 = r0.multiply(r0).mod(modulus);
			}
		}
		return r0; 
	}
}