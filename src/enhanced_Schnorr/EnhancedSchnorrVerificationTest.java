//import java.io.*;
import java.math.BigInteger;
import java.util.Map;

public class EnhancedSchnorrVerificationTest {

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        int blockIndex = 3; // Example block index to verify
        String blockIndexStr = Integer.toString(blockIndex);

        // Step 1: Load public & secret keys
        Map<String, BigInteger> publicKeys = EnhancedVerificationUtils.readPublicKeys("public_keys.txt");
        Map<String, BigInteger> secretKeys = EnhancedVerificationUtils.readSecretKeys("secret_keys.txt");

        BigInteger p = publicKeys.get("p");
        BigInteger q = publicKeys.get("q");
        BigInteger chi = publicKeys.get("chi");
        BigInteger delta = secretKeys.get("delta");
        BigInteger xi = secretKeys.get("xi");
        BigInteger skh = secretKeys.get("skh");

        if (skh == null) {
            throw new IllegalStateException("ERROR: skh is missing in secret_keys.txt!");
        }

        // Step 2: Compute fid once per file
       // String fileName = "Dna_seq50kb.fasta"; // or your actual file name
        //BigInteger fid = SchnorrUtils.computeFileIdentifier(fileName, p, skh);
        BigInteger fid = SchnorrUtils.readFidFromFile("fid.txt");
        // Step 3: CSP side — generate proof (W′)
        System.out.println("Generating W′ from CSP...");
        long cspStart = System.nanoTime();
        BigInteger W_prime = EnhancedVerificationUtils.generateW_dashWithFid(
            blockIndex, fid, chi, q, "numeric_data_blocks.txt"
        );
        long cspEnd = System.nanoTime();

        // Step 4: Save proof to file (simulate CSP sending proof)
        EnhancedVerificationUtils.writeProofToFile(
            "enhanced_csp_proof_output.txt", blockIndex, fid, chi, W_prime
        );

        // Step 5: TPA side — load stored (W, gamma) from CSV
        Map<String, String[]> storedTags = EnhancedVerificationUtils.readTagsFromCSV("enhanced_block_signature.csv");
        String[] tag = storedTags.get(blockIndexStr);
        if (tag == null) {
            System.err.println("❌ Block index not found in signature CSV.");
            return;
        }

        BigInteger W_stored = new BigInteger(tag[0]);
        BigInteger gamma_stored = new BigInteger(tag[1]);

        // Step 6: Load proof back from file
        Map<String, BigInteger> proof = EnhancedVerificationUtils.readProofFromFile("enhanced_csp_proof_output.txt");
        BigInteger W_dash_fromProof = proof.get("W_dash");

        // Step 7: Compute γ′ on TPA side
        long tpaStart = System.nanoTime();
        BigInteger gamma_prime = EnhancedVerificationUtils.computeGammaPrime(W_dash_fromProof, delta, xi, q);
        long tpaEnd = System.nanoTime();

        // Debug: print values
        System.out.println("W (stored):       " + W_stored);
        System.out.println("W′ (computed):    " + W_dash_fromProof);
        System.out.println("γ (stored):       " + gamma_stored);
        System.out.println("γ′ (computed):    " + gamma_prime);

        // Step 8: Check matching
        if (!W_stored.equals(W_dash_fromProof)) {
            System.out.println("⚠ Warning: W′ differs from stored W!");
        }

        if (gamma_stored.equals(gamma_prime)) {
            System.out.println("✔ Verification successful.");
        } else {
            System.out.println("✘ Verification failed.");
        }

        // Step 9: Print time measurements
        System.out.printf("CSP proof generation time: %d ns (%.2f ms)%n", (cspEnd - cspStart), (cspEnd - cspStart) / 1e6);
        System.out.printf("TPA verification time:     %d ns (%.2f ms)%n", (tpaEnd - tpaStart), (tpaEnd - tpaStart) / 1e6);
    }
}

/*import java.math.BigInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

public class EnhancedSchnorrVerificationTest {
    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        try {
            String inputFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\Dna_seq50kb.fasta";
            String numericDataBlocksFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\numeric_data_blocks.txt";
            String signatureFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\enhanced_block_signature.csv";
            String verificationTimeFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\enhanced_verification_time.txt";

            int blockSize = 512;

            // Load public keys and secret keys
            Map<String, BigInteger> publicKeys = VerificationUtils.readPublicKeys("public_keys.txt");
            Map<String, String[]> storedTags = VerificationUtils.readTagsFromCSV(signatureFilePath);

            BigInteger p = publicKeys.get("p");
            BigInteger q = publicKeys.get("q");
            BigInteger chi = publicKeys.get("chi");

            // Load skh from secret keys to compute fid
            Map<String, BigInteger> secretKeys = KeyLoader.readSecretKeys("secret_keys.txt");
            BigInteger delta = secretKeys.get("delta");
            BigInteger skh = secretKeys.get("skh");
           

           // ===============
          // Map<String, BigInteger> secretKeys = readSecretKeys("secret_keys.txt");
          // BigInteger sk = secretKeys.get("skh");
           System.out.println("DEBUG: skh loaded from file: " + skh);
           if (skh == null) {
               throw new IllegalStateException("skh is missing from secret_keys.txt!");
           }
           
          //  ===========
            // Compute fid once
            String fileName = "Dna_seq50kb.fasta";
            BigInteger fid = SchnorrUtils.computeFileIdentifier(fileName, p, skh);

            // Divide file into blocks (need numeric data for verification)
            FileDivider fileDivider = new FileDivider();
            byte[][] blocks = fileDivider.divideFile(inputFilePath, blockSize, numericDataBlocksFilePath, null);

            System.out.println("Starting Enhanced verification...");

            long verificationStartTime = System.nanoTime();

            int verifiedCount = 0;
            for (int i = 0; i < blocks.length; i++) {
                String blockIndex = String.valueOf(i + 1);
                BigInteger blockData = new BigInteger(1, blocks[i]);

                boolean verified = EnhancedTPAVerifier.verifyBlock(
                        blockIndex, fid, chi, delta, q, storedTags, blockData
                );
                if (verified) {
                    verifiedCount++;
                } else {
                    System.err.println("Block " + blockIndex + " failed verification.");
                }
            }

            long verificationEndTime = System.nanoTime();
            long verificationTime = verificationEndTime - verificationStartTime;
            double avgTimePerBlock = (double) verificationTime / blocks.length;

            // Write timing info
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(verificationTimeFilePath))) {
                writer.write("Total blocks: " + blocks.length + "\n");
                writer.write("Verified blocks: " + verifiedCount + "\n");
                writer.write("Verification Time: " + verificationTime + " ns\n");
                writer.write("Verification Time: " + verificationTime / 1_000_000 + " ms\n");
                writer.write("Average verification time per block: " + avgTimePerBlock + " ns\n");
                writer.write("Average verification time per block: " + avgTimePerBlock / 1_000_000 + " ms\n");
                writer.flush();
            }

            System.out.println("Enhanced verification complete: " + verifiedCount + "/" + blocks.length + " blocks verified.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
*/