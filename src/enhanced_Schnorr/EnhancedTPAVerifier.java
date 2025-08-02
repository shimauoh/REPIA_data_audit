import java.math.BigInteger;
import java.util.Map;

public class EnhancedTPAVerifier {

    /**
     * Verifies a single block by recomputing W' using fid, chi, and blockData,
     * then computing gamma′ and comparing with stored gamma.
     */
    public static boolean verifyBlock(
            String blockIndex,
            BigInteger fid,
            BigInteger chi,
            BigInteger delta,
            BigInteger q,
            Map<String, String[]> storedTags,
            BigInteger blockData
    ) {
        try {
            // Get stored W and gamma for this block
            String[] tag = storedTags.get(blockIndex);
            if (tag == null) {
                System.err.println("Block index not found in stored tags: " + blockIndex);
                return false;
            }

            BigInteger storedW = new BigInteger(tag[0]);
            BigInteger storedGamma = new BigInteger(tag[1]);

            // Recompute W'
            BigInteger W_prime = VerificationUtils.generateW_dashWithFid(fid, chi, blockData, q);

            if (!W_prime.equals(storedW)) {
                System.err.println("W mismatch at block " + blockIndex);
                return false;
            }

            // Recompute gamma′
            BigInteger gamma_prime = VerificationUtils.computeGammaPrime(W_prime, delta, chi, q);

            if (!gamma_prime.equals(storedGamma)) {
                System.err.println("Gamma mismatch at block " + blockIndex);
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Verification error for block " + blockIndex + ": " + e.getMessage());
            return false;
        }
    }
}
