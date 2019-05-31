//Divisor of Hyperelliptic curve over prime field

package HECC.Divisor;

import java.util.Random;
import java.math.BigInteger;
import HECC.Hyperelliptic.*;
import HECC.PolynomialRing.*;
import HECC.FiniteField.*;

public class Div_Fp_x extends Div
{
	public Div_Fp_x(HyperellipticCurve C)
	{
		this.C=C;
		this.u=Fp_x.I;
		this.v=Fp_x.O;
	}
	
	public Div_Fp_x(HyperellipticCurve C,Fq_x u,Fq_x v)
	{
		this.C=C;
		this.u=u;
		this.v=v;
	}
	
	/***************************************************************************************************
	 * Constructs a random divisor div(a, b) on a hyperelliptic curve C.
	 * Based on a algorithm described in the thesis 
	 * "Hyperelliptic Cryptosystems on Embedded Microprocessors" by 
	 * Jan Pelzl 2002
	 *Based on code from jSaluki 0.82 and modified accordingly.
	***************************************************************************************************/
	
	public Div_Fp_x (HyperellipticCurve C, Random rndSrc) {
		this.C = C;
		
		//STEP I
		Fq x[] = new Fp [this.C.genus];
		Fq y[] = new Fp [this.C.genus];
		
		for (int i=0; i<this.C.genus; i++) {
			do {
				boolean repeated;
				do {
					repeated = false;
					x[i] = new Fp (rndSrc);
					for (int j=0; j<i; j++) {
						if (x[i].compareTo(x[j])==0)
							repeated = true;
					}
				} while (repeated);
			} while (((Fp)C.f.eval(x[i])).legendre()!=1);
			y[i] = ((Fp)C.f.eval(x[i])).sqrt();
		}
		
		// 2
		this.u = Fp_x.I;
		Fq_x ux = new Fp_x(2);
		for (int i=0; i<this.C.genus; i++) {
			ux.set_coefficient(0,x[i].negate());
			ux.set_coefficient(1,Fp.I);
			this.u = this.u.mul(ux);
		}
		
		// 3
		this.v = new Fp_x(this.C.genus);
		Fq M[][] = new Fp [this.C.genus][this.C.genus];
		Fq.gaussianElimination (x, y, M);
		for (int i=this.C.genus-1; i>=0; i--) {
			Fq msum = Fp.O;
			for (int j=i+1; j<=this.C.genus-1; j++)
				msum = msum.add(M[i][j].mul(v.get_coefficient(j))); 
			v.set_coefficient(i,y[i].add(msum.negate()).mul(M[i][i].inverse()));
		}
		
	}
	
		
		
	
	/****************************************************************************************************
	******************************	GENERAL ADDITION FORMULA ********************************************
	****************************************************************************************************/
		
