import java.math.BigInteger;
import java.security.SecureRandom;
import java.io.*;

public class KeyGenerator {
    private BigInteger p, q, alpha, delta, nu, chi, xi,skh; // chi = alpha^xi mod p
    private final SecureRandom random = new SecureRandom();
    private final String publicKeyPath = "public_keys.txt";
    private final String secretKeyPath = "secret_keys.txt";
    private long publicKeyTimeNs;
    private long secretKeyTimeNs;
    private long secretFidKeyTimeNs;

    public void generateKeys(int lambda) {
        //long start = System.nanoTime();
       int qBitLength = Math.max(140, lambda);
       int pBitLength = 2 * lambda;

        //int pBitLength = Math.max(512, 2 * lambda);
      // int qBitLength = (lambda < 140) ? 140 : lambda;
      // int pBitLength = (2 * lambda < 512) ? 512 : 2 * lambda;
       
        System.out.println("Security parameter (Lambda): " + lambda + " bits");
        
        // Step 1: Generate q and p such that q | (p - 1)
        q = BigInteger.probablePrime(qBitLength, random);
        System.out.println("Generated q: " + q);

        BigInteger k;
        do {
            k = new BigInteger(pBitLength - qBitLength, random);
            p = q.multiply(k).add(BigInteger.ONE);
        } while (!p.isProbablePrime(50) || p.bitLength() < pBitLength);

        System.out.println("Generated p: " + p);

        // Step 2: Generate alpha ∈ G of order q
        BigInteger g;
        do {
            g = new BigInteger(p.bitLength() - 1, random);
        } while (g.compareTo(BigInteger.TWO) < 0 || g.compareTo(p.subtract(BigInteger.ONE)) >= 0);

        alpha = g.modPow(p.subtract(BigInteger.ONE).divide(q), p);
        while (alpha.equals(BigInteger.ONE)) {
            g = new BigInteger(p.bitLength() - 1, random);
            alpha = g.modPow(p.subtract(BigInteger.ONE).divide(q), p);
        }

        // Step 3: Generate private key delta ∈ Zq and additional secret xi ∈ Zq
         long secretStart = System.nanoTime();
        // SecureRandom random = new SecureRandom();
        // q = BigInteger.probablePrime(qBitLength, random);
         delta = new BigInteger(qBitLength - 1, random);
         xi = new BigInteger(qBitLength - 1, random);
         long secretEnd = System.nanoTime();
         secretKeyTimeNs = secretEnd - secretStart;

         // generate skh as random number in Zq or Zp, same style
       
         long secretSkhStart =System.nanoTime();
         
         do {
           // skh = new BigInteger(lambda, random);
            skh = new BigInteger(p.bitLength(), random);
        } while (skh.compareTo(p) >= 0 || skh.equals(BigInteger.ZERO));
        
        System.out.println("DEBUG: skh generated: " + skh);
        long secretSkhEnd = System.nanoTime();
        secretFidKeyTimeNs = secretSkhEnd - secretSkhStart;
        
       // System.out.println("Key generation time: " + duration + " nanoseconds");
       // Store keys

         long publicStart = System.nanoTime();
        
             p = q.multiply(k).add(BigInteger.ONE);
       
         nu = alpha.modPow(delta, p);
         chi = alpha.modPow(xi, p);

         long publicEnd = System.nanoTime();
         publicKeyTimeNs = publicEnd - publicStart;
         storeSecretKeys();
         storePublicKeys();
    }
    long globalEnd = System.nanoTime();
    
    //System.out.printf("Total key generation time: %d ns%n", globalEnd - globalStart);
    //System.out.printf("Secret key generation time: %d ns%n", secretKeyTimeNs);
    //System.out.printf("Public key generation time: %d ns%n",  publicKeyTimeNs);

 // Accessors for timing
 public long getSecretKeyTimeNs() {
    return secretKeyTimeNs;
}

 public long getPublicKeyTimeNs() {
    return publicKeyTimeNs;
}

public long getFidKeyTimeNs() {
    return secretFidKeyTimeNs;
}
   
      //storeKeyLengths();
      // Output
      // System.out.println("\n===== Key Generation Complete =====");
      //    System.out.println("p: " + p);
      //    System.out.println("q: " + q);
      //    System.out.println("alpha: " + alpha);
      //    System.out.println("delta (private): " + delta);
      //  System.out.println("xi (private): " + xi);
      //  System.out.println("nu (public): " + nu);
      //  System.out.println("chi (private): " + chi);
      //  System.out.println("skh (private): " + skh);
    
     private void storeSecretKeys() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(secretKeyPath))) {
            writer.println("delta=" + delta.toString());
            writer.println("xi=" + xi.toString());
            writer.println("skh=" + skh.toString());
            System.out.println("Secret keys saved to " + secretKeyPath);
        } catch (IOException e) {
            System.err.println("Error writing to secret key file: " + e.getMessage());
        }
    }

    private void storePublicKeys() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(publicKeyPath))) {
            writer.println("p=" + p.toString());
            writer.println("q=" + q.toString());
            writer.println("alpha=" + alpha.toString());
            writer.println("nu=" + nu.toString());
            writer.println("chi=" + chi.toString());
            System.out.println("Public keys saved to " + publicKeyPath);
        } catch (IOException e) {
            System.err.println("Error writing to public key file: " + e.getMessage());
        }
    }

  /*  private void storeKeyLengths() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Basic_time_measurements.txt", true))) {
            writer.println("\n===== Key Lengths =====");
            writer.println("Bit length of p: " + p.bitLength());
            writer.println("Bit length of q: " + q.bitLength());
            writer.println("Bit length of alpha: " + alpha.bitLength());
            writer.println("Bit length of delta (private): " + delta.bitLength());
            writer.println("Bit length of xi (private): " + xi.bitLength());
            writer.println("Bit length of nu (public): " + nu.bitLength());
            writer.println("Bit length of chi (public): " + chi.bitLength());
            writer.println("Bit length of skh (private): " + skh.bitLength());
            writer.println("========================\n");
        } catch (IOException e) {
            System.err.println("Error writing key lengths: " + e.getMessage());
        }
    }*/

    // Getters
    public BigInteger getP() { return p; }
    public BigInteger getQ() { return q; }
    public BigInteger getAlpha() { return alpha; }
    public BigInteger getDelta() { return delta; }
    public BigInteger getNu() { return nu; }
    public BigInteger getChi() { return chi; }
    public BigInteger getXi() { return xi; }
    public BigInteger getSkh() { return skh; }
    
    private void saveKeygenTime(long ns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("keygen_time.txt"))) {
            writer.write(Long.toString(ns));
        } catch (IOException e) {
            System.err.println("Error saving key generation time: " + e.getMessage());
        }
    }

   // public static void main(String[] args) {
     //   KeyGenerator generator = new KeyGenerator();
      //  generator.generateKeys(256); // Use 256-bit security parameter
    //}
}
