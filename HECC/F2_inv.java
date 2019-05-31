import java.math.BigInteger;

public class F2_inv
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
	
	//Addition in F2m
	//z=x+y
	protected static BigInteger F2x_add(BigInteger x,BigInteger y)
	{
		return x.xor(y);
	}
	
	//y=pow(x,-1)(mod modulus) in F2m
	//Baesd on Algorithm 2.48 of Guide to Elliptic Curve Cryptography
	protected static BigInteger F2x_inv(BigInteger x,BigInteger modulus)
	{
		BigInteger u= F2x_mod(x,modulus);
		BigInteger v=modulus;
		BigInteger g1=BigInteger.valueOf(1);
		BigInteger g2=BigInteger.valueOf(0);
		int j;
		BigInteger temp;
		while(u.bitLength()>1)
		{
			j=u.bitLength()-v.bitLength();
			if(j<0)
			{
				temp=u;
				u=v;
				v=temp;
				
				temp=g1;
				g1=g2;
				g2=temp;
				
				j=-j;
			}
			u=F2x_add(u,v.shiftLeft(j));
			g1=F2x_add(g1,g2.shiftLeft(j));
		}
		return g1;
	}
	
	
	public static void main(String args[])
	{
		System.out.println("g="+F2x_inv(BigInteger.valueOf(371),BigInteger.valueOf(2053)));
	}	
}
