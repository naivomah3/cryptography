/*Multi-precision Binary Finite Field class.
Finite Field elements in a polynomial basis of the form:
*/

package HECC.FiniteField;

import java.math.BigInteger;
import java.util.Random;

public class F2m extends Fq
{
	//Finite Field Additive Identity Element
	static public F2m O;

	//Finite Field Multiplicative Identity Element
	static public F2m I;

	static
	{
		O=new F2m();
		I=new F2m();
		I.val=BigInteger.valueOf(1);
	}
	
	public F2m()
	{
		super();
	}

	//Modular reduction in F2m
	//Returns r=a (mod modulus), Where a and modulus are in polynomial basis form
	private static BigInteger F2x_mod(BigInteger a) throws ArithmeticException
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
	
	//Construct a finite field element = val(mod modulus)
	public F2m(BigInteger val)
	{
		this.val=F2x_mod(val);
	}

	public F2m(String strVal,int radix)
	{
		val=F2x_mod(new BigInteger(strVal,radix));
	}
	
	public F2m(Random rndSrc)
	{
		val=F2x_mod(new BigInteger(modulus.bitLength()-1,rndSrc));
	}
	
	
	//Set the modulus as a irreducible trinomial of the form
	//pow(x,k3)+pow(x,k2)+pow(x,k1)
	public static void setModulus(int k3,int k2,int k1)
	{
		modulus=BigInteger.valueOf(0);
		modulus=modulus.setBit(k3);
		modulus=modulus.setBit(k2);
		modulus=modulus.setBit(k1);
	}
	
	
	//Set the modulus as a irreducible pentanomial of the form
	//pow(x,k5)+pow(x,k4)+pow(x,k3)+pow(x,k2)+pow(x,k1)
	public static void setModulus(int k5,int k4,int k3,int k2,int k1)
	{
		modulus=BigInteger.valueOf(0);
		modulus=modulus.setBit(k5);
		modulus=modulus.setBit(k4);
		modulus=modulus.setBit(k3);
		modulus=modulus.setBit(k2);
		modulus=modulus.setBit(k1);
	}
	
	
	//Addition in F2m
	//z=x+y
	protected static BigInteger F2x_add(BigInteger x,BigInteger y)
	{
		return x.xor(y);
	}
	
	
	//Multiplication in F2m
	//z=x*y (mod modulus)
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
		return F2x_mod(z); //slight modification of Algorithm to perform computation (mod modulus)
	}
	
	
	//Compute y=pow(x,-1)(mod modulus) in F2m
	//Baesd on Algorithm 2.48 of Guide to Elliptic Curve Cryptography
	protected static BigInteger F2x_inv(BigInteger x)
	{
		BigInteger u= F2x_mod(x);
		BigInteger v=modulus;
		BigInteger g1=BigInteger.valueOf(1);
		BigInteger g2=BigInteger.valueOf(0);
		int j;
		BigInteger temp;
		while(u.bitLength()!=0)
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
	
	//Return the degree of the field m, F2m
	public static int degree()
	{
		return modulus.bitLength()-1;
	}

	
	//Compute z=x+y (mod modulus)
	public Fq add(Fq y)
	{
		return new F2m(F2x_mod(F2x_add(val,y.val)));
	}
	/*
	//Compute y=pow(x,-1)(mod modulus)
	public Fq inverse()
	{
		return new F2m(F2x_mod(F2x_inv(val)));
	}*/
		
	/**
	 * Compute b = a<sup>-1</sup> (mod modulus).
	 * <P>
	 * Algorithm 8 in "Software Implementation of Elliptic Curve Cryptography
	 * Over Binary Fields", D. Hankerson, J.L. Hernandez, A. Menezes.
	 */
	public Fq inverse() {
		BigInteger b = BigInteger.valueOf(1);
		BigInteger c = BigInteger.valueOf(0);
		BigInteger u = val;
		BigInteger v = modulus;
		int j;
		BigInteger buf;
		while (u.bitLength() != 0) {
			j = u.bitLength() - v.bitLength();
			if (j < 0) {
				buf = u;
				u = v;
				v = buf;
				buf = c;
				c = b;
				b = buf;
				j = -j;
			}
			u = F2x_add(u, v.shiftLeft(j));
			b = F2x_add(b, c.shiftLeft(j));
		}

		return new F2m(F2x_mod(c));
	}
	
	
	
	//Compute z=x*y (mod modulus)
	public Fq mul(Fq y)
	{
		return new F2m(F2x_mod(F2x_mul(val,y.val)));
	}
	
	//Compute y=-x (mod modulus)
	public Fq negate()
	{
		return new F2m(val);
	}
	
	//Compute y=pow(x,n)(mod modulus)
	public Fq pow(int n)
	{
		Fq y=F2m.I;
		if(n<0)
			for(int i=0;i<(n*-1);i++)
				y=y.mul(this.inverse());
		else
			for(int i=0;i<n;i++)
				y=y.mul(this);
		return y;
	}
	
	//Added later from JSaluki0.82
	/**
	 * Calculate the trace of using the algoritm in A.4.5 of P1363.
	 */
	public Fq trace() {
		Fq T = this;
		for (int i=1; i<=degree()-1; i++) {
			T = T.pow(2).add(this);
		}
		return T;
	}

	//Added later from JSaluki0.82
	/**
	 * Calculate the trace of using the algoritm in A.4.6 of P1363.
	 */
	public Fq half_trace() {
		Fq H = this;
		for (int i=1; i<=(degree()-1)/2; i++) {
			H = H.pow(2);
			H = H.pow(2).add(this);
		}
		return H;
	}

	public String toString()
	{
		return val.toString(10);
	}	
	
}
