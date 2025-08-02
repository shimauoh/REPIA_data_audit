import java.math.BigInteger;
import java.io.*;
import java.util.Map;

public class AlgebraicSignatureTest {
    public static void main(String[] args) {
        try {
            String inputFile = "Dna_seq50kb.fasta";
            String blockFile = "algebraic_blocks.txt";
            String signatureFile = "algebraic_signatures.csv";
            String timingFile = "algebraic_time_measurements.txt";
            int blockSize = 512; // example

            // Load keys
            Map<String, BigInteger> pub = KeyLoader.readPublicKeys("algebraic_public_keys.txt");
            Map<String, BigInteger> sec = KeyLoader.readSecretKeys("algebraic_secret_keys.txt");
            BigInteger p = pub.get("p");
            BigInteger g = sec.get("g");

            // Divide file into blocks
            FileDivider divider = new FileDivider();
            String[] blocks = divider.divideFileToStrings(inputFile, blockSize, blockFile);

            // Sign all blocks and measure time
            long startTime = System.nanoTime();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(signatureFile))) {
                writer.write("Block,Signature\n");
                for (int i = 0; i < blocks.length; i++) {
                    BigInteger tag = AlgebraicSignature.generateTag(blocks[i], g, p);
                    writer.write((i + 1) + "," + tag.toString() + "\n");
                }
            }
            long endTime = System.nanoTime();

            long totalNs = endTime - startTime;
            double totalMs = totalNs / 1e6;
            double avgPerBlockMs = totalMs / blocks.length;

            // Signature size
            int sigBits = p.bitLength();
            int sigBytes = (int) Math.ceil(sigBits / 8.0);

            try (BufferedWriter t = new BufferedWriter(new FileWriter(timingFile))) {
                t.write("Total signature time: " + totalMs + " ms\n");
                t.write("Average per block: " + avgPerBlockMs + " ms\n");
                t.write("Signature size: " + sigBits + " bits (" + sigBytes + " bytes)\n");
            }

            System.out.println("✅ Algebraic signature test complete. Signatures saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
