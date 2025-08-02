import java.math.BigInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

public class BasicSchnorrSignatureTest {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        try {
            String inputFilePath = "Dna_seq50kb.fasta";
            String numericDataBlocksFilePath = "numeric_data_blocks.txt";
            String letterDataBlocksFilePath = "letter_data_blocks.txt";
            String bsignatureOutputFilePath = "Basic_block_signature.csv";
            String btimeMeasurementFilePath = "Basic_time_measurements.txt";

            int blockSize = 512; // match benchmark!
            int lambda = 256;

            // Step 1: Divide file into blocks
            FileDivider fileDivider = new FileDivider();
            byte[][] blocks = fileDivider.divideFile(inputFilePath, blockSize,
                    numericDataBlocksFilePath, letterDataBlocksFilePath);

            // Step 2: Load keys (do not generate new keys!)
            Map<String, BigInteger> publicKeys = KeyLoader.readPublicKeys("public_keys.txt");
            Map<String, BigInteger> secretKeys = KeyLoader.readSecretKeys("secret_keys.txt");
            BigInteger q = publicKeys.get("q");
            BigInteger delta = secretKeys.get("delta");

            // Step 3: Measure only signature generation
            System.out.println("Starting tag generation...");
            long sigStart = System.nanoTime();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(bsignatureOutputFilePath))) {
                writer.write("Block,W,gamma\n");
                for (int i = 0; i < blocks.length; i++) {
                    BigInteger blockData = new BigInteger(1, blocks[i]);
                    BasicSchnorrSignature.Tag tag = BasicSchnorrSignature.generateTag(blockData);
                    writer.write((i + 1) + "," + tag.W + "," + tag.gamma + "\n");
                }
            }

            long sigEnd = System.nanoTime();
            long totalSigTime = sigEnd - sigStart;
            double avgSigPerBlock = (double) totalSigTime / blocks.length;

            // Step 4: Write timing info
            try (BufferedWriter timeWriter = new BufferedWriter(new FileWriter(btimeMeasurementFilePath))) {
                timeWriter.write("Signature Generation Time: " + totalSigTime + " ns\n");
                timeWriter.write("Signature Generation Time: " + totalSigTime / 1_000_000.0 + " ms\n");
                timeWriter.write("Average Signature Generation Time per Block: " + avgSigPerBlock + " ns\n");
                timeWriter.write("Average Signature Generation Time per Block: " + avgSigPerBlock / 1_000_000.0 + " ms\n");
                timeWriter.write("Block Size: " + blockSize + " bytes\n");
                timeWriter.write("Tag Size (W, gamma): " + (q.bitLength() * 2) + " bits\n");
                timeWriter.write("Security parameter (Lambda): " + lambda + " bits\n");
            }

            System.out.println("✅ Done! Signature generation measured cleanly.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*import java.math.BigInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.Map;
//import java.util.HashMap;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
public class BasicSchnorrSignatureTest {
    public static void main(String[] args) throws Exception {
        run(); 
        }
    //public static void main(String[] args) throws Exception {
        public static void run() throws Exception {
         try{
            // File paths and block size
            String inputFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\Dna_seq50kb.fasta";
            String numericDataBlocksFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\numeric_data_blocks.txt";
            String letterDataBlocksFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\letter_data_blocks.txt";
            String bsignatureOutputFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\Basic_block_signature.csv";
            String btimeMeasurementFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\Basic_time_measurements.txt";
            //String filename = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\public_keys.txt";
            int blockSize = 1024;
            int lambda = 256; // 
            // 1. Divide the input file into blocks
            FileDivider fileDivider = new FileDivider();
            byte[][] blocks = fileDivider.divideFile(inputFilePath, blockSize, numericDataBlocksFilePath, letterDataBlocksFilePath);

            // 2. Generate Schnorr keys
            Map<String, BigInteger> publicKeys = KeyLoader.readPublicKeys("public_keys.txt");
            Map<String, BigInteger> secretKeys = KeyLoader.readSecretKeys("secret_keys.txt");
            BigInteger q = publicKeys.get("q");
            BigInteger delta = secretKeys.get("delta");
            BigInteger p = publicKeys.get("p");
            //BigInteger p = publicKeys.get("p");
            // etc.
            
            long keyStartTime = System.nanoTime();
            KeyGenerator keyGen = new KeyGenerator();
            keyGen.generateKeys(lambda);
            //BigInteger p = keyGen.getP();
            //BigInteger q = keyGen.getQ();
            BigInteger alpha = keyGen.getAlpha();
           // BigInteger delta = keyGen.getDelta();
            long keyEndTime = System.nanoTime();
            long keyGenerationTime = keyEndTime - keyStartTime;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("key_lengths.txt"))) {
                writer.write("===== Key Lengths =====\n");
                writer.write("Bit length of p: " + keyGen.getP().bitLength() + "\n");
                writer.write("Bit length of q: " + keyGen.getQ().bitLength() + "\n");
                writer.write("Bit length of alpha: " + keyGen.getAlpha().bitLength() + "\n");
                writer.write("Bit length of delta (private): " + keyGen.getDelta().bitLength() + "\n");
                writer.write("Bit length of xi (private): " + keyGen.getXi().bitLength() + "\n");
                writer.write("Bit length of nu (public): " + keyGen.getNu().bitLength() + "\n");
                writer.write("Bit length of chi (public): " + keyGen.getChi().bitLength() + "\n");
                writer.write("Bit length of skh (private): " + keyGen.getSkh().bitLength() + "\n");
                writer.write("========================\n");
                System.out.println("Key lengths written to key_lengths.txt");
            } catch (IOException e) {
                System.err.println("Error writing key lengths: " + e.getMessage());
            }

            // 3. Generate signatures using Basic Schnorr Tag (W, γ)
            System.out.println("Starting Tag generation...");
            long signatureStartTime = System.nanoTime();
            // Write (W, gamma) tags to CSV
            try (BufferedWriter signatureWriter = new BufferedWriter(new FileWriter(bsignatureOutputFilePath))) {
                signatureWriter.write("Block,W,gamma"); // CSV header
                signatureWriter.newLine();
                for (int i = 0; i < blocks.length; i++) {
                    BigInteger blockData = new BigInteger(1, blocks[i]);
                    BasicSchnorrSignature.Tag tag = BasicSchnorrSignature.generateTag(blockData);
                    signatureWriter.write((i + 1) + "," + tag.W + "," + tag.gamma); // CSV format
                    signatureWriter.newLine();
                }
            }
            long signatureEndTime = System.nanoTime();
            long signatureGenerationTime = signatureEndTime - signatureStartTime;
            double averageTimePerBlock = (double) signatureGenerationTime / blocks.length;
          //  keyGen.savePublicKeysToFile("public_keys.txt");
//System.out.println(tag.w);
            // 4. Write timing info to file
           // System.out.println("Writing time measurements...");
            try (BufferedWriter timeWriter = new BufferedWriter(new FileWriter(btimeMeasurementFilePath))) {
                System.out.println("Writing to: " + btimeMeasurementFilePath);
                timeWriter.write("Key Generation Time: " + keyGenerationTime + " ns\n");
                timeWriter.write("Key Generation Time: " + keyGenerationTime/1000000 + " ms\n");
                timeWriter.write("Signature Generation Time: " + signatureGenerationTime + " ns\n");
                timeWriter.write("Signature Generation Time: " + signatureGenerationTime/1000000 + " ms\n");
                timeWriter.write("Average Signature Generation Time per Block: " + averageTimePerBlock + " ns\n");
                timeWriter.write("Average Signature Generation Time per Block: " + averageTimePerBlock/1000000 + " ms\n");
                timeWriter.write("Key Size (delta): " + delta.bitLength() + " bits\n");
                timeWriter.write("Block Size: " + blockSize + " bytes\n");
                timeWriter.write("Tag Size (W, gamma): " + (q.bitLength() * 2) + " bits\n");
                timeWriter.write("Security parameter (Lambda): " + lambda + " bits\n");
                timeWriter.write("Bit length of q: " + keyGen.getQ().bitLength() + "\n");
                timeWriter.write("Bit length of p: " + keyGen.getP().bitLength() + "\n");
                timeWriter.flush();
                //System.out.println("Finished writing time measurements.");
            }

          //  System.out.println(averageTimePerBlock);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}*/
