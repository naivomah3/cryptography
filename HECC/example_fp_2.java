import java.math.BigInteger;
import java.security.SecureRandom;
import HECC.Divisor.*;
import HECC.Hyperelliptic.*;
import HECC.PolynomialRing.*;
import HECC.FiniteField.*;

public class example_fp_2 {

	
	
	// Hyperelliptic Curve Key Agreement Scheme Diffie-Hellman
	// (over an odd prime characteristic field)
	// The finite field and hyperelliptic curve are taken from 
	// from the paper "Generating Hyperelliptic Curves Suitable
	// for Cryptography" by Naoki Kanayama, Koh-ichi Nagao and
	// Shigenori Uchiyama.
	static void Fp_example_2 () {
		System.out.println();
		System.out.println("Hyperelliptic Curve Key Agreement Scheme Diffie-Hellman");
		System.out.println("(over an odd prime characteristic field)");
		System.out.println("The finite field and hyperelliptic curve are taken from");
		System.out.println("the paper \"Generating Hyperelliptic Curves Suitable");
		System.out.println("for Cryptography\" by Naoki Kanayama, Koh-ichi Nagao and");
		System.out.println("Shigenori Uchiyama.");
		System.out.println();
		
		SecureRandom rndSrc = new SecureRandom();
		// Set up the finite field
		Fp.setModulus(new BigInteger("8388617"));
		Fp f[];

		// h(u) = 0
		f = new Fp[1];
		f[0] = new Fp();
		Fq_x hu = new Fp_x(f);

		// f(u) = u^5 + 7943193u^4 + 6521255u^3 + 1065528u^2 + 3279922u + 3728927
		f = new Fp[6];
		f[5] = new Fp("1", 10);
		f[4] = new Fp("7943193", 10);
		f[3] = new Fp("6521255", 10);
		f[2] = new Fp("1065528", 10);
		f[1] = new Fp("3279922", 10);
		f[0] = new Fp("3728927", 10);
		Fq_x fu = new Fp_x(f);

		HyperellipticCurve C = new HyperellipticCurve(hu, fu);
		System.out.println("C: " + C);
		System.out.println("genus: " + C.genus);

		Div D = new Div_Fp_x(C, rndSrc);
		System.out.println("D: "+D);

		// Create A's secret and public keys
		BigInteger skA = BigInteger.valueOf(123);
		Div pkA = D.mul(skA);
		System.out.println("pkA: " + pkA);

		// Create B's secret and public keys
		BigInteger skB = BigInteger.valueOf(456);
		Div pkB = D.mul(skB);
		System.out.println("pkB: " + pkB);

		// A very simple Diffie-Hellman Key Exchange
		Div KA = (D.mul(skA)).add(pkB);
		Div KB = (D.mul(skA)).add(pkB);

		// Verify that the keys match 
		System.out.println("KA:" + KA);
		System.out.println("KB:" + KB);
	}
	
	
	public static void main(String[] args) {
		long start_time, end_time;
		start_time=System.currentTimeMillis();//get starting time
		
		Fp_example_2();
		
		end_time=System.currentTimeMillis();//get ending time
		
		System.out.println("Elapsed time:" + (end_time-start_time)+"ms");
	
	}

}