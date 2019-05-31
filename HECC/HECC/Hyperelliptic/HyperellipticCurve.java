//Hyperelliptic curve of arbitrary genus

package HECC.Hyperelliptic;

import HECC.PolynomialRing.*;
import HECC.FiniteField.*;

public class HyperellipticCurve
{
	public Fq_x h;
	public Fq_x f;
	public int genus;
	
	public HyperellipticCurve(Fq_x h,Fq_x f)
	{
		this.h=h;
		this.f=f;
		genus=(this.f.degree()-1)/2;
	}
	
	public String toString()
	{
		if(h.isZero())
			return "v^2="+f;
		else
			return "v^2 + ("+ h + ")v= "+f;
	}
}
