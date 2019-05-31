import java.math.BigInteger;

public class F2_mul
{
	private static BigInteger F2x_mod(BigInteger a,BigInteger modulus) throws ArithmeticException
	{
		BigInteger r=BigInteger.valueOf(0);
		
		if(modulus.compareTo(BigInteger.valueOf(0))==0)
			throw new ArithmeticException("F2m.F2x_mod: modulus is zero");
		if(a.compareTo(modulus)<0)
			return a;
		else if(a.compareTo(modulus)==0)
			return r;
		
		r=a;
		while(r.bitLength()>=modulus.bitLength())
		{
			r=r.xor(modulus.shiftLeft(r.bitLength()-modulus.bitLength()));
		}
		return r;
		
	}
	//Multiplication in F2m
	//z=x*y
	//Baesd on Algorithm 11.34 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	protected static BigInteger F2x_mul(BigInteger x,BigInteger y)
	{
		BigInteger z=BigInteger.valueOf(0);
		for(int i=0;i<x.bitLength();i++)
		{
			if(x.testBit(i))
				z=z.xor(y);
			y=y.shiftLeft(1);
		}
		return z;
	}
	
	public static void main(String args[])
	{
		System.out.println("m="+F2x_mul(BigInteger.valueOf(54),BigInteger.valueOf(1785)));
	}	
}
