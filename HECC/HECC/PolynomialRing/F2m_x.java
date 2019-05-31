//Polynomial ring over the prime field F2m(x)

package HECC.PolynomialRing;

import HECC.FiniteField.*;

public class F2m_x extends Fq_x
{
	//Additive Identity Element
	static public F2m_x O;
	
	//Multiplicative Identity Element
	static public F2m_x I;
	
	static
	{
		O=new F2m_x();
		I=new F2m_x();
		I.coeffs[0]=F2m.I;
	}
	
	public F2m_x()
	{
		coeffs=new Fq[1];
		coeffs[0]=new F2m();
	}
	
	public F2m_x(int n)
	{
		coeffs=new Fq[n];
		for(int i=0;i<n;i++)
			coeffs[i]=new F2m();
	}
	
	public F2m_x(Fq val[])
	{
		this.coeffs=val;
	}
	
	protected void resize(int n)
	{
		Fq temp_coeffs[]=new F2m[n];
		
		if(n<=coeffs.length)
			for(int i=0;i<n;i++)
				temp_coeffs[i]=coeffs[i];
		else
		{
			for(int i=0;i<coeffs.length;i++)
				temp_coeffs[i]=coeffs[i];
			for(int i=coeffs.length;i<n;i++)
				temp_coeffs[i]=new F2m();
		}
		coeffs=temp_coeffs;
	}
	
	//Add x to each coefficient
	public Fq_x scalar_add(Fq x)
	{
		Fq_x temp=new F2m_x(coeffs.length);
		
		for(int i=0;i<coeffs.length;i++)
			temp.coeffs[i]=coeffs[i].add(x);
		return temp;
	}
	
	//Multiply each coeffieient by x
	public Fq_x scalar_mul(Fq x)
	{
		Fq_x temp=new F2m_x(coeffs.length);
		
		for(int i=0;i<coeffs.length;i++)
			temp.coeffs[i]=coeffs[i].mul(x);
		return temp;
	}
	
	//Polynomial Addition
	public Fq_x add(Fq_x x)
	{
		Fq_x temp;
		
		if(coeffs.length<=x.coeffs.length)
		{
			temp=new F2m_x(x.coeffs.length);
			for(int i=0;i<coeffs.length;i++)
				temp.coeffs[i]=coeffs[i].add(x.coeffs[i]);
			for(int i=coeffs.length;i<x.coeffs.length;i++)
				temp.coeffs[i]=x.coeffs[i];
		}
		else
		{
			temp=new F2m_x(coeffs.length);
			for(int i=0;i<x.coeffs.length;i++)
				temp.coeffs[i]=coeffs[i].add(x.coeffs[i]);
			for(int i=x.coeffs.length;i<coeffs.length;i++)
				temp.coeffs[i]=coeffs[i];
		}
		return temp;
	}

	//Polynomial Multiplication
	//Based on Algorithm given in Section 3.1.2 of "A Course in Computational Algebraic Number Theory" by Henri Cohen.
	public Fq_x mul(Fq_x f) {
		int m = this.degree();
		int n = f.degree();
		Fq_x c = new F2m_x(m + n + 2);
		Fq a[] = this.coeffs;
		Fq b[] = f.coeffs;

		for (int k = 0; k <= (n + m); k++) {
			for (int i = 0; i <= k; i++) {
				if ((i <= m) & ((k - i) <= n)) {
					c.coeffs[k] = c.coeffs[k].add(a[i].mul(b[k - i]));
				}
			}
		}
		c.resize(c.degree()+1);
		return c;
	}
	//Polynomial Division
	//Based on Algorithm 3.1.1 of "A Course in Computational Algebraic Number Theory" by Henri Cohen.
	public Fq_x[] divideAndRemainder(Fq_x B) throws RuntimeException
	{
		if(B.isZero())
			throw new RuntimeException("divideAndRemainder: divisor is zero");
		Fq_x R=this;
		Fq_x Q=new F2m_x();
		Fq_x S;
		
		while(R.degree()>=B.degree())
		{
			S=new F2m_x(R.degree()-B.degree()+1);
			S.coeffs[R.degree()-B.degree()]=R.leading_coefficient().mul(B.leading_coefficient().inverse());
			Q=Q.add(S);
			R=R.add(S.mul(B).negate());
		}
		Fq_x QR[]=new Fq_x[2]; //QR[1]=Q, QR[0]=R
		Q.resize(Q.degree()+1);
		R.resize(R.degree()+1);
		QR[1]=Q;
		QR[0]=R;
		return QR;
	}
	
