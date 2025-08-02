import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class VerificationUtils {

    public static Map<String, BigInteger> readPublicKeys(String filePath) throws IOException {
        Map<String, BigInteger> keys = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.trim().split("=");
            if (parts.length == 2) {
                keys.put(parts[0].trim(), new BigInteger(parts[1].trim()));
            }
        }
        reader.close();
        return keys;
    }

    public static Map<String, String[]> readTagsFromCSV(String filePath) throws IOException {
        Map<String, String[]> tags = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Block")) continue; // Skip header if present
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                tags.put(parts[0].trim(), new String[]{parts[1].trim(), parts[2].trim()});
            }
        }
        reader.close();
        return tags;
    }

   /* public static String readBlockFromFile(String filePath, String blockIndex) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(blockIndex + ",")) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    reader.close();
                    return parts[1].trim();
                }
            }
        }
        reader.close();
        throw new IllegalArgumentException("Block index " + blockIndex + " not found in " + filePath);
    }*/
    public static String readBlockFromFile(String filePath, String blockIndex) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        String prefix = "Block " + blockIndex + ":";
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(prefix)) {
                reader.close();
                return line.substring(prefix.length()).trim();
            }
        }
        reader.close();
        throw new IllegalArgumentException("Block index " + blockIndex + " not found in " + filePath);
    }
    
    public static BigInteger generateW_dashFromCSP(int blockIndex, BigInteger chi, BigInteger q, String dataFilePath) throws IOException {
        String blockStr = readBlockFromFile(dataFilePath, String.valueOf(blockIndex));
        BigInteger blockData = new BigInteger(blockStr);
        return SchnorrUtils.computeW(chi, blockData, q); // ✅ Same method used by Data Owner
    }
    
    public static void writeProofToFile(String filePath, int blockIndex, BigInteger x, BigInteger chi, BigInteger W_dash) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write("BlockIndex=" + blockIndex + "\n");
        writer.write("x=" + x + "\n");
        writer.write("chi=" + chi + "\n");
        writer.write("W_dash=" + W_dash + "\n");
        writer.close();
    }

    public static Map<String, BigInteger> readProofFromFile(String filePath) throws IOException {
        Map<String, BigInteger> proof = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                proof.put(parts[0].trim(), new BigInteger(parts[1].trim()));
            }
        }
        reader.close();
        return proof;
    }
    public static BigInteger computeGammaPrime(BigInteger W_dash, BigInteger delta, BigInteger chi, BigInteger p) {
        return W_dash.multiply(delta).add(chi).mod(p);
    }
    
//    public static BigInteger computeGammaPrime(BigInteger W_dash, BigInteger alpha, BigInteger e, BigInteger p) {
  //      BigInteger alphaPowE = alpha.modPow(e, p);
    //    return W_dash.multiply(alphaPowE).mod(p);
    //}
    
    public static BigInteger generateW_dashWithFid(BigInteger fid, BigInteger chi, BigInteger blockData, BigInteger q) {
        return SchnorrUtils.computeWWithFid(fid, chi, blockData, q);
    }

    
}
