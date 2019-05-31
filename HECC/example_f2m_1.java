import java.math.BigInteger;
import java.security.SecureRandom;
import HECC.Divisor.*;
import HECC.Hyperelliptic.*;
import HECC.PolynomialRing.*;
import HECC.FiniteField.*;

public class example_f2m_1 {

	
	static void F2m_example_1() {
		// Hyperelliptic Curve Key Agreement Scheme Diffie-Hellman
		// The finite field, hyperelliptic curve and divisors are 
		// taken from Example 7.1 in the Appendix by Alfred J. 
		// Menezes, Yi-Hong Wu, and Robert Zuccherato in "Algebraic
		// Aspects of Cryptography" by Neal Koblitz

		// Set up the finite field
		F2m.setModulus(5, 2, 0);
		F2m f[];

		// h(u) = u^2 + u
		f = new F2m[3];
		f[2] = new F2m("1", 16);
		f[1] = new F2m("1", 16);
		f[0] = new F2m();
		Fq_x hu = new F2m_x(f);

		// f(u) = u^5 + u^3 + 1
		f = new F2m[6];
		f[5] = new F2m("1", 16);
		f[4] = new F2m();
		f[3] = new F2m("1", 16);
		f[2] = new F2m();
		f[1] = new F2m();
		f[0] = new F2m("1", 16);
		Fq_x fu = new F2m_x(f);

		HyperellipticCurve C = new HyperellipticCurve(hu, fu);
		System.out.println("C: " + C);
		System.out.println("genus: " + C.genus);

		// a1 = u^2 + (x^4 + x)u 
		f = new F2m[3];
		f[2] = new F2m("1", 16);
		f[1] = new F2m("10010", 2);
		f[0] = new F2m();
		Fq_x a1 = new F2m_x(f);
		// b1 = (x)u + 1 
		f = new F2m[2];
		f[1] = new F2m("10", 2);
		f[0] = new F2m("1", 16);
		Fq_x b1 = new F2m_x(f);
		Div D1 = new Div_F2m_x(C, a1, b1);
		System.out.println("D1: " + D1);

		// a2 = u^2 + (x^4 + x + 1)u + (x^4 + x)
		f = new F2m[3];
		f[2] = new F2m("1", 16);
		f[1] = new F2m("10011", 2);
		f[0] = new F2m("10010", 2);
		Fq_x a2 = new F2m_x(f);
		// b2 = (x^3+x^2+x+1)u + (x^3+x^2+x)
		f = new F2m[2];
		f[1] = new F2m("1111", 2);
		f[0] = new F2m("1110", 2);
		Fq_x b2 = new F2m_x(f);
		Div D2 = new Div_F2m_x(C, a2, b2);
		System.out.println("D2: " + D2);

		System.out.println("D1+D2: " + D1.add(D2));
		Div Dinv = D1.negate();
		System.out.println("Dinv: " + Dinv);
		System.out.println("D1+Dinv: " + D1.add(Dinv));

		// Create A's secret and public keys
		BigInteger skA = BigInteger.valueOf(123);
		Div pkA = D1.mul(skA);
		System.out.println("pkA: " + pkA);

		// Create B's secret and public keys
		BigInteger skB = BigInteger.valueOf(456);
		Div pkB = D1.mul(skB);
		System.out.println("pkB: " + pkB);

		// A very simple Diffie-Hellman Key Exchange
		Div KA = (D1.mul(skA)).add(pkB);
		Div KB = (D1.mul(skA)).add(pkB);

		// Verify that the keys match 
		System.out.println("KA:" + KA);
		System.out.println("KB:" + KB);
	}
	
		
	public static void main(String[] args) {
		long start_time, end_time;
		start_time=System.currentTimeMillis();//get starting time
		
		F2m_example_1();
		
		end_time=System.currentTimeMillis();//get ending time
		
		System.out.println("Elapsed time:" + (end_time-start_time)+"ms");
	
	}


}