	//Euclidean algorithm to compute GCD
	//Baesd on Algorithm 2.218 in Handbook of Applied Cryptography
	public Fq_x gcd(Fq_x h)
	{
		Fq_x g=this;
		Fq_x QR[];
		
		while(!h.isZero())
		{
			QR=g.divideAndRemainder(h);
			g=h;
			h=QR[0];
		}
		g.resize(g.degree()+1);
		return g;
	}
	/*
	//Extended Euclidean algorithm to compute EGCD
	//Baesd on Algorithm 2.221 in Handbook of Applied Cryptography
	public Fq_x[] egcd(Fq_x h)
	{
		Fq_x g=this;
		Fq_x QR[];
		Fq_x d;
		Fq_x s;
		Fq_x t;
		Fq_x s1;Fq_x s2;
		Fq_x t1;Fq_x t2;
		
		if(h.isZero())
		{
			d=g;
			s=new F2m_x();s.coeffs[0]=F2m.I;
			t=new F2m_x();s.coeffs[0]=F2m.O;
		}
		else
		{
			s2=new F2m_x();s2.coeffs[0]=F2m.I;
			s1=new F2m_x();s1.coeffs[0]=F2m.O;
			t2=new F2m_x();t2.coeffs[0]=F2m.O;
			t1=new F2m_x();t1.coeffs[0]=F2m.I;
			while(!h.isZero())
			{
				QR=g.divideAndRemainder(h);//QR[1]=quotient and QR[0]=remainder
				s=s2.add(QR[1].mul(s1).negate());
				t=t2.add(QR[1].mul(t1).negate());
				g=h;h=QR[0];
				s2=s1;s1=s;t2=t1;t1=t;
			}
			d=g;s=s2;t=t2;
		}
		
		Fq_x dst[]=new F2m_x[3];
		d.resize(d.degree()+1);
		s.resize(s.degree()+1);
		t.resize(t.degree()+1);
		
		dst[2]=d;
		dst[1]=s;
		dst[0]=t;
			
		return dst;
	}
	*/
	/**
	 * Extended Polynomial GCD
	 * <BR>
	 * Algorithm 3.2.2 from of "A Course in Computational
	 * Algebraic Number Theory" by Henri Cohen
	 */
	public Fq_x[] egcd(Fq_x B) {
		Fq_x A = this;
		Fq_x U; Fq_x V; Fq_x D;
		Fq_x V1; Fq_x V3;
		Fq_x QR[];
		Fq_x T;
		
		U = new F2m_x(); U.coeffs[0] = F2m.I;
		D = A;
		V1 = new F2m_x(); V1.coeffs[0] = F2m.O;
		V3 = B;
		
		while (!V3.isZero()) {
			QR = D.divideAndRemainder(V3);
			T = U.add(V1.mul(QR[1]).negate());
			U = V1;
			D = V3;
			V1 = T;
			V3 = QR[0];
		}
		
		
		if (!B.isZero())
			V = (D.add(A.mul(U).negate())).divide(B);
		else V = B;

		Fq_x UVD[] = new F2m_x[3];
		U.resize(U.degree()+1); V.resize(V.degree()+1); D.resize(D.degree()+1);
		UVD[2] = U; UVD[1] = V; UVD[0] = D;
		
		return UVD;
	}
	
		
	public Fq_x negate()
	{
		Fq_x c=new F2m_x(coeffs.length);
		for(int i=0;i<coeffs.length;i++)
			c.coeffs[i]=coeffs[i].negate();
		return c;
	}
}