	//Cantor's Algorithm
	//Baesd on Algorithm 14.7 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	public Div add_Cantor(Div D2)
	{
		Fq_x u1 = this.u; Fq_x v1 = this.v;
		Fq_x u2 = D2.u; Fq_x v2 = D2.v;
		
		Fq_x DST1[], DST2[];
		Fq_x d1, e1, e2;
		Fq_x d, c1, c2;
		Fq_x s1, s2, s3;
		Fq_x u,v;
		
		DST1 = u1.egcd(u2);
		e1 = DST1[2]; e2 = DST1[1]; d1 = DST1[0];
		DST2 = d1.egcd(v1.add(v2).add(C.h));
 		c1 = DST2[2]; c2 = DST2[1]; d = DST2[0];
		
		s1 = c1.mul(e1); s2 = c1.mul(e2); s3 = c2;

		
		u = u1.mul(u2).divide(d.mul(d));
		Fq_x s1u1v2 = s1.mul(u1).mul(v2);
		Fq_x s2u2v1 = s2.mul(u2).mul(v1);
		Fq_x s3_v1v2_f = s3.mul(v1.mul(v2).add(C.f));
		v = s1u1v2.add(s2u2v1).add(s3_v1v2_f).divide(d).mod(u);
		
		//Reduce the divisor
		Fq_x u_1, v_1;
		do {
			u_1 = C.f.add(v.mul(C.h).negate()).add(v.mul(v).negate()).divide(u);
			v_1 = C.h.negate().add(v.negate()).mod(u_1);
			u = u_1; v= v_1;
		} while (u.degree()>C.genus);
		
		//make u monic
		u = u.scalar_mul(u.leading_coefficient().inverse());
		
		Div D = new Div_Fp_x (C);
		D.u=u;
		D.v=v;
		
		return D;
	}	

	
	/********************************************************************************************************
	**********************ARITHMETIC ON GENUS 2 CURVES OVER ARBITRARY CHARACTERISITIC************************
	**********************ADDITION AND DOUBLING IN AFFINE COORDINATES****************************************	
	********************************************************************************************************/
		
		
	//Add two divisors using Explicit Formula
	//CASE :- genus=2,deg(u1)=deg(u2)=2,deg(h)=2
	//Baesd on Algorithm 14.19 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	private Div add_genustwo_degu1_2_degu2_2(Div D2)
	{
		Fq_x u1 = this.u; Fq_x v1 = this.v;
		Fq_x u2 = D2.u; Fq_x v2 = D2.v;
		
		Fq u10,u11,u20,u21;
		Fq v10,v11,v20,v21;
		Fq h0,h1,h2,f4;
		
		try
		{
			h0=C.h.get_coefficient(0);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h0=Fp.O;
		}	
		
		try
		{
			h1=C.h.get_coefficient(1);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h1=Fp.O;
		}	
		try
		{
			h2=C.h.get_coefficient(2);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h2=Fp.O;
		}	
		try
		{
			f4=C.f.get_coefficient(4);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f4=Fp.O;
		}		
				
		//variables for STEP I
		Fq z1,z2,z3,r;
		
		//variables for STEP II
		Fq inv0,inv1;
		
		//variables for STEP III
		Fq w0,w1,w2,w3,s1_1,s0_1;
		
		//variables for STEP IV
		Fq w4,w5,s0_11;
		
		//variables for STEP V
		Fq L2_1,L1_1,L0_1;
		
		//variables for STEP VI		
		Fq u0_1,u1_1;
		
		//variables for STEP VII
		Fq v0_1,v1_1;
		
		//variables for STEP IV'
		Fq inv,s0;
		
		//variables for STEP VIII
		Fq u[],v[];
		Fq_x u_1,v_1;
		Div D = new Div_Fp_x (C);
		
		u10=u1.get_coefficient(0);u11=u1.get_coefficient(1);u20=u2.get_coefficient(0);u21=u2.get_coefficient(1);
		v10=v1.get_coefficient(0);v11=v1.get_coefficient(1);v20=v2.get_coefficient(0);v21=v2.get_coefficient(1);
		
		//STEP I
		z1=u11.add(u21.negate());
		z2=u20.add(u10.negate());
		z3=u11.mul(z1).add(z2);
		r=z2.mul(z3).add(z1.mul(z1).mul(u10));
		
		//STEP II
		inv1=z1;
		inv0=z3;
		
		//STEP III
		w0=v10.add(v20.negate());
		w1=v11.add(v21.negate());
		w2=inv0.mul(w0);
		w3=inv1.mul(w1);
		Fq inv0_inv1=inv0.add(inv1);
		Fq w0_w1=w0.add(w1);
		Fq w3_1_u11=w3.add(w3.mul(u11));
		s1_1=inv0_inv1.mul(w0_w1).add(w2.negate()).add(w3_1_u11.negate());
		s0_1=w2.add(u10.mul(w3).negate());
		
		if(!s1_1.isZero())
		{
			//STEP IV
			w1=(r.mul(s1_1)).inverse();
			w2=r.mul(w1);
			w3=s1_1.mul(s1_1).mul(w1);
			w4=r.mul(w2);
			w5=w4.mul(w4);
			s0_11=s0_1.mul(w2);
			
			//STEP V
			L2_1=u21.add(s0_11);
			L1_1=u21.mul(s0_11).add(u20);
			L0_1=u20.mul(s0_11);
			
			//STEP VI
			Fq s0_11_u11=s0_11.add(u11.negate());
			Fq s0_11_z1_h2w4=s0_11.add(z1.negate()).add(h2.mul(w4));
			u0_1=s0_11_u11.mul(s0_11_z1_h2w4).add(u10.negate());
			Fq h1_twov21=h1.add(v21).add(v21);
			Fq twou21_z1_f4=u21.add(u21).add(z1).add(f4.negate());
			u0_1=u0_1.add(L1_1).add(h1_twov21.mul(w4)).add(twou21_z1_f4.mul(w5));
			u1_1=s0_11.add(s0_11).add(z1.negate()).add(h2.mul(w4)).add(w5.negate());
			
			//STEP VII
			w1=L2_1.add(u1_1.negate());
			w2=u1_1.mul(w1).add(u0_1).add(L1_1.negate());
			v1_1=w2.mul(w3).add(v21.negate()).add(h1.negate()).add(h2.mul(u1_1));
			w2=u0_1.mul(w1).add(L0_1.negate());
			v0_1=w2.mul(w3).add(v20.negate()).add(h0.negate()).add(h2.mul(u0_1));
		
		    //STEP VIII
			u=new Fp[3];
			u[2]=Fp.I;
			u[1]=u1_1;
			u[0]=u0_1;
			u_1=new Fp_x(u);
		
			v=new Fp[2];
			v[1]=v1_1;v[0]=v0_1;
			v_1=new Fp_x(v);
			
			D.u=u_1;
			D.v=v_1;		
		
		}	
		else
		{
			//STEP IV'
			inv=r.inverse();
			s0=s0_1.mul(inv);
			
			//STEP V'
			u0_1=f4.add(u21.negate()).add(u11.negate()).add((s0.mul(s0)).negate()).add(s0.mul(h2));
			
			//STEP VI'
			Fq u21_u0_1=u21.add(u0_1);
			w1=s0.mul(u21_u0_1).add(h1).add(v21).add((h2.mul(u0_1)).negate());
			w2=u20.mul(s0).add(v20).add(h0);
			v0_1=u0_1.mul(w1).add(w2.negate());
			
			
			//STEP VII
			L2_1=Fp.O;
			L1_1=Fp.O;
			L0_1=Fp.O;
			u1_1=Fp.O;
						
			w1=L2_1.add(u1_1.negate());
			w2=u1_1.mul(w1).add(u0_1).add(L1_1.negate());
			v1_1=w2.mul(w3).add(v21.negate()).add(h1.negate()).add(h2.mul(u1_1));
			w2=u0_1.mul(w1).add(L0_1.negate());
			v0_1=w2.mul(w3).add(v20.negate()).add(h0.negate()).add(h2.mul(u0_1));
		
            //STEP VIII		
			u=new Fp[2];
			u[1]=Fp.I;
			u[0]=u0_1;
			u_1=new Fp_x(u);
		
			v=new Fp[1];
			v[0]=v0_1;
			v_1=new Fp_x(v);
			
			D.u=u_1;
			D.v=v_1;		
		
		}	
		return D;
	}
	
	
	
