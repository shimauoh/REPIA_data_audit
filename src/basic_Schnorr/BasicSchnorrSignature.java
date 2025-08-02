import java.math.BigInteger;
import java.io.IOException;
import java.util.Map;

public class BasicSchnorrSignature {

    public static class Tag {
        public final BigInteger W, gamma;

        public Tag(BigInteger W, BigInteger gamma) {
            this.W = W;
            this.gamma = gamma;
        }
    }

    /**
     * Public method: keeps single arg for blockData.
     * Internally loads keys once.
     */
    public static Tag generateTag(BigInteger blockData) throws IOException {
        // Load keys
        Map<String, BigInteger> publicKeys = KeyLoader.readPublicKeys("public_keys.txt");
        Map<String, BigInteger> secretKeys = KeyLoader.readSecretKeys("secret_keys.txt");

        BigInteger q = publicKeys.get("q");
        BigInteger chi = publicKeys.get("chi");
        BigInteger delta = secretKeys.get("delta");
        BigInteger xi = secretKeys.get("xi");

        // Step 1: Compute W = H(χ ∥ blockData) mod q
        BigInteger W = SchnorrUtils.computeW(chi, blockData, q);

        // Step 2: Compute γ = (W * δ + ξ) mod q
        BigInteger gamma = W.multiply(delta).add(xi).mod(q);

        return new Tag(W, gamma);
    }
    public static Tag generateTag(BigInteger blockData, BigInteger q, BigInteger chi, BigInteger delta, BigInteger xi) {
        BigInteger W = SchnorrUtils.computeW(chi, blockData, q);
        BigInteger gamma = W.multiply(delta).add(xi).mod(q);
        return new Tag(W, gamma);
    }
    
}
