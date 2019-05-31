import java.math.BigInteger;

public class F2_mod
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
	public static void main(String args[])
	{
		for(int i=21;i<=31;i++)
		System.out.println("m="+F2x_mod(BigInteger.valueOf(i),BigInteger.valueOf(7)));
	}	
}
