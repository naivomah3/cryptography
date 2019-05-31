import java.math.BigInteger;
import java.util.Random;

class legendre
{
	//Compute the Legendre symbol(a/p)
	//Algorithm 11.19 Handbook of Elliptic and HyperElliptic Curve Cryptography
	//p is an odd prime
	public static int Legend(BigInteger a,BigInteger p)
	{
		int k=1;
		while(p.compareTo(BigInteger.ONE)!=0)
		{
			if (a.compareTo(BigInteger.ZERO)==0)
				return 0;
			BigInteger v = BigInteger.valueOf(0);
			while (a.mod(BigInteger.valueOf(2)).intValue()==0)
			{
				v = v.add(BigInteger.ONE);
				a = a.divide(BigInteger.valueOf(2));
			}
			
			if ((v.mod(BigInteger.valueOf(2)).intValue()==1)&&((p.mod(BigInteger.valueOf(8)).intValue()==3)))
				k = k*-1;
			if ((v.mod(BigInteger.valueOf(2)).intValue()==1)&&((p.mod(BigInteger.valueOf(8)).intValue()==5)))
				k = k*-1;
			if ((a.mod(BigInteger.valueOf(4)).intValue()==3)&&((p.mod(BigInteger.valueOf(4)).intValue()==3)))
				k = k*-1;
			BigInteger r = a;
			a=p.mod(r);
			p=r;
		}
		return k;
	}
	//Compute x=sqrt(a) (mod p)
	//Based on Algorithm 11.23 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	//p is a prime and a in an integer such that (a/p)=1
	
	public static BigInteger TonelliShanks(BigInteger a,BigInteger p)
	{
		if (Legend(a, p)==-1) // TODO Find a better solution to this
			throw new RuntimeException ("private BigInteger  TonelliShanks(BigInteger a, BigInteger p): Legendre(a, p)==-1!");

		BigInteger r = p.subtract(BigInteger.ONE);
		BigInteger e = BigInteger.valueOf(0);
		while (r.mod(BigInteger.valueOf(2)).intValue()==0) {
			r = r.divide(BigInteger.valueOf(2));
			e = e.add(BigInteger.ONE);
		}
		
		BigInteger n;
		Random rndSrc=new Random();
		do{
			n=(new BigInteger(p.bitLength()-1,rndSrc)).mod(p);
		}while(Legend(n,p)!=-1);
		
		BigInteger z=n.modPow(r,p);
		BigInteger y=z;
		BigInteger s=e;
		BigInteger r1=(r.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(2));
		BigInteger x=a.modPow(r1,p);
		BigInteger b=a.multiply(x.pow(2)).mod(p);
		x=a.multiply(x).mod(p);
		
		while (b.mod(p).intValue()!=1)
		{
			BigInteger m=BigInteger.ONE;
			while (b.modPow(BigInteger.valueOf(2).pow(m.intValue()),p).intValue()!=1)
				m = m.add(BigInteger.ONE);
			BigInteger t=y.modPow(BigInteger.valueOf(2).pow(s.intValue()-m.intValue()-1),p);
			y=t.modPow(BigInteger.valueOf(2),p);
			s=m;
			x=t.multiply(x).mod(p);
			b=y.multiply(b).mod(p);
			System.out.println(m.toString(10)+" "+y.toString(10)+" "+x.toString(10)+" "+b.toString(10)+" "+t.toString(10));		}
		
		return x;
	}
	public static void main(String args[])
	{
		System.out.println("L="+TonelliShanks(BigInteger.valueOf(109608),BigInteger.valueOf(163841)));
	}	
}
