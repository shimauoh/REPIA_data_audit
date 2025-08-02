import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
import java.io.IOException;
//import java.io.File;
import java.util.Map;

public class EnhancedSchnorrSignature {

    // Tag structure (same as in BasicSchnorrSignature)
    public static class Tag {
        public final BigInteger W, gamma;

        public Tag(BigInteger W, BigInteger gamma) {
            this.W = W;
            this.gamma = gamma;
        }
    }

    /**
     * Generates tag after reading keys from file.
     * Difference: uses fid when computing W.
     */
    public static Tag generateTag(BigInteger blockData, BigInteger fid) throws NoSuchAlgorithmException, IOException {
        // Load public keys
        Map<String, BigInteger> publicKeys = KeyLoader.readPublicKeys("public_keys.txt");
        BigInteger q = publicKeys.get("q");
        BigInteger chi = publicKeys.get("chi");

        // Load secret keys
        Map<String, BigInteger> secretKeys = KeyLoader.readSecretKeys("secret_keys.txt");
        BigInteger delta = secretKeys.get("delta");
        BigInteger xi = secretKeys.get("xi");
        BigInteger skh = secretKeys.get("skh");

        // Step 3: Compute W = H(fid ∥ χ ∥ blockData) mod q
        BigInteger W = SchnorrUtils.computeWWithFid( fid, chi, blockData, q);

        // Step 4: Compute γ = (W * δ + ξ) mod q
        BigInteger gamma = (W.multiply(delta).add(xi)).mod(q);

        // Store xi back into secret_keys.txt
       // storeXi(xi, secretKeys);

        return new Tag(W, gamma);
    }

    public static String formatTag(Tag tag) {
        return "(" + tag.W + ", " + tag.gamma + ")";
    }
    public static Tag generateTag(BigInteger blockData, BigInteger fid, BigInteger q, BigInteger chi, BigInteger delta, BigInteger xi) {
        BigInteger W = SchnorrUtils.computeWWithFid(fid, chi, blockData, q);
        BigInteger gamma = W.multiply(delta).add(xi).mod(q);
        return new Tag(W, gamma);
    }
    
}
