import java.io.BufferedWriter;
import java.io.FileWriter;

public class KeyGenerationTest {
    public static void main(String[] args) {
        try {
            int lambda = 256;
            if (args.length > 0) {
                lambda = Integer.parseInt(args[0]);
            }

            System.out.println("Generating keys with lambda = " + lambda + "...");
            long globalStart = System.nanoTime();

            KeyGenerator generator = new KeyGenerator();
            generator.generateKeys(lambda);

            long globalEnd = System.nanoTime();
            long totalKeygenTime = globalEnd - globalStart;
            double totalTimeMs = totalKeygenTime / 1_000_000.0;

            long secretTime = generator.getSecretKeyTimeNs();
            long publicTime = generator.getPublicKeyTimeNs();
            long FidSkhTime = generator.getFidKeyTimeNs();

            System.out.printf("Total key generation took: %d ns (%.2f ms)%n", totalKeygenTime, totalTimeMs);
            System.out.printf("Secret key generation time: %d ns (%.2f ms)%n", secretTime, secretTime / 1_000_000.0);
            System.out.printf("Public key generation time: %d ns (%.2f ms)%n", publicTime, publicTime / 1_000_000.0);
            System.out.printf("File Identifier Secret key generation time: %d ns (%.2f ms)%n", FidSkhTime, FidSkhTime / 1_000_000.0);

            // Save timing data to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("keygen_time.txt", false))) {
                writer.write("Lambda: " + lambda + "\n");
                writer.write("TotalKeyGenTime(ns): " + totalKeygenTime + "\n");
                writer.write("SecretKeyGenTime(ns): " + secretTime + "\n");
                writer.write("PublicKeyGenTime(ns): " + publicTime + "\n");
            }

            System.out.println("Timing saved to keygen_time.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
