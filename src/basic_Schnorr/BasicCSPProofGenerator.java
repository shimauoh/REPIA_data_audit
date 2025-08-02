import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
//import java.security.MessageDigest;
//import java.security.*;
import java.util.List;

public class BasicCSPProofGenerator {
    public static BigInteger simulateCSP(String blockIndex, BigInteger chi, BigInteger p, BigInteger q, String blockFile) throws IOException {
        BigInteger blockData = new BigInteger(readBlockByIndexFromLabeledFile(blockFile, Integer.parseInt(blockIndex)));
        return SchnorrUtils.computeW(chi, blockData, q);
    }
 public static BigInteger simulateCSP(int blockIndex, BigInteger chi, BigInteger p, BigInteger q, String blockFile) throws IOException {
    BigInteger blockData = new BigInteger(readBlockByIndexFromLabeledFile("numeric_data_blocks.txt", blockIndex));
    return SchnorrUtils.computeW(chi, blockData, q);  // Unify with the utility method
}
  
   /* public static BigInteger simulateCSP(String blockIndex, BigInteger chi, BigInteger p, String blockFile) throws IOException {
        String dataBlock = readBlockFromFile(blockFile, blockIndex);
        String concatenated = dataBlock + chi.toString();
        BigInteger hash = sha256ToBigInteger(concatenated);
        return hash;
    }
*/
    public static void writeProofToFile(String filename, String blockIndex, BigInteger chi, BigInteger q, BigInteger W_dash) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("blockIndex=" + blockIndex + "\n");
            writer.write("chi=" + chi.toString() + "\n");
            writer.write("q=" + q.toString() + "\n");
            writer.write("W_dash=" + W_dash.toString() + "\n");
        }
    }

 
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


   /* private static BigInteger sha256ToBigInteger(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            return new BigInteger(1, hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Hash error", e);
        }
    }*/
}
