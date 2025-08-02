import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class KeyLoader {

    // Load public keys from a file (e.g., public_keys.txt)
    public static Map<String, BigInteger> readPublicKeys(String filename) throws IOException {
        return readKeyFile(filename);
    }

    // Load secret keys from a file (e.g., secret_keys.txt)
    public static Map<String, BigInteger> readSecretKeys(String filename) throws IOException {
        return readKeyFile(filename);
    }

    // Shared private method to read key-value pairs from a file
    private static Map<String, BigInteger> readKeyFile(String filename) throws IOException {
        Map<String, BigInteger> keys = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines

                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    BigInteger value = new BigInteger(parts[1].trim());
                    keys.put(key, value);
                }
            }
        }

        return keys;
    }
}
