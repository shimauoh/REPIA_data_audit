import java.math.BigInteger;
import java.security.SecureRandom;

public class SchnorrParameterGenerator {

    public static class SchnorrParams {
        public BigInteger p;
        public BigInteger q;
        public BigInteger g;

        public SchnorrParams(BigInteger p, BigInteger q, BigInteger g) {
            this.p = p;
            this.q = q;
            this.g = g;
        }
    }

    public static SchnorrParams generateParameters(int lambda) {
        SecureRandom rnd = new SecureRandom();

        int qBitLength = Math.max(lambda, 160);            // q is the subgroup order
        int pBitLength = Math.max(2 * lambda, 1024);       // p is the modulus

        BigInteger q, p, g;
        while (true) {
            q = BigInteger.probablePrime(qBitLength, rnd);

            // Try to find p = k*q + 1 such that p is also prime
            for (int k = 2; k < 10000; k++) {
                p = q.multiply(BigInteger.valueOf(k)).add(BigInteger.ONE);
                if (p.bitLength() >= pBitLength && p.isProbablePrime(100)) {
                    // Now find generator g of subgroup of order q in Z_p*
                    BigInteger h, exp;
                    exp = p.subtract(BigInteger.ONE).divide(q);
                    for (int j = 2; j < 100; j++) {
                        h = BigInteger.valueOf(j);
                        g = h.modPow(exp, p);
                        if (!g.equals(BigInteger.ONE)) {
                            return new SchnorrParams(p, q, g);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        int lambda = 128;  // Set your desired security level here
        SchnorrParams params = generateParameters(lambda);

        System.out.println("Generated Schnorr Parameters:");
        System.out.println("q (" + params.q.bitLength() + " bits): " + params.q);
        System.out.println("p (" + params.p.bitLength() + " bits): " + params.p);
        System.out.println("g: " + params.g);
    }
}
