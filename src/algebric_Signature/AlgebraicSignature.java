import java.math.BigInteger;

public class AlgebraicSignature {
    public static BigInteger generateTag(String block, BigInteger g, BigInteger p) {
        BigInteger sum = BigInteger.ZERO;

        for (int i = 0; i < block.length(); i++) {
            char c = block.charAt(i);
            int si = (int) c; // can be ASCII value
            BigInteger gi = g.modPow(BigInteger.valueOf(i), p);
            BigInteger term = gi.multiply(BigInteger.valueOf(si)).mod(p);
            sum = sum.add(term).mod(p);
        }

        return sum;
    }
}
