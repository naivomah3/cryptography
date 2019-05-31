import java.math.BigInteger;
import java.util.Random;
class jacobi
{
/**
	 * Compute the Jacobi symbol = (a/p)
	 * Based on Algorithm 2.149 in the Handbook of Applied Cryptography
	 */
	private static int jacobi1(BigInteger a, BigInteger n) {
		if (a.compareTo(BigInteger.ZERO)==0)
			return 0;
		if (a.compareTo(BigInteger.ONE)==0)
			return 1;
		
		BigInteger a1 = a;
		BigInteger e = BigInteger.valueOf(0);
		while (a1.mod(BigInteger.valueOf(2)).intValue()==0) {
			a1 = a1.divide(BigInteger.valueOf(2));
			e = e.add(BigInteger.ONE);
		}

		int s = 0;
		if (e.mod(BigInteger.valueOf(2)).compareTo(BigInteger.valueOf(0))==0)
			s = 1;
		else if (n.mod(BigInteger.valueOf(8)).intValue()==1)
			s = 1;
		else if (n.mod(BigInteger.valueOf(8)).intValue()==7)
			s = 1;
		else if (n.mod(BigInteger.valueOf(8)).intValue()==3)
			s = -1;
		else if (n.mod(BigInteger.valueOf(8)).intValue()==5)
			s = -1;
			
		if ((n.mod(BigInteger.valueOf(4)).intValue()==3)&&((a1.mod(BigInteger.valueOf(4)).intValue()==3)))
			s = s*-1;

		BigInteger n1 = n.mod(a1);
		return s*jacobi1(n1, a1);
	}
	
	/**
	 * Compute the Legendre symbol = (a/p)
	 * Based on Algorithm 2.149 in the Handbook of Applied Cryptography
	 */
	public static int legendre(BigInteger a,BigInteger p) {
		return jacobi1(a, p);
	}
	public static void main(String args[])
	{
		System.out.println("L="+legendre(BigInteger.valueOf(158),BigInteger.valueOf(235)));
	}
}	
