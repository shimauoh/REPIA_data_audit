import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
//import java.security.MessageDigest;
import java.util.List;

public class EnhancedCSPProofGenerator {

    /**
     * Simulates CSP proof generation (block index as String):
     * W′ = H(fid ∥ chi ∥ blockData) mod q
     */
    public static BigInteger simulateCSP(
            String blockIndex,
            BigInteger fid,
            BigInteger chi,
            BigInteger p,
            BigInteger q,
            String blockFile
    ) throws IOException {
        BigInteger blockData = new BigInteger(
                readBlockByIndexFromLabeledFile(blockFile, Integer.parseInt(blockIndex))
        );
        return SchnorrUtils.computeWWithFid(fid, chi, blockData, q);
    }

    /**
     * Overloaded version: blockIndex as int
     */
    public static BigInteger simulateCSP(
            int blockIndex,
            BigInteger fid,
            BigInteger chi,
            BigInteger p,
            BigInteger q,
            String blockFile
    ) throws IOException {
        BigInteger blockData = new BigInteger(
                readBlockByIndexFromLabeledFile(blockFile, blockIndex)
        );
        return SchnorrUtils.computeWWithFid(fid, chi, blockData, q);
    }

    /**
     * Writes CSP's proof to file.
     * Follows same format as original: blockIndex, fid, chi, q, W_dash
     */
    public static void writeProofToFile(
            String filename,
            String blockIndex,
            BigInteger fid,
            BigInteger chi,
            BigInteger q,
            BigInteger W_dash
    ) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("blockIndex=" + blockIndex + "\n");
            writer.write("fid=" + fid.toString() + "\n");
            writer.write("chi=" + chi.toString() + "\n");
            writer.write("q=" + q.toString() + "\n");
            writer.write("W_dash=" + W_dash.toString() + "\n");
        }
    }

    /**
     * Reads block data from labeled file: lines like "Block 2:123456..."
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

        throw new IllegalArgumentException("Block index " + blockIndex + " not found in file.");
    }

    /*
     * Utility: SHA-256 of a String → BigInteger (if needed)
         private static BigInteger sha256ToBigInteger(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            return new BigInteger(1, hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Hash error", e);
        }
    }*/
}
