import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class EnhancedVerificationUtils {

    // Read public keys from file
    public static Map<String, BigInteger> readPublicKeys(String filePath) throws IOException {
        Map<String, BigInteger> keys = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("=");
                if (parts.length == 2) {
                    keys.put(parts[0].trim(), new BigInteger(parts[1].trim()));
                }
            }
        }
        return keys;
    }

    // Read secret keys from file
    public static Map<String, BigInteger> readSecretKeys(String filePath) throws IOException {
        Map<String, BigInteger> keys = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("=");
                if (parts.length == 2) {
                    keys.put(parts[0].trim(), new BigInteger(parts[1].trim()));
                }
            }
        }
        return keys;
    }

    // Read (W, gamma) tags from CSV
    public static Map<String, String[]> readTagsFromCSV(String filePath) throws IOException {
        Map<String, String[]> tags = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Block")) continue; // Skip header
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    tags.put(parts[0].trim(), new String[]{parts[1].trim(), parts[2].trim()});
                }
            }
        }
        return tags;
    }

    // Read numeric block data by block index from labeled file
    public static String readBlockFromFile(String filePath, int blockIndex) throws IOException {
        String prefix = "Block " + blockIndex + ":";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(prefix)) {
                    return line.substring(prefix.length()).trim();
                }
            }
        }
        throw new IllegalArgumentException("Block index " + blockIndex + " not found in " + filePath);
    }

    // CSP: Generate W′ = H(fid || chi || block) mod q
    public static BigInteger generateW_dashWithFid(int blockIndex, BigInteger fid, BigInteger chi, BigInteger q, String dataFilePath) throws IOException {
        String blockStr = readBlockFromFile(dataFilePath, blockIndex);
        BigInteger blockData = new BigInteger(blockStr);
        return SchnorrUtils.computeWWithFid(fid, chi, blockData, q);
    }

    // Write proof to file (W′, fid, etc.)
    public static void writeProofToFile(String filePath, int blockIndex, BigInteger fid, BigInteger chi, BigInteger W_dash) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("blockIndex=" + blockIndex + "\n");
            writer.write("fid=" + fid.toString() + "\n");
            writer.write("chi=" + chi.toString() + "\n");
            writer.write("W_dash=" + W_dash.toString() + "\n");
        }
    }

    // Read proof from file
    public static Map<String, BigInteger> readProofFromFile(String filePath) throws IOException {
        Map<String, BigInteger> proof = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("=");
                if (parts.length == 2) {
                    proof.put(parts[0].trim(), new BigInteger(parts[1].trim()));
                }
            }
        }
        return proof;
    }

    // Compute gamma′ = (W′ * delta + xi) mod q
    public static BigInteger computeGammaPrime(BigInteger W_dash, BigInteger delta, BigInteger xi, BigInteger q) {
        return W_dash.multiply(delta).add(xi).mod(q);
    }
}
