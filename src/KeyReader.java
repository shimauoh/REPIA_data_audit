import java.io.*;
//import java.math.BigInteger;
import java.util.List;
import java.nio.file.*;

public class KeyReader {

    // Read both delta and ξ from secret_keys.txt
    public static String[] readSecretKeys() throws IOException {
        String delta = null;
        String xi = null;

        // Check if file exists
        Path filePath = Paths.get("secret_keys.txt");
        if (!Files.exists(filePath)) {
            throw new IOException("secret_keys.txt not found. Please run KeyGenerator first to generate the keys.");
        }

        // Read lines from the secret_keys.txt file
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
            if (line.trim().startsWith("delta=")) {
                delta = line.split("=")[1].trim();  // Extract the value of delta
            } else if (line.trim().startsWith("xi=")) {
                xi = line.split("=")[1].trim();  // Extract the value of ξ (xi)
            }
        }

        if (delta == null || xi == null) {
            throw new IOException("Both delta and xi (ξ) keys not found in secret_keys.txt. Please ensure the file contains both keys.");
        }

        return new String[]{delta, xi};
    }

    public static void main(String[] args) {
        try {
            // Read delta and ξ (xi) from the secret key file
            String[] keys = readSecretKeys();
            String delta = keys[0];  // private key (delta)
            String xi = keys[1];     // secret key (ξ)

            // Now you can use these keys in the verification process
            System.out.println("Delta: " + delta);
            System.out.println("Xi (ξ): " + xi);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please run KeyGenerator first to generate the keys.");
        }
    }
}