	//Add two divisors using Explicit Formula
	//CASE :- genus=2,deg(u1)=1,deg(u2)=2
	//Baesd on Algorithm 14.20 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	private Div add_genustwo_degu1_1_degu2_2(Div D2)
	{
		Fq_x u1 = this.u; Fq_x v1 = this.v;
		Fq_x u2 = D2.u; Fq_x v2 = D2.v;
		Fq_x temp;
		
		if(u1.degree()==2)
		{
			temp=u1;
			u1=u2;
			u2=temp;
			
			temp=v1;
			v1=v2;
			v2=temp;
		}	
		
		Fq u10,u20,u21;
		Fq v10,v20,v21;
		Fq h0,h1,h2,f3,f4;
		
		try
		{
			h0=C.h.get_coefficient(0);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h0=Fp.O;
		}	
		
		try
		{
			h1=C.h.get_coefficient(1);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h1=Fp.O;
		}	
		try
		{
			h2=C.h.get_coefficient(2);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h2=Fp.O;
		}	
		try
		{
			f3=C.f.get_coefficient(3);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f3=Fp.O;
		}	
		try
		{
			f4=C.f.get_coefficient(4);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f4=Fp.O;
		}	
		
		//variables for STEP I
		Fq r;
		
		//variables for STEP II
		Fq inv;
		
		//variables for STEP III
		Fq s0;
		
		//variables for STEP IV
		Fq L1,L0;
		
		//variables for STEP V
		Fq t1,t2;
		
		//variables for STEP VI
		Fq u0_1,u1_1,v0_1,v1_1;
		
		//variables for STEP VIII
		Fq u[],v[];
		Fq_x u_1,v_1;
		Div D = new Div_Fp_x (C);
		
		u10=u1.get_coefficient(0);u20=u2.get_coefficient(0);u21=u2.get_coefficient(1);
		v10=v1.get_coefficient(0);v20=v2.get_coefficient(0);v21=v2.get_coefficient(1);
		
		//STEP I
		r=u20.add(((u21.add(u10.negate())).mul(u10)).negate());
		
		//STEP II
		inv=r.inverse();
		
		//STEP III
		Fq v10_v20_v21u10=v10.add(v20.negate()).add(v21.mul(u10).negate());
		s0=inv.mul(v10_v20_v21u10);
		
		//STEP IV
		L1=u21.mul(s0);
		L0=u20.mul(s0);
			
		//STEP V
		t2=f4.add(u21.negate());
		Fq f4_u21=f4.add(u21.negate());
		t1=f3.add(f4_u21.mul(u21).negate()).add(v21.mul(h2).negate()).add(u20.negate());
		
		//STEP VI
		u1_1=t2.add(s0.mul(s0).negate()).add(s0.mul(h2).negate()).add(u10.negate());
		Fq L1_h1_twov21=L1.add(h1).add(v21).add(v21);
		u0_1=t1.add(s0.mul(L1_h1_twov21).negate()).add(u10.mul(u1_1).negate());
		
		//STEP VII
		Fq h2_s0=h2.add(s0);
		Fq h1_L1_v21=h1.add(L1).add(v21);
		v1_1=h2_s0.mul(u1_1).add(h1_L1_v21.negate());
		Fq h0_L0_v20=h0.add(L0).add(v20);
		v0_1=h2_s0.mul(u0_1).add(h0_L0_v20.negate());
		
		//STEP VIII
		u=new Fp[3];
		u[2]=Fp.I;
		u[1]=u1_1;
		u[0]=u0_1;
		u_1=new Fp_x(u);
		
		v=new Fp[2];
		v[1]=v1_1;
		v[0]=v0_1;
		v_1=new Fp_x(v);
			
		D.u=u_1;
		D.v=v_1;		
		
		return D;
	}
	
