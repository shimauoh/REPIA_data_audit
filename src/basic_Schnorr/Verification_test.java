import java.io.*;
import java.math.BigInteger;
//import java.nio.file.*;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.*;
import java.io.IOException;
//import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Verification_test {
    public static void main(String[] args) throws Exception {
        // Simulate a challenge from TPA
        int blockIndex = 3;
        String blockIndexStr = Integer.toString(blockIndex);
        //String filename = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\verificationResults.txt";

        // Load public parameters
        Map<String, BigInteger> publicKeys = readPublicKeys("public_keys.txt");
        BigInteger p = publicKeys.get("p");
       // BigInteger alpha = publicKeys.get("alpha");
        BigInteger chi = publicKeys.get("chi");
        BigInteger q = publicKeys.get("q");

        // Load secret keys (delta, xi) from file
        Map<String, BigInteger> secretKeys = readSecretKeys("secret_keys.txt");
        BigInteger delta = secretKeys.get("delta");
        BigInteger xi = secretKeys.get("xi");

        // CSP side: Proof generation with timing
        long cspStart = System.nanoTime();
        BigInteger W_prime = simulateCSP(blockIndex, chi, p, q, "numeric_data_blocks.txt");
        BigInteger dataBlock = new BigInteger(readBlockByIndexFromLabeledFile("numeric_data_blocks.txt", blockIndex));
        writeProofToFile("csp_proof_output.txt", blockIndex, dataBlock, chi, W_prime);  // Save proof
        long cspEnd = System.nanoTime();

        // TPA side: Load stored tag (W, gamma)
        Map<String, String[]> storedTags = readTagsFromCSV("Basic_block_signature.csv");
        String[] tag = storedTags.get(blockIndexStr);
        if (tag == null) {
            System.err.println("Block index not found in signature CSV.");
            return;
        }
        BigInteger W_stored = new BigInteger(tag[0]);
        BigInteger gamma_stored = new BigInteger(tag[1]);

        // TPA side: Load W′ from file
        Map<String, BigInteger> cspProof = readProofFromFile("csp_proof_output.txt");
        BigInteger W_prime_fromFile = cspProof.get("W_dash");

        // Debug: Print intermediate values
        System.out.println("W (stored): " + W_stored);
        System.out.println("W' (from CSP): " + W_prime_fromFile);
        System.out.println("γ (stored): " + gamma_stored);

        // TPA side: Verification with timing
        long tpaStart = System.nanoTime();
        BigInteger gamma_prime = computeGammaPrime(W_prime_fromFile, delta, xi, q);
        long tpaEnd = System.nanoTime();

        // Debug: Print gamma′
        System.out.println("γ (computed): " + gamma_prime);

        // Output results
        System.out.println("Block Index: " + blockIndex);
        System.out.println("Block Size: " + getBlockSize() + " bytes");

        if (!W_stored.equals(W_prime_fromFile)) {
            System.out.println("⚠ Warning: W′ differs from stored W. Possible data integrity issue.");
        }

        if (verifyGamma(gamma_stored, gamma_prime)) {
            System.out.println("✔ Verification successful");
        } else {
            System.out.println("✘ Verification failed");
        }

        // Output time measurements
        System.out.printf("CSP Time (proof generation): %d ns (%.2f ms)%n", (cspEnd - cspStart), (cspEnd - cspStart) / 1e6);
        System.out.printf("TPA Time (verification): %d ns (%.2f ms)%n", (tpaEnd - tpaStart), (tpaEnd - tpaStart) / 1e6);
    }

    // Compute gamma′ = (W′ * delta + xi) mod q
    private static BigInteger computeGammaPrime(BigInteger W_prime, BigInteger delta, BigInteger xi, BigInteger q) {
        return W_prime.multiply(delta).add(xi).mod(q);
    }

    // Verify gamma values
    private static boolean verifyGamma(BigInteger gammaStored, BigInteger gammaPrime) {
        return gammaStored.equals(gammaPrime);
    }

    // Read secret keys from file
    private static Map<String, BigInteger> readSecretKeys(String filename) throws IOException {
        Map<String, BigInteger> keys = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && line.contains("=")) {
                String[] parts = line.split("=", 2);
                keys.put(parts[0].trim(), new BigInteger(parts[1].trim()));
            }
        }
        return keys;
    }

    // Read public keys from file
    private static Map<String, BigInteger> readPublicKeys(String filename) throws IOException {
        Map<String, BigInteger> keys = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && line.contains("=")) {
                String[] parts = line.split("=", 2);
                keys.put(parts[0].trim(), new BigInteger(parts[1].trim()));
            }
        }
        return keys;
    }

    // Simulate CSP: Compute W' = H(chi || block) mod q
    public static BigInteger simulateCSP(int blockIndex, BigInteger chi, BigInteger p, BigInteger q, String blockFile) throws IOException {
        BigInteger blockData = new BigInteger(readBlockByIndexFromLabeledFile("numeric_data_blocks.txt", blockIndex));
       return SchnorrUtils.computeW(chi, blockData, q);}
/*
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

    // Read numeric data block from file
    /*private static String readBlockFromFile(String filename, String index) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        int idx = Integer.parseInt(index);
        if (idx >= 0 && idx < lines.size()) {
            String line = lines.get(idx);
            // Extract the number after the colon
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                return parts[1].trim();
            }
            throw new IllegalArgumentException("Invalid block format: " + line);
        } else {
            throw new IllegalArgumentException("Invalid block index");
        }
    }
*/
public static String readBlockByIndexFromLabeledFile(String filename, int blockIndex) throws IOException {
    List<String> lines = Files.readAllLines(Paths.get(filename));
    String targetLabel = "Block " + blockIndex + ":";

    for (String line : lines) {
        if (line.startsWith(targetLabel)) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                return parts[1].trim();
            } else {
                throw new IllegalArgumentException("Malformed line: " + line);
            }
        }
    }

    throw new IllegalArgumentException("Block with index " + blockIndex + " not found.");
}

    // Write CSP's generated proof to file
    private static void writeProofToFile(String filename, int blockIndex, BigInteger dataBlock, BigInteger chi, BigInteger W_dash) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("blockIndex=" + blockIndex + "\n");
            writer.write("dataBlock=" + dataBlock.toString() + "\n");
            writer.write("chi=" + chi.toString() + "\n");
            writer.write("W_dash=" + W_dash.toString() + "\n");
        }
    }

    // Read (W, gamma) tags from CSV
    private static Map<String, String[]> readTagsFromCSV(String filename) throws IOException {
        Map<String, String[]> tags = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3)
                    tags.put(parts[0].trim(), new String[]{parts[1].trim(), parts[2].trim()});
            }
        }
        return tags;
    }

    // Read CSP's proof from file
    private static Map<String, BigInteger> readProofFromFile(String filename) throws IOException {
        Map<String, BigInteger> values = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("=")) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String valueStr = parts[1].trim();
                    try {
                        BigInteger value = new BigInteger(valueStr);
                        values.put(key, value);
                    } catch (NumberFormatException e) {
                        // Skip non-numeric values like blockIndex or dataBlock
                    }
                }
            }
        }
        reader.close();
        return values;
    }

    // Read block size from Basic_time_measurements.txt
    private static int getBlockSize() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("Basic_time_measurements.txt"));
        for (String line : lines) {
            if (line.startsWith("Block Size:")) {
                return Integer.parseInt(line.split(":")[1].trim().split(" ")[0]);
            }
        }
        throw new IOException("Block size not found in Basic_time_measurements.txt");
    }
}
