import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.*;

public class EnhancedVerificationTest {
    public static void main(String[] args) throws Exception {
        // Block index to verify
        int blockIndex = 2;
        String blockIndexStr = Integer.toString(blockIndex);

        // Load public keys
        Map<String, BigInteger> publicKeys = readPublicKeys("public_keys.txt");
        BigInteger p = publicKeys.get("p");
        BigInteger q = publicKeys.get("q");
        BigInteger chi = publicKeys.get("chi");

        // Load secret keys
        Map<String, BigInteger> secretKeys = readSecretKeys("secret_keys.txt");
        BigInteger delta = secretKeys.get("delta");
        BigInteger xi = secretKeys.get("xi");
        BigInteger skh = secretKeys.get("skh");

        // Compute fid (once per file)
        String fileName = "Dna_seq50kb.fasta";
        BigInteger fid = SchnorrUtils.computeFileIdentifier(fileName, p, skh);

        // CSP side: proof generation with timing
        long cspStart = System.nanoTime();
        BigInteger W_prime = EnhancedCSPProofGenerator.simulateCSP(
                blockIndex, fid, chi, p, q, "numeric_data_blocks.txt"
        );
        //BigInteger blockData = new BigInteger
        //String blockData = EnhancedCSPProofGenerator.readBlockByIndexFromLabeledFile("numeric_data_blocks.txt", blockIndex);
              //  EnhancedCSPProofGenerator.readBlockByIndexFromLabeledFile("numeric_data_blocks.txt", blockIndex)
        //);
        // Save proof to file
        EnhancedCSPProofGenerator.writeProofToFile(
                "enhanced_csp_proof_output.txt",
                String.valueOf(blockIndex), fid, chi, q, W_prime
        );
        long cspEnd = System.nanoTime();

        // TPA side: read stored tags
        Map<String, String[]> storedTags = readTagsFromCSV("enhanced_block_signature.csv");
        String[] tag = storedTags.get(blockIndexStr);
        if (tag == null) {
            System.err.println("Block index not found in signature CSV.");
            return;
        }
        BigInteger W_stored = new BigInteger(tag[0]);
        BigInteger gamma_stored = new BigInteger(tag[1]);

        // Read proof from CSP file
        Map<String, BigInteger> cspProof = readProofFromFile("enhanced_csp_proof_output.txt");
        BigInteger W_prime_fromFile = cspProof.get("W_dash");

        // Print debug
        System.out.println("W (stored): " + W_stored);
        System.out.println("W' (from CSP): " + W_prime_fromFile);
        System.out.println("γ (stored): " + gamma_stored);

        // TPA: compute γ′
        long tpaStart = System.nanoTime();
        BigInteger gamma_prime = computeGammaPrime(W_prime_fromFile, delta, xi, q);
        long tpaEnd = System.nanoTime();

        System.out.println("γ (computed): " + gamma_prime);

        // Compare
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

    // Compute γ′ = (W′ * delta + xi) mod q
    private static BigInteger computeGammaPrime(BigInteger W_prime, BigInteger delta, BigInteger xi, BigInteger q) {
        return W_prime.multiply(delta).add(xi).mod(q);
    }

    private static boolean verifyGamma(BigInteger gammaStored, BigInteger gammaPrime) {
        return gammaStored.equals(gammaPrime);
    }

    private static Map<String, BigInteger> readPublicKeys(String filename) throws IOException {
        Map<String, BigInteger> keys = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            if (!line.isEmpty() && line.contains("=")) {
                String[] parts = line.split("=", 2);
                keys.put(parts[0].trim(), new BigInteger(parts[1].trim()));
            }
        }
        return keys;
    }

    private static Map<String, BigInteger> readSecretKeys(String filename) throws IOException {
        Map<String, BigInteger> keys = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            if (!line.isEmpty() && line.contains("=")) {
                String[] parts = line.split("=", 2);
                keys.put(parts[0].trim(), new BigInteger(parts[1].trim()));
            }
        }
        return keys;
    }

    private static Map<String, String[]> readTagsFromCSV(String filename) throws IOException {
        Map<String, String[]> tags = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    tags.put(parts[0].trim(), new String[]{parts[1].trim(), parts[2].trim()});
                }
            }
        }
        return tags;
    }

    private static Map<String, BigInteger> readProofFromFile(String filename) throws IOException {
        Map<String, BigInteger> values = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            if (!line.isEmpty() && line.contains("=")) {
                String[] parts = line.split("=", 2);
                String key = parts[0].trim();
                try {
                    BigInteger value = new BigInteger(parts[1].trim());
                    values.put(key, value);
                } catch (NumberFormatException e) {
                    // Ignore non-numeric like blockIndex
                }
            }
        }
        return values;
    }
}
