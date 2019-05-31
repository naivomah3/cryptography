//Divisor of Hyperelliptic curve

package HECC.Divisor;

import java.math.BigInteger;
import HECC.Hyperelliptic.*;
import HECC.PolynomialRing.*;
import HECC.FiniteField.*;

public abstract class Div
{
	public HyperellipticCurve C;
	public Fq_x u;
	public Fq_x v;
	
	public abstract Div add(Div D2);
	public abstract Div double_divisor();
	public abstract Div negate();
	public abstract Div mul(BigInteger n);
	public abstract Div add_Cantor(Div D2);
	
	
	public String toString()
	{
		return "div ("+ u + "," + v + ")";
	}
}

