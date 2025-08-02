import java.math.BigInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

public class EnhancedSchnorrSignatureTest {
    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        try {
            String inputFilePath = "Dna_seq50kb.fasta";
            String numericDataBlocksFilePath = "numeric_data_blocks.txt";
            String letterDataBlocksFilePath = "letter_data_blocks.txt";
            String signatureOutputFilePath = "enhanced_block_signature.csv";
            String timeMeasurementFilePath = "enhanced_time_measurements.txt";

            int blockSize = 512;   // match benchmark
            int lambda = 256;

            // 1. Divide file
            FileDivider fileDivider = new FileDivider();
            byte[][] blocks = fileDivider.divideFile(
                    inputFilePath, blockSize,
                    numericDataBlocksFilePath, letterDataBlocksFilePath);

            // 2. Load keys
            Map<String, BigInteger> publicKeys = KeyLoader.readPublicKeys("public_keys.txt");
            Map<String, BigInteger> secretKeys = KeyLoader.readSecretKeys("secret_keys.txt");
            BigInteger p = publicKeys.get("p");
            BigInteger skh = secretKeys.get("skh");

            // 3. Compute fid outside timing
            String fileName = "Dna_seq50kb.fasta";
            System.out.println("Starting enhanced tag generation...");
            long sigStart = System.nanoTime();
            BigInteger fid = SchnorrUtils.computeFileIdentifier(fileName, p, skh);
            SchnorrUtils.saveFidToFile(fid, "fid.txt");

           

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(signatureOutputFilePath))) {
                writer.write("Block,W,gamma\n");
                for (int i = 0; i < blocks.length; i++) {
                    BigInteger blockData = new BigInteger(1, blocks[i]);
                    EnhancedSchnorrSignature.Tag tag = EnhancedSchnorrSignature.generateTag(blockData, fid);
                    writer.write((i + 1) + "," + tag.W + "," + tag.gamma + "\n");
                }
            }

            long sigEnd = System.nanoTime();
            long totalSigTime = sigEnd - sigStart;
            double avgSigPerBlock = (double) totalSigTime / blocks.length;

            // Write timing info
            try (BufferedWriter timeWriter = new BufferedWriter(new FileWriter(timeMeasurementFilePath))) {
                timeWriter.write("Signature Generation Time: " + totalSigTime + " ns\n");
                timeWriter.write("Signature Generation Time: " + totalSigTime / 1_000_000.0 + " ms\n");
                timeWriter.write("Average Signature Generation Time per Block: " + avgSigPerBlock + " ns\n");
                timeWriter.write("Average Signature Generation Time per Block: " + avgSigPerBlock / 1_000_000.0 + " ms\n");
                timeWriter.write("Block Size: " + blockSize + " bytes\n");
                timeWriter.write("Security parameter (Lambda): " + lambda + " bits\n");
            }

            System.out.println("✅ Enhanced execution complete. Times and signatures saved.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

/*import java.math.BigInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

public class EnhancedSchnorrSignatureTest {
    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        try {
            // File paths and block size
            String inputFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\Dna_seq50kb.fasta";
            String numericDataBlocksFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\numeric_data_blocks.txt";
            String letterDataBlocksFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\letter_data_blocks.txt";
            String signatureOutputFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\enhanced_block_signature.csv";
            String timeMeasurementFilePath = "C:\\Users\\sham2\\OneDrive\\Desktop\\SchnorrSignature\\enhanced_time_measurements.txt";

            int blockSize = 1024;
            int lambda = 256;

            // 1. Divide the input file into blocks
            FileDivider fileDivider = new FileDivider();
            byte[][] blocks = fileDivider.divideFile(inputFilePath, blockSize, numericDataBlocksFilePath, letterDataBlocksFilePath);

            // 2. Generate Schnorr keys
          //  long keyStartTime = System.nanoTime();
          //  KeyGenerator keyGen = new KeyGenerator();
           // keyGen.generateKeys(lambda);
           // BigInteger q = keyGen.getQ();
           // BigInteger delta = keyGen.getDelta();
           // long keyEndTime = System.nanoTime();
           // long keyGenerationTime = keyEndTime - keyStartTime;

            // Load p and sk from keys to compute fid
            Map<String, BigInteger> publicKeys = KeyLoader.readPublicKeys("public_keys.txt");
            BigInteger p = publicKeys.get("p");

            Map<String, BigInteger> secretKeys = KeyLoader.readSecretKeys("secret_keys.txt");
            BigInteger sk = secretKeys.get("skh"); // assuming secret key is stored as skh=...

            // 3. Compute fid once per file
            String fileName = "Dna_seq50kb.fasta";
            System.out.println("Starting Enhanced Tag generation...");
            
            long signatureStartTime = System.nanoTime();
            BigInteger fid = SchnorrUtils.computeFileIdentifier(fileName, p, sk);
           // Save fid to fid.txt
SchnorrUtils.saveFidToFile(fid, "fid.txt");
            // 4. Generate signatures using Enhanced Schnorr Tag (W, γ)                  
            try (BufferedWriter signatureWriter = new BufferedWriter(new FileWriter(signatureOutputFilePath))) {
              // BigInteger fid = SchnorrUtils.computeFileIdentifier(fileName, p, sk);
                signatureWriter.write("Block,W,gamma"); // CSV header
                signatureWriter.newLine();
                for (int i = 0; i < blocks.length; i++) {
                    BigInteger blockData = new BigInteger(1, blocks[i]);
                    //BigInteger fid = SchnorrUtils.computeFileIdentifier(fileName, p, sk);
                    EnhancedSchnorrSignature.Tag tag = EnhancedSchnorrSignature.generateTag(blockData, fid);
                    signatureWriter.write((i + 1) + "," + tag.W + "," + tag.gamma);
                    signatureWriter.newLine();
                }
            }

            long signatureEndTime = System.nanoTime();
            long signatureGenerationTime = signatureEndTime - signatureStartTime;
            double averageTimePerBlock = (double) signatureGenerationTime / blocks.length;

            // 5. Write timing info to file
            System.out.println("Writing enhanced time measurements...");
            try (BufferedWriter timeWriter = new BufferedWriter(new FileWriter(timeMeasurementFilePath))) {
              //  timeWriter.write("Key Generation Time: " + keyGenerationTime + " ns\n");
              // timeWriter.write("Key Generation Time: " + keyGenerationTime / 1_000_000 + " ms\n");
                timeWriter.write("Signature Generation Time: " + signatureGenerationTime + " ns\n");
                timeWriter.write("Signature Generation Time: " + signatureGenerationTime / 1_000_000 + " ms\n");
                timeWriter.write("Average Signature Generation Time per Block: " + averageTimePerBlock + " ns\n");
                timeWriter.write("Average Signature Generation Time per Block: " + averageTimePerBlock / 1_000_000 + " ms\n");
              //  timeWriter.write("Key Size (delta): " + delta.bitLength() + " bits\n");
                timeWriter.write("Block Size: " + blockSize + " bytes\n");
               // timeWriter.write("Tag Size (W, gamma): " + (q.bitLength() * 2) + " bits\n");
                timeWriter.write("Security parameter (Lambda): " + lambda + " bits\n");
               // timeWriter.write("Bit length of q: " + q.bitLength() + "\n");
               // timeWriter.write("Bit length of p: " + p.bitLength() + "\n");
                timeWriter.flush();
            }

            System.out.println("Enhanced execution complete. Times and signatures saved.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
*/