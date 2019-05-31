import java.math.BigInteger;
import java.security.SecureRandom;
import HECC.Divisor.*;
import HECC.Hyperelliptic.*;
import HECC.PolynomialRing.*;
import HECC.FiniteField.*;

public class example_f2m_2 {

	
	// Hyperelliptic Curve Key Agreement Scheme Diffie-Hellman
	// (over a binary field)
	// The finite field and hyperelliptic curve are taken from 
	// Example 7.1 in the Appendix by Alfred J. Menezes, 
	// Yi-Hong Wu, and Robert Zuccherato in "Algebraic Aspects
	// of Cryptography" by Neal Koblitz.
	static void F2m_example_2 () {
		System.out.println();
		System.out.println("Hyperelliptic Curve Key Agreement Scheme Diffie-Hellman");
		System.out.println("(over a binary field)");
		System.out.println("The finite field and hyperelliptic curve are taken from");
		System.out.println("Example 7.1 in the Appendix by Alfred J. Menezes,");
		System.out.println("Yi-Hong Wu, and Robert Zuccherato in \"Algebraic Aspects");
		System.out.println("of Cryptography\" by Neal Koblitz.");
		System.out.println();

		//F2m_example();
		SecureRandom rndSrc = new SecureRandom();
		// Set up the finite field
		F2m.setModulus(5, 2, 0);
		F2m F2m_f[];

		// h(u) = u^2 + u
		F2m_f = new F2m[3];
		F2m_f[2] = new F2m("1", 16);
		F2m_f[1] = new F2m("1", 16);
		F2m_f[0] = new F2m();
		Fq_x F2m_hu = new F2m_x(F2m_f);

		// f(u) = u^5 + u^3 + 1
		F2m_f = new F2m[6];
		F2m_f[5] = new F2m("1", 16);
		F2m_f[4] = new F2m();
		F2m_f[3] = new F2m("1", 16);
		F2m_f[2] = new F2m();
		F2m_f[1] = new F2m();
		F2m_f[0] = new F2m("1", 16);
		Fq_x F2m_fu = new F2m_x(F2m_f);

		HyperellipticCurve F2m_C = new HyperellipticCurve(F2m_hu, F2m_fu);
		System.out.println("F2m_C: " + F2m_C);
		System.out.println("genus: " + F2m_C.genus);

		Div F2m_D = new Div_F2m_x(F2m_C, rndSrc);
		System.out.println("D: "+F2m_D);

		// Create A's secret and public keys
		BigInteger F2m_skA = BigInteger.valueOf(123);
		Div F2m_pkA = F2m_D.mul(F2m_skA);
		System.out.println("pkA: " + F2m_pkA);

		// Create B's secret and public keys
		BigInteger F2m_skB = BigInteger.valueOf(456);
		Div F2m_pkB = F2m_D.mul(F2m_skB);
		System.out.println("pkB: " + F2m_pkB);

		// A very simple Diffie-Hellman Key Exchange
		Div F2m_KA = (F2m_D.mul(F2m_skA)).add(F2m_pkB);
		Div F2m_KB = (F2m_D.mul(F2m_skA)).add(F2m_pkB);

		// Verify that the keys match 
		System.out.println("KA:" + F2m_KA);
		System.out.println("KB:" + F2m_KB);
	}
		public static void main(String[] args) {
		long start_time, end_time;
		start_time=System.currentTimeMillis();//get starting time
		
		F2m_example_2();
		
		end_time=System.currentTimeMillis();//get ending time
		
		System.out.println("Elapsed time:" + (end_time-start_time)+"ms");
	
	}


}