	//Double two divisors using Explicit Formula
	//CASE :- genus=2,deg(u)=2,deg(h)=2
	//Baesd on Algorithm 14.21 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	private Div double_genustwo_degu_2()
	{
		Fq_x u = this.u; Fq_x v = this.v;
		
		Fq u1,u0;
		Fq v1,v0;
		
		Fq h0,h1,h2,f2,f3,f4;
		
		try
		{
			h0=C.h.get_coefficient(0);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h0=Fp.O;
		}	
		
		try
		{
			h1=C.h.get_coefficient(1);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h1=Fp.O;
		}	
		try
		{
			h2=C.h.get_coefficient(2);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h2=Fp.O;
		}
		try
		{
			f2=C.f.get_coefficient(2);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f2=Fp.O;
		}		
		try
		{
			f3=C.f.get_coefficient(3);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f3=Fp.O;
		}	
		try
		{
			f4=C.f.get_coefficient(4);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f4=Fp.O;
		}	
		
		
		//variables for STEP I
		Fq v1_bar,v0_bar;
		
		//variables for STEP II
		Fq w0,w1,w2,w3,r;
		
		//variables for STEP III
		Fq inv1_1,inv0_1;
		
		//variables for STEP IV
		Fq t1_1,t0_1;
		
		//variables for STEP V
		Fq s1_1,s0_1;
		
		//variables for STEP VI
		Fq w4,w5,s0_11;
		
		//variables for STEP VI'
		Fq s0;
		
		//variables for STEP VII
		Fq L2_1,L1_1,L0_1;
		
		//variables for STEP VIII
		Fq u0_1,u1_1;
		
		//variables for STEP IX
		Fq v0_1,v1_1;
		
		//variables for STEP X
		Fq uu[],vv[];
		Fq_x u_1,v_1;
		Div D = new Div_Fp_x (C);
		
		u0=u.get_coefficient(0);u1=u.get_coefficient(1);
		v0=v.get_coefficient(0);v1=v.get_coefficient(1);
		
		//STEP I
		v1_bar=h1.add(v1).add(v1).add(h2.mul(u1).negate());
		v0_bar=h0.add(v0).add(v0).add(h2.mul(u0).negate());
		
		//STEP II
		w0=v1.mul(v1);w1=u1.mul(u1);w2=v1_bar.mul(v1_bar);
		w3=u1.mul(v1_bar);
		Fq v0_bar_w3=v0_bar.add(w3.negate());
		r=u0.mul(w2).add(v0_bar.mul(v0_bar_w3));
		
		//STEP III
		inv1_1=v1_bar.negate();
		inv0_1=v0_bar.add(w3.negate());
		
		//STEP IV
		w3=f3.add(w1);
		w4=u0.add(u0);
		Fq two_w1_f4u1=w1.add(w1).add(f4.mul(u1).negate()).add(f4.mul(u1).negate());
		t1_1=two_w1_f4u1.add(w2).add(w4.negate()).add(h2.mul(v1).negate());
		Fq two_w4_w3_f4u1_h2v1=w4.add(w4).add(w3.negate()).add(f4.mul(u1)).add(h2.mul(v1));
		Fq two_f4u0=(f4.mul(u0)).add(f4.mul(u0));
		Fq h1v1=h1.mul(v1);
		Fq h2v0=h2.mul(v0);
		t0_1=u1.mul(two_w4_w3_f4u1_h2v1).add(f2).add(w0.negate()).add(two_f4u0.negate()).add(h1v1.negate()).add(h2v0.negate());
		
		//STEP V
		w0=t0_1.mul(inv0_1);w1=t1_1.mul(inv1_1);
		Fq inv0_inv1=inv0_1.add(inv1_1);
		Fq t0_1_t1_1=t0_1.mul(t1_1);
		Fq w1_1_u1=w1.add(w1.mul(u1));
		s1_1=inv0_inv1.mul(t0_1_t1_1).add(w0.negate()).add(w1_1_u1.negate());
		s0_1=w0.add(u0.mul(w1).negate());
		
					
		if(!s1_1.isZero())
		{
			//STEP VI
			w1=r.mul(s1_1).inverse();
			w2=r.mul(w1);
			w3=s1_1.mul(s1_1).mul(w1);
			w4=r.mul(w2);
			w5=w4.mul(w4);
			s0_11=s0_1.mul(w2);
			
			//STEP VII
			L2_1=u1.add(s0_11);
			L1_1=u1.mul(s0_11).add(u0);
			L0_1=u0.mul(s0_11);
			
			//STEP VIII
			Fq h2_s0_u1_twov1_h1=h2.mul(s0_11.add(u1.negate())).add(v1).add(h1);
			Fq w5_twou1_f4=w5.mul(u1.add(u1).add(f4.negate()));
			u0_1=s0_11.mul(s0_11).add(w4.mul(h2_s0_u1_twov1_h1)).add(w5_twou1_f4);
			u1_1=s0_11.mul(s0_11).add(h2.mul(w4)).add(w5.negate());
			
			
			//STEP IX
			w1=L2_1.add(u1_1.negate());
			w2=u1_1.mul(w1).add(u0_1).add(L1_1.negate());
			v1_1=w2.mul(w3).add(v1.negate()).add(h1.negate()).add(h2.mul(u1_1));
			w2=u0_1.mul(w1).add(L0_1.negate());
			v0_1=w2.mul(w3).add(v0.negate()).add(h0.negate()).add(h2.mul(u0_1));
		
		    //STEP X
			uu=new Fp[3];
			uu[2]=Fp.I;
			uu[1]=u1_1;
			uu[0]=u0_1;
			u_1=new Fp_x(uu);
		
			vv=new Fp[2];
			vv[1]=v1_1;
			vv[0]=v0_1;
			v_1=new Fp_x(vv);
			
			D.u=u_1;
			D.v=v_1;		
		
		}	
		else
		{
			//STEP VI'
			w1=r.inverse();
			s0=s0_1.mul(w1);
			w2=u0.mul(s0).add(v0).add(h0);
			
			//STEP VII'
			u0_1=f4.add(s0.mul(s0).negate()).add(s0.mul(h2).negate()).add(u1.negate()).add(u1.negate());
			
			//STEP VIII'
			w1=s0.mul(u1.add(u0_1.negate())).add(h1).add(v1).add((h2.mul(u0_1)).negate());
			v0_1=u0_1.mul(w1).add(w2.negate());
			
			
			//STEP IX
			L2_1=Fp.O;
			L1_1=Fp.O;
			L0_1=Fp.O;
			u1_1=Fp.O;
			
			w1=L2_1.add(u1_1.negate());
			w2=u1_1.mul(w1).add(u0_1).add(L1_1.negate());
			v1_1=w2.mul(w3).add(v1.negate()).add(h1.negate()).add(h2.mul(u1_1));
			w2=u0_1.mul(w1).add(L0_1.negate());
			v0_1=w2.mul(w3).add(v0.negate()).add(h0.negate()).add(h2.mul(u0_1));
			
			//STEP X			
			uu=new Fp[2];
			uu[1]=Fp.I;
			uu[0]=u0_1;
			u_1=new Fp_x(uu);
		
			vv=new Fp[1];
			vv[0]=v0_1;
			v_1=new Fp_x(vv);
			
			D.u=u_1;
			D.v=v_1;		
		
		}	
		return D;
	}
	
	
	/*********************************************************************************************************
	*****************************************ARITHMETIC ON GENUS THREE CURVES*********************************	
	*********************************************************************************************************/	
		
