import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TPAVerifier {
    public static Map<String, BigInteger> readPublicKeys(String filename) throws IOException {
        Map<String, BigInteger> keys = new HashMap<>();
        for (String line : Files.readAllLines(Paths.get(filename))) {
            if (line.contains("=")) {
                String[] parts = line.split("=");
                if (parts.length == 2 && parts[0] != null && parts[1] != null) {
                    try {
                        keys.put(parts[0].trim(), new BigInteger(parts[1].trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in line: " + line);
                    }
                }
            }
        }
        return keys;
    }

    public static Map<String, BigInteger> readProofFromFile(String filename) throws IOException {
        Map<String, BigInteger> map = new HashMap<>();
        for (String line : Files.readAllLines(Paths.get(filename))) {
            if (line.contains("=")) {
                String[] parts = line.split("=");
                if (parts.length == 2 && parts[0] != null && parts[1] != null) {
                    try {
                        map.put(parts[0].trim(), new BigInteger(parts[1].trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format in line: " + line);
                    }
                }
            }
        }
        return map;
    }

    public static Map<String, String[]> readTagsFromCSV(String filename) throws IOException {
        Map<String, String[]> tags = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3 && parts[0] != null && parts[1] != null && parts[2] != null) {
                tags.put(parts[0].trim(), new String[]{parts[1].trim(), parts[2].trim()});
            }
        }
        reader.close();
        return tags;
    }

    public static BigInteger computeGammaPrime(BigInteger W_prime, BigInteger delta, BigInteger chi, BigInteger q) {
        if (W_prime == null || delta == null || chi == null || q == null) {
            throw new IllegalArgumentException("Input parameters cannot be null");
        }
        return W_prime.multiply(delta).add(chi).mod(q);
    }

    public static boolean verify(String blockIndex, Map<String, BigInteger> publicKeys, Map<String, String[]> storedTags, Map<String, BigInteger> proof) {
        if (blockIndex == null || publicKeys == null || storedTags == null || proof == null) {
            throw new IllegalArgumentException("Input parameters cannot be null");
        }

        BigInteger delta = publicKeys.get("delta");
        BigInteger q = publicKeys.get("q");
        
        if (delta == null || q == null) {
            System.err.println("Required public keys 'delta' or 'q' not found");
            return false;
        }

        BigInteger W_prime = proof.get("W_dash");
        BigInteger chi = proof.get("chi");
        
        if (W_prime == null || chi == null) {
            System.err.println("Required proof values 'W_dash' or 'chi' not found");
            return false;
        }

        String[] tag = storedTags.get(blockIndex);
        if (tag == null) {
            System.err.println("Block index not found in CSV.");
            return false;
        }
        BigInteger W_stored = new BigInteger(tag[0]);
        if (!W_stored.equals(W_prime)) {
            System.err.println("W mismatch: Computed W' from CSP does not match stored W.");
            return false;
        }
        
        try {
           // BigInteger W_stored = new BigInteger(tag[0]);
            BigInteger gamma_stored = new BigInteger(tag[1]);

            BigInteger gamma_prime = computeGammaPrime(W_prime, delta, chi, q);
            return gamma_stored.equals(gamma_prime);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in stored tags for block " + blockIndex);
            return false;
        }
    }
}
