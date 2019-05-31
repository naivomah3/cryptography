//Polynomial ring over the finite field Fq(x)


package HECC.PolynomialRing;

import java.math.BigInteger;
import HECC.FiniteField.*;

public abstract class Fq_x
{
	protected Fq coeffs[];
	
	protected abstract void resize(int n);
	
	public Fq_x()
	{
	}
	
	public Fq_x(int n)
	{
	}
	
	public Fq_x(Fq val[])
	{
	}
	
	//Return the degree of the polynomial
	public int degree()
	{
		int i;
		for(i=coeffs.length-1;i>=0;i--)
			if(!coeffs[i].isZero())
				return i;
		return i;
	}
	
	//Check whether polynomial is empty or not
	public boolean isZero()
	{
		for(int i=0;i<coeffs.length;i++)
			if(!coeffs[i].isZero())
				return false;
		return true;
	}

	//Returns the leading coefficient of the polynomial
	public Fq leading_coefficient()
	{
		for(int i=coeffs.length-1;i>=0;i--)
			if(!coeffs[i].isZero())
				return coeffs[i];
		return coeffs[0];
	}
	
	//Returns the nth coefficient of the polynomial
	public Fq get_coefficient(int n)throws RuntimeException
	{	
		if(n<0)
			throw new RuntimeException("get_coefficient: non-existent coefficient");	
		else
			return coeffs[n];
	}
	//Set the nth coefficient of the polynomial
	public void set_coefficient(int n,Fq val)throws RuntimeException
	{	
		if(n<0)
			throw new RuntimeException("set_coefficient1: non-existent coefficient");	
		coeffs[n]=val;
	}
	
	public String toString() {
		if (this.isZero())
			return "0";
		String s = "";
		for (int i = coeffs.length - 1; i > 1; i--) {
			if (coeffs[i].val.compareTo(BigInteger.valueOf(1)) == 0) {
				if (s.length() > 0)
					s += " + ";
				s += "u^" + i;
			} else if (!coeffs[i].isZero()) {
				if (s.length() > 0)
					s += " + ";
				s += coeffs[i] + "u^" + i;
			}
		}
		if (coeffs.length > 1) {
			if (coeffs[1].val.compareTo(BigInteger.valueOf(1)) == 0) {
				if (s.length() > 0)
					s += " + ";
				s += "u";
			} else if (!coeffs[1].isZero()) {
				if (s.length() > 0)
					s += " + ";
				s += coeffs[1] + "u";
			}
		}
		if (!coeffs[0].isZero()) {
			if (s.length() > 0)
				s += " + ";
			s += coeffs[0];
		}
		return s;
	}
	
	//Add x to each coefficient
	public abstract Fq_x scalar_add(Fq x);
	
	//Multiply each coefficient by x
	public abstract Fq_x scalar_mul(Fq x);
	
	//Polynomial Addition 
	public abstract Fq_x add(Fq_x y);
	
	//Polynomial Subtraction
	public Fq_x sub(Fq_x y)
	{
		return this.add(y.negate());
	}

	//Polynomial Multiplication
	public abstract Fq_x mul(Fq_x y);

	//Polynomial Division
	//Returns the Quotient and remainder
	//return Fq_x[1] the Quotient,Fq_x[0] the Remainder
	//throws RuntimeException on division by zero

	public abstract Fq_x[] divideAndRemainder(Fq_x y) throws RuntimeException;

	//Polynomial Division
	//Returns Quotient
	public Fq_x divide(Fq_x y)
	{
		return this.divideAndRemainder(y)[1];
	}

	//Polynomial Division 
	//Returns the Remainder
	public Fq_x mod(Fq_x y)
	{
		return this.divideAndRemainder(y)[0];
	}

	//Polynomial GCD
	public abstract Fq_x gcd(Fq_x y);

	//Extended Euclid Polynomial GCD
	public abstract Fq_x[] egcd(Fq_x y);

	//Returns the Additive Inverse
	public abstract Fq_x negate();
	
	//Evaluate the polynomial at x=a and returns f(a)
	public Fq eval(Fq a)
	{
		Fq b=coeffs[0];
		for(int i=1;i<coeffs.length;i++)
			b=b.add(coeffs[i].mul(a.pow(i)));
		return b;
	}	
	
}

