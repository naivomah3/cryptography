/*Multi-precision Prime characteristic Finite Field class.
Finite Field elements are represented as integers modulo a prime
integer (Z/pZ)
*/

package HECC.FiniteField;

import java.math.BigInteger;
import java.util.Random;

public class Fp extends Fq
{
	//Finite Field Additive Identity Element
	static public Fp O;
	
	//Finite Field Multiplicative Identity Element
	static public Fp I;
	
	static
	{
		O=new Fp();
		I=new Fp();
		I.val=BigInteger.valueOf(1);
	}
	
	public Fp()
	{
		super();
	}
	
	public Fp(BigInteger val)
	{
		this.val=val.mod(modulus);
	}

	public Fp(String strVal,int radix)
	{
		val=(new BigInteger(strVal,radix)).mod(modulus);
	}
	
	public Fp(Random rndSrc) {
		val = (new BigInteger(modulus.bitLength()-1, rndSrc)).mod(modulus);
	}

	public static void setModulus(BigInteger m)
	{
		modulus=m;
	}
	
	// Compute z=x+y (mod modulus)
	public Fq add(Fq y)
	{
		return new Fp(val.add(y.val).mod(modulus));
	}
	
	//Compute y=pow(x,-1) mod modulus
	public Fq inverse()
	{
		return new Fp(val.modInverse(modulus));
	}
	
	//Compute z=x*y (mod modulus)
	public Fq mul(Fq y)
	{
		return new Fp(val.multiply(y.val).mod(modulus));
	}
	
	//Compute y=-x (mod modulus)
	public Fq negate()
	{
		return new Fp(O.val.subtract(val).mod(modulus));
	}
	
	//Compute y=x^n (mod modulus)
	public Fq pow(int n)
	{
		Fp y;
		
		if(n<0)
			return new Fp((this.inverse().val.modPow(BigInteger.valueOf(n*-1),modulus)));
		else
			return new Fp((val.modPow(BigInteger.valueOf(n),modulus)));
	}
	
	//Compute the Legendre symbol(a/p)
	//Baesd on Algorithm 11.19 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	//p is an odd prime
	public int Legendre(BigInteger a,BigInteger p)
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
	
	//This form will be used to generate a random divisor
	public int legendre() {
		return Legendre(this.val, modulus);
	}
	//Compute x=sqrt(a) (mod p)
	//Based on Algorithm 11.23 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	//p is a prime and a in an integer such that (a/p)=1
	
	private BigInteger TonelliShanks(BigInteger a,BigInteger p)
	{
		if (Legendre(a, p)==-1) // TODO Find a better solution to this
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
		}while(Legendre(n,p)!=-1);
		
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
		}
		
		return x;
	}
	
	//Calculate r=sqrt(a) (mod p)
	public Fq sqrt()
	{
		Fq r=new Fp(TonelliShanks(this.val,modulus));
		return r;
	}
	
	public String toString()
	{
		return val.toString(10);
	}	
}

