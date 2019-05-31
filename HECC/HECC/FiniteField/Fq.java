/*Multi-precision Finite Field class*/

package HECC.FiniteField;

import java.math.BigInteger;
import java.util.Random;

public abstract class Fq
{
	//Finite Field Modulus
	static protected BigInteger modulus=BigInteger.valueOf(0);
	
	//Finite Field Element
	public BigInteger val;
	
	//Get the Finite Field Modulus
	public static BigInteger getModulus()
	{
		return modulus;
	}
	
	public Fq()
	{
		this.val=BigInteger.valueOf(0);
	}
	
	public Fq(BigInteger x)
	{
		this.val=x;
	}
	//Check whether it is zero or not
	public boolean isZero()
	{
		if(val.equals(BigInteger.valueOf(0)))
			return true;
		else
			return false;
	}
	
	public int compareTo(Fq x)
	{
		return val.compareTo(x.val);
	}
	
	//Required for generating Random Divisor
	//Added later from JSaluki0.82
	static public void gaussianElimination (Fq x[], Fq y[], Fq M[][]) {
		// x matrix : each element x_ij = (x_i)^j
		int n = M.length;
		if (n!=M[0].length)
			throw new RuntimeException ("gaussianElimination: Can't Invert!"); // TODO
		Fq c[] = new Fq[n];

		for (int i=0; i<n; i++) {
			for (int j=0; j<n; j++) {
				M[i][j] = x[i].pow(j);
			}
		}
		for (int j=0; j<=n-1; j++) {
			int i=j;
			while (M[i][j].isZero()) {
				i++;
				if (i==n)
					throw new RuntimeException ("gaussianElimination: Can't Invert!"); // TODO
			}
			if (i>j) {
				for (int l=j; l<n-1; l++) {
					Fq buf;
					buf = M[i][l]; M[i][l] = M[j][l]; M[j][l] = buf; 
					buf = y[i]; y[i] = y[j]; y[j] = buf;
				}
			}
			Fq d = M[j][j].inverse();
			for (int k=j+1; k<=n-1; k++)
				c[k] = d.mul(M[k][j]);
			for (int k=j+1; k<=n-1; k++) {
				for (int l=j; l<=n-1; l++)
					M[k][l] = M[k][l].add(c[k].mul(M[j][l]).negate());
				y[k] = y[k].add(c[k].mul(y[j]).negate());
			}
		}
	}

	public abstract Fq add(Fq y);
	public abstract Fq inverse();
	public abstract Fq mul(Fq y);
	public abstract Fq negate();
	public abstract Fq pow(int n);
	public abstract String toString();	
	
}	

