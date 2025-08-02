import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.*;

public class AlgebraicSignatureKeyGen {
    public static void main(String[] args) {
        try {
            int lambda = 256; // security parameter
            SecureRandom random = new SecureRandom();

            // Generate large prime p
            BigInteger p = BigInteger.probablePrime(lambda, random);
            long startTime = System.nanoTime();
            // Generate random g ∈ Zp
            BigInteger g;
            do {
                g = new BigInteger(lambda, random);
            } while (g.compareTo(BigInteger.ZERO) <= 0 || g.compareTo(p) >= 0);

            // Save to files
            try (BufferedWriter pub = new BufferedWriter(new FileWriter("algebraic_public_keys.txt"))) {
                pub.write("p=" + p.toString());
            }
            try (BufferedWriter sec = new BufferedWriter(new FileWriter("algebraic_secret_keys.txt"))) {
                sec.write("g=" + g.toString());
            }
            long endTime = System.nanoTime();
            long keyGenTime = endTime - startTime;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("algebraic_keygen_time.txt"))) {
                writer.write(Long.toString(keyGenTime));
                writer.newLine();
                writer.write(Integer.toString(lambda)); // optional: store lambda
            }
            System.out.printf("Algebraic key generation took: %.2f ms%n", keyGenTime / 1e6);
            System.out.println("✅ Algebraic signature keys generated and saved.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