	//Add two divisors using Explicit Formula
	//CASE :- genus=3
	//Baesd on Algorithm 14.52 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	private Div add_genusThree(Div D2)
	{
		//variables required for algorithm
		Fq_x u1 = this.u; Fq_x v1 = this.v;
		Fq_x u2 = D2.u; Fq_x v2 = D2.v;
		
		Fq h0,h1,h2,h3,f4,f5,f6;
		
		try
		{
			h0=C.h.get_coefficient(0);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h0=Fp.O;
		}	
		
		try
		{
			h1=C.h.get_coefficient(1);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h1=Fp.O;
		}	
		try
		{
			h2=C.h.get_coefficient(2);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h2=Fp.O;
		}
		try
		{
			h3=C.h.get_coefficient(3);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h3=Fp.O;
		}
		try
		{
			f4=C.f.get_coefficient(4);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f4=Fp.O;
		}		
		try
		{
			f5=C.f.get_coefficient(5);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f5=Fp.O;
		}	
		try
		{
			f6=C.f.get_coefficient(6);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f6=Fp.O;
		}	
		
		
		//variables for Step I
		Fq u10,u11,u12,u20,u21,u22;
		Fq v10,v11,v12,v20,v21,v22;
		Fq t0,t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,r;
		
		//variables for Step II
		Fq inv0,inv1,inv2;
		
		//variables for Step III
		Fq t12,t13,t14,t15,t16,t17,t18,r0_1,r1_1,r2_1,r3_1,r4_1,s0_1,s1_1,s2_1,s2_2;
		
		//variables for Step IV
		Fq w1,w2,w3,w4,w5,s0,s1;
		
		//variables for Step V
		Fq z0,z1,z2,z3,z4;
		
		//variables for Step VI
		Fq u3_1,u2_1,u1_1,u0_1;
			
		//variables for Step VII	
		Fq v0_1,v1_1,v2_1,v3_1;
		
		//variables for Step VIII
		Fq u2_11,u1_11,u0_11;
		
		//variables for Step IX
		Fq v2_11,v1_11,v0_11;
		
		//variables for Step X
		Fq u[],v[];
		Fq_x u_11,v_11;
		Div D = new Div_Fp_x (C);
		
		u10=u1.get_coefficient(0);u11=u1.get_coefficient(1);u12=u1.get_coefficient(2);
		u20=u2.get_coefficient(0);u21=u2.get_coefficient(1);u22=u2.get_coefficient(2);
		v10=v1.get_coefficient(0);v11=v1.get_coefficient(1);v12=v1.get_coefficient(2);
		v20=v2.get_coefficient(0);v21=v2.get_coefficient(1);v22=v2.get_coefficient(2);
		
		//STEP I
		t1=u12.mul(u21);t2=u11.mul(u22);t3=u11.mul(u20);t4=u10.mul(u21);t5=u12.mul(u20);t6=u10.mul(u22);
		Fq u20_u10=u20.add(u10.negate());t7=u20_u10.mul(u20_u10);
		Fq u21_u11=u21.add(u11.negate());t8=u21_u11.mul(u21_u11);
		Fq u22_u12=u22.add(u12.negate());
		Fq t3_t4=t3.add(t4.negate());
		t9=u22_u12.mul(t3_t4);
		Fq t5_t6=t5.add(t6.negate());
		t10=u22_u12.mul(t5_t6);
		t11=u21_u11.mul(u20_u10);
		Fq u20_u10_t1_t2=u20.add(u10.negate()).add(t1).add(t2.negate());
		Fq t7_t9=t7.add(t9.negate());
		Fq t10_two_t11=t10.add(t11.negate()).add(t11.negate());
		r=u20_u10_t1_t2.mul(t7_t9).add(t5_t6.mul(t10_two_t11)).add(t8.mul(t3_t4));
		
		if(r.isZero())
		{
			//Call add_Cantor()
			return this.add_Cantor(D2);
		}	
			
			 
		//STEP II
		Fq t1_t2_u10_u20=t1.add(t2.negate()).add(u10.negate()).add(u20);
		inv2=t1_t2_u10_u20.mul(u22_u12).add(t8.negate());
		inv1=inv2.mul(u22).add(t10.negate()).add(t11);
		Fq t10_t11=t10.add(t11.negate());
		Fq u22_t10_t11=u22.mul(t10_t11);
		inv0=inv2.mul(u21).add(u22_t10_t11.negate()).add(t9).add(t7.negate());
			
		//STEP III
		Fq inv1_inv2=inv1.add(inv2);
		Fq v22_v12=v22.add(v12.negate());
		Fq v21_v11=v21.add(v11.negate());
		t12=inv1_inv2.mul(v22_v12.add(v21_v11));
		t13=v21_v11.mul(inv1);
		Fq inv0_inv2=inv0.add(inv2.negate());
		Fq v20_v10=v20.add(v10.negate());
		t14=inv0_inv2.mul(v22_v12.add(v20_v10));
		t15=v20_v10.mul(inv0);
		Fq inv0_inv1=inv0.add(inv1);
		t16=inv0_inv1.mul(v21_v11.add(v20_v10));
		t17=v22_v12.mul(inv2);
			
		r0_1=t15;
		r1_1=t16.add(t13.negate()).add(t15.negate());
		r2_1=t13.add(t14).add(t15.negate()).add(t17.negate());
		r3_1=t12.add(t13.negate()).add(t17.negate());
		r4_1=t17;
		t18=u22.mul(r4_1).add(r3_1.negate());
		t15=u20.mul(t18);t16=u21.mul(r4_1);
		s0_1=r0_1.add(t15);
		Fq u21_u20=u21.add(u20);
		Fq r4_1_t18=r4_1.add(t18.negate());
		s1_1=r1_1.add(u21_u20.mul(r4_1_t18).negate()).add(t16).add(t15.negate());
		s2_1=r2_1.add(t16.negate()).add(u22.mul(t18));
		
		if(s2_1.isZero())
		{
			//Call add_Cantor()
			return this.add_Cantor(D2);
		}
		
		w1=(r.mul(s2_1)).inverse();
		w2=r.mul(w1);
		w3=w1.mul(s2_1).mul(s2_1);
		w4=r.mul(w2);
		w5=w4.mul(w4);
		s0=w2.mul(s0_1);
		s1=w2.mul(s1_1);
			
		//STEP V
		z0=s0.mul(u10);
		z1=s1.mul(u10).add(s0.mul(u11));
		z2=s0.mul(u12).add(s1.mul(u11)).add(u10);
		z3=s1.mul(u12).add(s0).add(u11);
		z4=u12.add(s1);
		
		//STEP VI
		u3_1=z4.add(s1).add(u22.negate());
		u2_1=u22.mul(u3_1).negate().add(u21.negate()).add(z3).add(s0).add(w4.mul(h3)).add(s1.mul(z4));
		Fq h2_twov12_s1h3=h2.add(v12).add(v12).add(s1.mul(h3));
		u1_1=w4.mul(h2_twov12_s1h3).add(s1.mul(z3)).add(s0.mul(z4)).add(z2).add(w5.negate()).add(u22.mul(u2_1).negate()).add(u21.mul(u3_1).negate()).add(u20.negate());
		
		Fq s1h2_h1_twov11_twos1v12_s0h3=s1.mul(h2).add(h1).add(v11).add(v11).add(s1.mul(v12)).add(s1.mul(v12)).add(s0.mul(h3));
		Fq u12_f6=u12.add(f6.negate());
		u0_1=w4.mul(s1h2_h1_twov11_twos1v12_s0h3).add(s1.mul(z2)).add(z1).add(s0.mul(z3)).add(w5.mul(u12_f6)).add(u22.mul(u1_1).negate()).add(u21.mul(u2_1).negate()).add(u20.mul(u3_1).negate());
		
		//STEP VII
		t1=u3_1.add(z4.negate());
		Fq u0_1t1_z0=u0_1.mul(t1).add(z0);
		v0_1=w3.mul(u0_1t1_z0).negate().add(h0.negate()).add(v10.negate());
		Fq u1_1t1_u0_1_z1=u1_1.mul(t1).add(u0_1.negate()).add(z1);
		v1_1=w3.mul(u1_1t1_u0_1_z1).negate().add(h1.negate()).add(v11.negate());
		Fq u2_1t1_u1_1_z2=u2_1.mul(t1).add(u2_1.negate()).add(z2);
		v2_1=w3.mul(u2_1t1_u1_1_z2).negate().add(h2.negate()).add(v12.negate());
		Fq u3_1t1_u2_1_z3=u3_1.mul(t1).add(u2_1.negate()).add(z3);
		v3_1=w3.mul(u3_1t1_u2_1_z3).negate().add(h3.negate());
		
		//STEP VIII
		u2_11=f6.add(u3_1.negate()).add(v3_1.mul(v3_1).negate()).add(v3_1.mul(h3).negate());
		u1_11=u2_1.negate().add(u2_11.mul(u3_1).negate()).add(f5).add(v2_1.mul(v3_1).negate()).add(v2_1.mul(v3_1).negate()).add(v3_1.mul(h2).negate()).add(v2_1.mul(h3).negate());
		u0_11=u1_1.negate().add(u2_11.mul(u2_1).negate()).add(u1_11.mul(u3_1).negate()).add(f4).add(v1_1.mul(v3_1).negate()).add(v1_1.mul(v3_1).negate()).add(v2_1.mul(v2_1).negate()).add(v2_1.mul(h2).negate()).add(v3_1.mul(h1).negate()).add(v1_1.mul(h3).negate());
		
		//STEP IX
		Fq v3_1_h3=v3_1.add(h3);
		
		v2_11=v2_1.negate().add(v3_1_h3.mul(u2_11)).add(h2.negate());
		v1_11=v1_1.negate().add(v3_1_h3.mul(u1_11)).add(h1.negate());
		v0_11=v0_1.negate().add(v3_1_h3.mul(u0_11)).add(h0.negate());
		
		
		//STEP X
		u=new Fp[4];
		u[3]=Fp.I;
		u[2]=u2_11;
		u[1]=u1_11;
		u[0]=u0_11;
		u_11=new Fp_x(u);
		
		v=new Fp[3];
		v[2]=v2_11;
		v[1]=v1_11;
		v[0]=v0_11;
		v_11=new Fp_x(v);
		D.u=u_11;
		D.v=v_11;		
		
		return D;
	}


