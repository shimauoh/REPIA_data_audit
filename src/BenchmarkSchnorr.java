import java.io.*;
import java.math.BigInteger;
import java.util.Map;
import java.util.Scanner;

public class BenchmarkSchnorr {
    public static void main(String[] args) {
        System.out.println("=== Execute the KeyGenerationTest first ===");
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Schnorr & Algebraic Benchmark Tool ===");

            System.out.print("Enter block size in bytes (e.g., 512): ");
            int blockSize = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter block index to verify (e.g., 2): ");
            int blockIndex = Integer.parseInt(scanner.nextLine().trim());
            runBenchmark(blockSize, blockIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runBenchmark(int blockSize, int blockIndex) throws Exception {
        // === Step 1: Load keys and lambda ===
        long keyTime;
        int lambda;
        try (BufferedReader reader = new BufferedReader(new FileReader("keygen_time.txt"))) {
            keyTime = Long.parseLong(reader.readLine().trim());
            lambda = Integer.parseInt(reader.readLine().trim());
        }

        Map<String, BigInteger> publicKeys = KeyLoader.readPublicKeys("public_keys.txt");
        Map<String, BigInteger> secretKeys = KeyLoader.readSecretKeys("secret_keys.txt");
        BigInteger p = publicKeys.get("p");
        BigInteger q = publicKeys.get("q");
        int qBits = q.bitLength();
        int schnorrTagBits = qBits * 2;
        int schnorrTagBytes = (int) Math.ceil(schnorrTagBits / 8.0);

        BigInteger chi = publicKeys.get("chi");
        BigInteger delta = secretKeys.get("delta");
        BigInteger xi = secretKeys.get("xi");
        BigInteger skh = secretKeys.get("skh");

        // Algebraic keys
        Map<String, BigInteger> algPub = KeyLoader.readPublicKeys("algebraic_public_keys.txt");
        Map<String, BigInteger> algSec = KeyLoader.readSecretKeys("algebraic_secret_keys.txt");
        BigInteger pAlg = algPub.get("p");
        BigInteger g = algSec.get("g");
        int algTagBits = pAlg.bitLength();
        int algTagBytes = (int) Math.ceil(algTagBits / 8.0);

        // === Step 2: Divide file into blocks ===
        FileDivider divider = new FileDivider();
        byte[][] blocks = divider.divideFile("Dna_seq50kb.fasta", blockSize, "numeric_data_blocks.txt", "letter_data_blocks.txt");
        int totalBlocks = blocks.length;

        // === Step 3: Run benchmarks ===
        BenchmarkResult enhRes = benchmarkEnhanced(blocks, blockIndex, q, chi, delta, xi, p, skh);
        BenchmarkResult basicRes = benchmarkBasic(blocks, blockIndex, q, chi, delta, xi);
       
        BenchmarkResult algRes = benchmarkAlgebraic(blocks, blockIndex, pAlg, g);
        long algebraicKeyTime;
        try (BufferedReader reader = new BufferedReader(new FileReader("algebraic_keygen_time.txt"))) {
            algebraicKeyTime = Long.parseLong(reader.readLine().trim());
            // optionally read lambda from second line
        }
        long algKeyTime;
try (BufferedReader reader = new BufferedReader(new FileReader("algebraic_keygen_time.txt"))) {
    algKeyTime = Long.parseLong(reader.readLine().trim());
} catch (Exception e) {
    throw new RuntimeException("Failed to read algebraic_keygen_time.txt! Did you run AlgebraicSignatureKeyGen?");
}

        
        // === Step 4: Print table ===
        String table = 
        "\n+---------------------------------------------------------------------------+\n" +
        "|                    Schnorr & Algebraic Benchmark Result                    |\n" +
        "+---------------------------+--------------+--------------+--------------+\n" +
        "|                           |     Basic    |   Enhanced   |   Algebraic  |\n" +
        "+---------------------------+--------------+--------------+--------------+\n" +
        String.format("| Security parameter λ     | %12d | %12d | %12d |\n", lambda, lambda, lambda) +
        String.format("| Block size (bytes)       | %12d | %12d | %12d |\n", blockSize, blockSize, blockSize) +
        String.format("| Total blocks             | %12d | %12d | %12d |\n", totalBlocks, totalBlocks, totalBlocks) +
        "+---------------------------+--------------+--------------+--------------+\n" +
        String.format("| Key generation time (ms) | %12.2f | %12.2f | %12.2f |\n", 
                      keyTime / 1e6, keyTime / 1e6, algKeyTime / 1e6) +
        String.format("| Signature gen time (ms)  | %12.2f | %12.2f | %12.2f |\n", 
                      basicRes.totalSigMs, enhRes.totalSigMs, algRes.totalSigMs) +
        String.format("| Avg time per block (ms)  | %12.4f | %12.4f | %12.4f |\n", 
                      basicRes.avgSigMs, enhRes.avgSigMs, algRes.avgSigMs) +
        String.format("| CSP proof gen time (ms)  | %12.4f | %12.4f | %12.4f |\n", 
                      basicRes.cspMs, enhRes.cspMs, algRes.cspMs) +
        String.format("| TPA verification time(ms)| %12.4f | %12.4f | %12.4f |\n", 
                      basicRes.tpaMs, enhRes.tpaMs, algRes.tpaMs) +
       "+---------------------------+--------------+--------------+--------------+\n" +
String.format("| Schnorr tag size (bits)  | %12d | %12d |      -       |\n", schnorrTagBits, schnorrTagBits) +
String.format("| Schnorr tag size (bytes) | %12d | %12d |      -       |\n", schnorrTagBytes, schnorrTagBytes) +
String.format("| Algebraic tag size (bits)|       -      |       -      | %12d |\n", algTagBits) +
String.format("| Algebraic tag size(bytes)|       -      |       -      | %12d |\n", algTagBytes) +
"+---------------------------------------------------------------------------+\n";
try (BufferedWriter writer = new BufferedWriter(new FileWriter("benchmark_comparison.csv"))) {
    // Write CSV header
    writer.write("Metric,Basic,Enhanced,Algebraic\n");

    // Write data rows
    writer.write(String.format("Security parameter λ,%d,%d,%d\n", lambda, lambda, lambda));
    writer.write(String.format("Block size (bytes),%d,%d,%d\n", blockSize, blockSize, blockSize));
    writer.write(String.format("Total blocks,%d,%d,%d\n", totalBlocks, totalBlocks, totalBlocks));
    writer.write(String.format("Key generation time (ms),%.2f,%.2f,%.2f\n", 
                keyTime/1e6, keyTime/1e6, algKeyTime/1e6));
    writer.write(String.format("Signature gen time (ms),%.2f,%.2f,%.2f\n", 
                basicRes.totalSigMs, enhRes.totalSigMs, algRes.totalSigMs));
    writer.write(String.format("Avg time per block (ms),%.4f,%.4f,%.4f\n", 
                basicRes.avgSigMs, enhRes.avgSigMs, algRes.avgSigMs));
    writer.write(String.format("CSP proof gen time (ms),%.4f,%.4f,%.4f\n", 
                basicRes.cspMs, enhRes.cspMs, algRes.cspMs));
    writer.write(String.format("TPA verification time (ms),%.4f,%.4f,%.4f\n", 
                basicRes.tpaMs, enhRes.tpaMs, algRes.tpaMs));

    // Signature sizes
    writer.write(String.format("Schnorr tag size (bits),%d,%d,\n", schnorrTagBits, schnorrTagBits));
    writer.write(String.format("Schnorr tag size (bytes),%d,%d,\n", schnorrTagBytes, schnorrTagBytes));
    writer.write(String.format("Algebraic tag size (bits),,,%d\n", algTagBits));
    writer.write(String.format("Algebraic tag size (bytes),,,%d\n", algTagBytes));
}
System.out.println("✅ Benchmark CSV report saved to benchmark_comparison.csv");

        
        

        System.out.println(table);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("benchmark_comparison.txt"))) {
            writer.write(table);
        }
        System.out.println("✅ Benchmark complete! Report saved to benchmark_comparison.txt");
    }

