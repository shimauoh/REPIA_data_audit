import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
public class SchnorrUtils {
    public static BigInteger computeW(BigInteger chi, BigInteger blockData, BigInteger q) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(chi.toByteArray());
            digest.update(blockData.toByteArray());
            byte[] hashBytes = digest.digest();
            return new BigInteger(1, hashBytes).mod(q);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
// File identifier generation based on Algorithm 2
public static BigInteger computeFileIdentifier(String fileName, BigInteger p, BigInteger skh) throws Exception {
    // Step 1: Generate random a ∈ Zp
    SecureRandom random = new SecureRandom();
    BigInteger a;
    do {
        a = new BigInteger(p.bitLength(), random);
    } while (a.compareTo(p) >= 0 || a.equals(BigInteger.ZERO));

    // Step 2: Compute Tfile = Hsk(fileName) using HMAC-SHA256
    byte[] skBytes = skh.toByteArray();
    SecretKeySpec keySpec = new SecretKeySpec(skBytes, "HmacSHA256");
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(keySpec);
    byte[] tfileBytes = mac.doFinal(fileName.getBytes());

    // Step 3: fid = SHA-256(a ∥ Tfile)
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    digest.update(a.toByteArray());
    digest.update(tfileBytes);
    byte[] fidHashBytes = digest.digest();
    return new BigInteger(1, fidHashBytes).mod(p);
}
// Helper method: save fid to a text file
public static void saveFidToFile(BigInteger fid, String filePath) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        writer.write(fid.toString());
    } catch (IOException e) {
        throw new RuntimeException("Failed to save fid to file: " + e.getMessage(), e);
    }
}

// Helper method: read fid back from a text file
public static BigInteger readFidFromFile(String filePath) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line = reader.readLine();
        return new BigInteger(line.trim());
    } catch (IOException e) {
        throw new RuntimeException("Failed to read fid from file: " + e.getMessage(), e);
    }
}
    
    // Helper method: load fid from a text file
public static BigInteger loadFidFromFile(String filePath) {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line = reader.readLine();
        if (line != null && !line.trim().isEmpty()) {
            return new BigInteger(line.trim());
        } else {
            throw new RuntimeException("fid.txt is empty or missing content!");
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to load fid from file: " + e.getMessage(), e);
    }
}
public static BigInteger computeWWithFid(BigInteger fid,BigInteger chi, BigInteger blockData, BigInteger q) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(fid.toByteArray());
        digest.update(chi.toByteArray());
        digest.update(blockData.toByteArray());
        byte[] hashBytes = digest.digest();
        return new BigInteger(1, hashBytes).mod(q);
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("SHA-256 not available", e);
    }
}

    }