	//Double divisor using Explicit Formula
	//CASE :- genus=3
	//Baesd on Algorithm 14.53 in Handbook of Elliptic and HyperElliptic Curve Cryptography
	private Div double_genusThree()
	{
		//variables required for algorithm
		Fq_x u = this.u; Fq_x v = this.v;
		
		Fq_x h_bar=C.h.add(v).add(v);
		
		Fq h0_bar,h1_bar,h2_bar;
		try
		{
			h0_bar=h_bar.get_coefficient(0);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h0_bar=Fp.O;
		}
		try
		{
			h1_bar=h_bar.get_coefficient(1);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h1_bar=Fp.O;
		}
		try
		{
			h2_bar=h_bar.get_coefficient(2);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h2_bar=Fp.O;
		}
						
		Fq h0,h1,h2,h3,f3,f4,f5,f6;
		
		try
		{
			h0=C.h.get_coefficient(0);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h0=Fp.O;
		}	
		
		try
		{
			h1=C.h.get_coefficient(1);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h1=Fp.O;
		}	
		try
		{
			h2=C.h.get_coefficient(2);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h2=Fp.O;
		}
		try
		{
			h3=C.h.get_coefficient(3);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			h3=Fp.O;
		}
		try
		{
			f3=C.f.get_coefficient(3);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f3=Fp.O;
		}
		try
		{
			f4=C.f.get_coefficient(4);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f4=Fp.O;
		}		
		try
		{
			f5=C.f.get_coefficient(5);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f5=Fp.O;
		}	
		try
		{
			f6=C.f.get_coefficient(6);
		}catch(ArrayIndexOutOfBoundsException e)
		{
			f6=Fp.O;
		}	
		
		//variables for Step I
		Fq u0,u1,u2;
		Fq v0,v1,v2;
		Fq t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,r;
		
		//variables for Step II
		Fq inv0,inv1,inv2;
		
		//variables for Step III
		Fq t12,t13,z1_1,z2_1,z3_1,z0,z1,z2;
		
		//variables for Step IV
		Fq t14,t15,t16,t17,t18,r0_1,r1_1,r2_1,r3_1,r4_1,s0_1,s1_1,s2_1,s2_2;
		
		//variables for Step V
		Fq w1,w2,w3,w4,w5,s0,s1;
		
		//variables for Step VI
		Fq g0,g1,g2,g3,g4;
		
		//variables for Step VII
		Fq u3_1,u2_1,u1_1,u0_1;
			
		//variables for Step VIII	
		Fq v0_1,v1_1,v2_1,v3_1;
		
		//variables for Step IX
		Fq u2_11,u1_11,u0_11;
		
		//variables for Step X
		Fq v2_11,v1_11,v0_11;
		
		//variables for Step XI
		Fq uu[],vv[];
		Fq_x u_11,v_11;
		Div D = new Div_Fp_x (C);
		
		u0=u.get_coefficient(0);u1=u.get_coefficient(1);u2=u.get_coefficient(2);
		v0=v.get_coefficient(0);v1=v.get_coefficient(1);v2=v.get_coefficient(2);
		
		
		//STEP I
		t1=u2.mul(h1_bar);t2=u1.mul(h2_bar);t3=u1.mul(h0_bar);t4=u0.mul(h1_bar);t5=u2.mul(h0_bar);t6=u0.mul(h2_bar);
		
		Fq h0_bar_h3u0=h0_bar.add(h3.mul(u0).negate());t7=h0_bar_h3u0.mul(h0_bar_h3u0);
		Fq h1_bar_h3u1=h1_bar.add(h3.mul(u1).negate());t8=h1_bar_h3u1.mul(h1_bar_h3u1);
		Fq h2_bar_h3u2=h2_bar.add(h3.mul(u2).negate());
		Fq t3_t4=t3.add(t4.negate());
		t9=h2_bar_h3u2.mul(t3_t4);
		Fq t5_t6=t5.add(t6.negate());
		t10=h2_bar_h3u2.mul(t5_t6);
		t11=h1_bar_h3u1.mul(h0_bar_h3u0);
		
		Fq h0_bar_h3u0_t1_t2=h0_bar_h3u0.add(t1).add(t2.negate());
		Fq t7_t9=t7.add(t9.negate());
		Fq t10_two_t11=t10.add(t11.negate()).add(t11.negate());
		r=h0_bar_h3u0_t1_t2.mul(t7_t9).add(t5_t6.mul(t10_two_t11)).add(t8.mul(t3_t4));
		
		if(r.isZero())
		{
			//Call add_Cantor()
			return this.add_Cantor(this);
		}	
			
			 
		//STEP II
		inv2=h0_bar_h3u0_t1_t2.mul(h2_bar_h3u2).negate().add(t8);
		Fq t10_t11=t10.add(t11.negate());
		inv1=inv2.mul(u2).add(t10_t11);
		Fq u2_t10_t11=u2.mul(t10_t11);
		inv0=inv2.mul(u1).add(u2_t10_t11).add(t9.negate()).add(t7);
		
		//STEP III
		t12=v2.mul(v2);z3_1=f6.add(u2.negate());t13=z3_1.mul(u1);
		z2_1=f5.add(h3.mul(v2).negate()).add(u1.negate()).add(u2.mul(f6).negate()).add(u2.mul(u2));
		z1_1=f4.add(h2.mul(v2).negate()).add(h3.mul(v1).negate()).add(t12.negate()).add(u0.negate()).add(t13.negate()).add(z2_1.mul(u2).negate());
		Fq u2_twoz3_1=u2.add(z3_1.negate()).add(z3_1.negate());
		z2=f5.add(h3.mul(v2).negate()).add(u1.negate()).add(u1.negate()).add(u2.mul(u2_twoz3_1));
		z1=z1_1.add(t13.negate()).add(u2.mul(u1)).add(u0.negate());
		z0=f3.add(h2.mul(v1).negate()).add(h1.mul(v2).negate()).add(v2.mul(v1).negate()).add(v2.mul(v1).negate()).add(h3.mul(v0).negate()).add(u0.mul(u2_twoz3_1)).add(z2_1.mul(u1).negate()).add(z2_1.mul(u2).negate());
		
		
		//STEP IV
		Fq inv1_inv2=inv1.add(inv2);
		Fq z1_z2=z1.add(z2);
		t12=inv1_inv2.mul(z1_z2);
		t13=z1.mul(inv1);
		
		Fq inv0_inv2=inv0.add(inv2);
		Fq z0_z2=z0.add(z2);
		t14=inv0_inv2.mul(z0_z2);
		t15=z0.mul(inv0);
		
		Fq inv0_inv1=inv0.add(inv1);
		Fq z0_z1=z0.add(z1);
		t16=inv0_inv1.mul(z0_z1);
		t17=z2.mul(inv2);
		
			
		r0_1=t15;
		r1_1=t16.add(t13.negate()).add(t15.negate());
		r2_1=t13.add(t14).add(t15.negate()).add(t17.negate());
		r3_1=t12.add(t13.negate()).add(t17.negate());
		r4_1=t17;
		t18=u2.mul(r4_1).add(r3_1.negate());
		t15=u0.mul(t18);t16=u1.mul(r4_1);
		s0_1=r0_1.add(t15);
		
		Fq u1_u0=u1.add(u0);
		Fq r4_1_t18=r4_1.add(t18.negate());
		s1_1=r1_1.add(u1_u0.mul(r4_1_t18).negate()).add(t16).add(t15.negate());
		s2_1=r2_1.add(t16.negate()).add(u2.mul(t18));
		
		if(s2_1.isZero())
		{
			//Call add_Cantor()
			return this.add_Cantor(this);
		}
		
		//STEP V
		w1=(r.mul(s2_1)).inverse();
		w2=r.mul(w1);
		w3=w1.mul(s2_1).mul(s2_1);
		w4=r.mul(w2);
		w5=w4.mul(w4);
		s0=w2.mul(s0_1);
		s1=w2.mul(s1_1);
			
		//STEP VI
		g0=s0.mul(u0);
		g1=s1.mul(u0).add(s0.mul(u1));
		g2=s0.mul(u2).add(s1.mul(u1)).add(u0);
		g3=s1.mul(u2).add(s0).add(u1);
		g4=u2.add(s1);
		
		//STEP VII
		u3_1=s1.add(s1);
		u2_1=s1.mul(s1).add(s0).add(s0).add(w4.mul(h3));
		Fq twov2_h3s1_h2_h3u2=v2.add(v2).add(h3.mul(s1)).add(h2).add(h3.mul(u2).negate());
		u1_1=s0.mul(s1).add(s0.mul(s1)).add(w4.mul(twov2_h3s1_h2_h3u2)).add(w5.negate());
		Fq u2h3_twov2_h2_s1h3=u2.mul(h3).add(v2.negate()).add(v2.negate()).add(h2.negate()).add(s1.mul(h3).negate());
		Fq twov1_h1_h3s0_h3u1_twov2s1_h2s1=v1.add(v1).add(h1).add(h3.mul(s0)).add(h3.mul(u1).negate()).add(v2.mul(s1)).add(v2.mul(s1)).add(h2.mul(s1));
		u0_1=w4.mul(twov1_h1_h3s0_h3u1_twov2s1_h2s1.add(u2.mul(u2h3_twov2_h2_s1h3)));
		Fq f6_twou2=f6.negate().add(u2).add(u2);
		u0_1=u0_1.add(w5.mul(f6_twou2)).add(s0.mul(s0));
		
		
		//STEP VIII
		t1=u3_1.add(g4.negate());
		Fq t1u3_1_u2_1_g3=t1.mul(u3_1).add(u2_1.negate()).add(g3);
		v3_1=w3.mul(t1u3_1_u2_1_g3).negate().add(h3.negate());
		
		Fq t1u2_1_u1_1_g2=t1.mul(u2_1).add(u1_1.negate()).add(g2);
		v2_1=w3.mul(t1u2_1_u1_1_g2).negate().add(h2.negate()).add(v2.negate());
		
		Fq t1u1_1_u0_1_g1=t1.mul(u1_1).add(u0_1.negate()).add(g1);
		v1_1=w3.mul(t1u1_1_u0_1_g1).negate().add(h1.negate()).add(v1.negate());
		
		Fq t1u0_1_g1=t1.mul(u0_1).add(g0);
		v0_1=w3.mul(t1u0_1_g1).negate().add(h0.negate()).add(v0.negate());
		
		
		
		//STEP IX
		u2_11=f6.add(u3_1.negate()).add(v3_1.mul(v3_1).negate()).add(v3_1.mul(h3).negate());
		u1_11=u2_1.negate().add(u2_11.mul(u3_1).negate()).add(f5).add(v2_1.mul(v3_1).negate()).add(v2_1.mul(v3_1).negate()).add(v3_1.mul(h2).negate()).add(v2_1.mul(h3).negate());
		u0_11=u1_1.negate().add(u2_11.mul(u2_1).negate()).add(u1_11.mul(u3_1).negate()).add(f4).add(v1_1.mul(v3_1).negate()).add(v1_1.mul(v3_1).negate()).add(v2_1.mul(v2_1).negate()).add(v2_1.mul(h2).negate()).add(v3_1.mul(h1).negate()).add(v1_1.mul(h3).negate());
		
		//STEP X
		Fq v3_1_h3=v3_1.add(h3);
		
		v2_11=v2_1.negate().add(v3_1_h3.mul(u2_11)).add(h2.negate());
		v1_11=v1_1.negate().add(v3_1_h3.mul(u1_11)).add(h1.negate());
		v0_11=v0_1.negate().add(v3_1_h3.mul(u0_11)).add(h0.negate());
		
		
		//STEP X
		uu=new Fp[4];
		uu[3]=Fp.I;
		uu[2]=u2_11;
		uu[1]=u1_11;
		uu[0]=u0_11;
		u_11=new Fp_x(uu);
		
		vv=new Fp[3];
		vv[2]=v2_11;
		vv[1]=v1_11;
		vv[0]=v0_11;
		v_11=new Fp_x(vv);
		D.u=u_11;
		D.v=v_11;		
		
		return D;
	}
	
	
	/**
	 * Convert a semi-reduced divisor to a reduced divisor.
	 * Based on Algorithm 2 in the Appendix by Alfred J. Menezes,
	 * Yi-Hong Wu, and Robert Zuccherato in "Algebraic Aspects of
	 * Cryptography" by Neal Koblitz
	 */
	public Div reduce () {
		Fq_x u1 = this.u;
		Fq_x v1 = this.v;
		Fq_x red_u, red_v;
		do {
			red_u = C.f.add(v1.mul(C.h).negate()).add(v1.mul(v1).negate()).divide(u1);
			red_v = C.h.negate().add(v1.negate()).mod(red_u);
			u1 = red_u; v1= red_v;
		} while (u1.degree()>C.genus);
		u1 = u1.scalar_mul(u1.leading_coefficient().inverse());
		return new Div_Fp_x (C, u1, v1);
	}
	
	
	/*public Div add(Div D2)
	{
		Div D= new Div_Fp_x (C);
		D=this.add_Cantor(D2);
		return D;
	}	
	*/
	