    // === Benchmark methods ===

    private static BenchmarkResult benchmarkBasic(byte[][] blocks, int blockIndex, BigInteger q, BigInteger chi, BigInteger delta, BigInteger xi) throws Exception {
        long sigStart = System.nanoTime();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("basic_block_signature.csv"))) {
            writer.write("Block,W,gamma\n");
            for (int i = 0; i < blocks.length; i++) {
                BigInteger data = new BigInteger(1, blocks[i]);
                BasicSchnorrSignature.Tag tag = BasicSchnorrSignature.generateTag(data, q, chi, delta, xi);
                writer.write((i+1) + "," + tag.W + "," + tag.gamma + "\n");
            }
        }
        long sigEnd = System.nanoTime();

        long cspStart = System.nanoTime();
        BigInteger blockData = new BigInteger(1, blocks[blockIndex - 1]);
        BigInteger W_prime = SchnorrUtils.computeW(chi, blockData, q);
        long cspEnd = System.nanoTime();

        Map<String, String[]> tags = EnhancedVerificationUtils.readTagsFromCSV("basic_block_signature.csv");
        BigInteger gamma_stored = new BigInteger(tags.get(String.valueOf(blockIndex))[1]);

        long tpaStart = System.nanoTime();
        BigInteger gamma_prime = W_prime.multiply(delta).add(xi).mod(q);
        boolean verified = gamma_stored.equals(gamma_prime);
        long tpaEnd = System.nanoTime();

        System.out.println("[Basic] Verification " + (verified ? "✔ passed" : "✘ failed"));
        return new BenchmarkResult(sigEnd - sigStart, blocks.length, cspEnd - cspStart, tpaEnd - tpaStart);
    }

    private static BenchmarkResult benchmarkEnhanced(byte[][] blocks, int blockIndex, BigInteger q, BigInteger chi, BigInteger delta, BigInteger xi, BigInteger p, BigInteger skh) throws Exception {
        BigInteger fid = SchnorrUtils.computeFileIdentifier("Dna_seq50kb.fasta", p, skh);
        SchnorrUtils.saveFidToFile(fid, "fid.txt");

        long sigStart = System.nanoTime();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("enhanced_block_signature.csv"))) {
            writer.write("Block,W,gamma\n");
            for (int i = 0; i < blocks.length; i++) {
                BigInteger data = new BigInteger(1, blocks[i]);
                EnhancedSchnorrSignature.Tag tag = EnhancedSchnorrSignature.generateTag(data, fid, q, chi, delta, xi);
                writer.write((i+1) + "," + tag.W + "," + tag.gamma + "\n");
            }
        }
        long sigEnd = System.nanoTime();

        long cspStart = System.nanoTime();
        BigInteger blockData = new BigInteger(1, blocks[blockIndex - 1]);
        BigInteger W_prime = SchnorrUtils.computeWWithFid(fid, chi, blockData, q);
        long cspEnd = System.nanoTime();

        Map<String, String[]> tags = EnhancedVerificationUtils.readTagsFromCSV("enhanced_block_signature.csv");
        BigInteger gamma_stored = new BigInteger(tags.get(String.valueOf(blockIndex))[1]);

        long tpaStart = System.nanoTime();
        BigInteger gamma_prime = W_prime.multiply(delta).add(xi).mod(q);
        boolean verified = gamma_stored.equals(gamma_prime);
        long tpaEnd = System.nanoTime();

        System.out.println("[Enhanced] Verification " + (verified ? "✔ passed" : "✘ failed"));
        return new BenchmarkResult(sigEnd - sigStart, blocks.length, cspEnd - cspStart, tpaEnd - tpaStart);
    }

    private static BenchmarkResult benchmarkAlgebraic(byte[][] blocks, int blockIndex, BigInteger p, BigInteger g) throws Exception {
        long sigStart = System.nanoTime();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("algebraic_signatures.csv"))) {
            writer.write("Block,Signature\n");
            for (int i = 0; i < blocks.length; i++) {
                String strBlock = new String(blocks[i]);
                BigInteger tag = AlgebraicSignature.generateTag(strBlock, g, p);
                writer.write((i+1) + "," + tag.toString() + "\n");
            }
        }
        long sigEnd = System.nanoTime();

        long cspStart = System.nanoTime();
        String str = new String(blocks[blockIndex - 1]);
        BigInteger proof = AlgebraicSignature.generateTag(str, g, p);
        long cspEnd = System.nanoTime();

        long tpaStart = System.nanoTime();
        BigInteger verify = AlgebraicSignature.generateTag(str, g, p);
        boolean ok = proof.equals(verify);
        long tpaEnd = System.nanoTime();

        System.out.println("[Algebraic] Verification " + (ok ? "✔ passed" : "✘ failed"));
        return new BenchmarkResult(sigEnd - sigStart, blocks.length, cspEnd - cspStart, tpaEnd - tpaStart);
    }
    

    // Helper class
    static class BenchmarkResult {
        double totalSigMs, avgSigMs, cspMs, tpaMs;
        BenchmarkResult(long totalSigNs, int blocks, long cspNs, long tpaNs) {
            totalSigMs = totalSigNs / 1e6;
            avgSigMs = (double) totalSigNs / blocks / 1e6;
            cspMs = cspNs / 1e6;
            tpaMs = tpaNs / 1e6;
        }
    }
}
