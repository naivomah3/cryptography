import java.math.BigInteger;
import java.security.SecureRandom;
import HECC.Divisor.*;
import HECC.Hyperelliptic.*;
import HECC.PolynomialRing.*;
import HECC.FiniteField.*;

public class example_fp_1 {


	static void Fp_example_1() {
		// Hyperelliptic Curve Key Agreement Scheme Diffie-Hellman
		// The finite field, hyperelliptic curve and divisors are 
		// taken from the paper "Generating Hyperelliptic Curves Suitable
		// for Cryptography" by Naoki Kanayama, Koh-ichi Nagao and Shigenori
		// Uchiyama.

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

	}

	public static void main(String[] args) {
		long start_time, end_time;
		start_time=System.currentTimeMillis();//get starting time

		Fp_example_1();

		end_time=System.currentTimeMillis();//get ending time

		System.out.println("Elapsed time:" + (end_time-start_time)+"ms");

	}

}