	public Div add(Div D2)
	{
		Div D= new Div_Fp_x (C);
		if(this.C.genus==2)
		{
			if((this.u.degree()==2)&&(D2.u.degree()==2))
				D=this.add_genustwo_degu1_2_degu2_2(D2);
			else if((this.u.degree()==1)&&(D2.u.degree()==2))	
				D=this.add_genustwo_degu1_1_degu2_2(D2);
			else if((this.u.degree()==2)&&(D2.u.degree()==1))	
				D=this.add_genustwo_degu1_1_degu2_2(D2);
			else
				D=this.add_Cantor(D2);
		}	
		else if(this.C.genus==3)
			D=this.add_genusThree(D2);
		else
			D=this.add_Cantor(D2);
		
		return D;
	}	
		
	/*	
	public Div double_divisor()
	{
		Div D= new Div_Fp_x (C);
		D=this.add_Cantor(this);
		return D;
	}*/
	public Div double_divisor()
	{
		Div D= new Div_Fp_x (C);
		if((this.C.genus==2)&&(this.u.degree()==2))
			D=this.double_genustwo_degu_2();
		else if(this.C.genus==3)
			D=this.double_genusThree();
		else
			D=this.add_Cantor(this.reduce());
		
		return D;
	}	
		
		
	
	
	//Returns -D=div(a,-h-b)
	public Div negate()
	{
		Div D= new Div_Fp_x (C);
	
		D.C=this.C;
		D.u=this.u;
		D.v=C.h.add(v).negate().mod(u);
	
		return D;	
	}	
	
	/****************************************************************************************
	 ********SCALAR MULTIPLICATION***********************************************************
	 *Based on Algorithm 9.5 in Handbook of Elliptic and Hyperelliptic Curve Cryptography****	
	*****************************************************************************************/
	public Div mul(BigInteger n)
	{
		Div D= new Div_Fp_x(C);
		Div D1=new Div_Fp_x(C);
		Div D2=new Div_Fp_x(C);
		BigInteger k;

		if (n.compareTo(BigInteger.valueOf(0)) == 0)
			return new Div_Fp_x(C);

		if (n.compareTo(BigInteger.valueOf(0)) < 0) {
			k = n.negate();
			D = this.negate();
		} else {
			k = n;
			D = this;
		}

		D1=D;
		D2=D.double_divisor();
		for (int j = k.bitLength() - 1; j >= 0; j--)
		{
			if (!k.testBit(j))
			{
				D1=D1.double_divisor();
				D2=D1.add(D2);
			}
			else
			{
				D1=D1.add(D2);
				D2=D2.double_divisor();
			}	
		}

		return D1;	
	}
	
		
	public String toString()
	{
		return "div ("+ u + "," + v + ")";
	}
}

