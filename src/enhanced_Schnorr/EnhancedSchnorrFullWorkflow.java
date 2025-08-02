public class EnhancedSchnorrFullWorkflow {
    public static void main(String[] args) {
        try {
            System.out.println("🔑 Step 1: Generating keys...");
            KeyGenerator keyGen = new KeyGenerator();
            keyGen.generateKeys(256); // you can change lambda if needed
            System.out.println("✅ Keys generated.");

            System.out.println("\n📝 Step 2: Generating signatures...");
            EnhancedSchnorrSignatureTest.run();
            System.out.println("✅ Signatures generated.");

            System.out.println("\n🔍 Step 3: Verifying signatures...");
            EnhancedSchnorrVerificationTest.run();
            System.out.println("✅ Verification complete.");

            System.out.println("\n🎉 Workflow complete! All stages ran successfully.");
        } catch (Exception ex) {
            System.err.println("❌ Error during workflow: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
