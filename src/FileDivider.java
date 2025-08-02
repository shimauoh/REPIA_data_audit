import java.io.*;
import java.math.BigInteger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileDivider {

    // Method to divide the file into blocks of a specified size
    public byte[][] divideFile(String filePath, int blockSize, String numericDataBlocksFilePath, String letterDataBlocksFilePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Path.of(filePath));
        int totalBlocks = (int) Math.ceil((double) fileBytes.length / blockSize);
        byte[][] blocks = new byte[totalBlocks][];

        try (BufferedWriter numericDataWriter = new BufferedWriter(new FileWriter(numericDataBlocksFilePath));
             BufferedWriter letterDataWriter = new BufferedWriter(new FileWriter(letterDataBlocksFilePath))) {
            for (int i = 0; i < totalBlocks; i++) {
                int start = i * blockSize;
                int length = Math.min(blockSize, fileBytes.length - start);
                blocks[i] = new byte[length];
                System.arraycopy(fileBytes, start, blocks[i], 0, length);

                // Convert the block to a numeric value (BigInteger)
                BigInteger blockData = new BigInteger(1, blocks[i]);

                // Write the numeric value to the numeric data blocks file
                numericDataWriter.write("Block " + (i + 1) + ": " + blockData.toString());
                numericDataWriter.newLine();
                numericDataWriter.flush(); // Ensure data is written to the file

                // Convert the block to a letter representation (assuming ASCII)
                String letterRepresentation = new String(blocks[i]);
                letterDataWriter.write("Block " + (i + 1) + ": " + letterRepresentation);
                letterDataWriter.newLine();
            }
        }

        return blocks; // Return the original blocks
    }
    public String[] divideFileToStrings(String inputFile, int blockSize, String blockFile) throws IOException {
        // Read the file into a byte array
        byte[] data = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(inputFile));
        int numBlocks = (int) Math.ceil((double) data.length / blockSize);
        String[] blocks = new String[numBlocks];
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(blockFile))) {
            for (int i = 0; i < numBlocks; i++) {
                int start = i * blockSize;
                int end = Math.min(start + blockSize, data.length);
                String block = new String(data, start, end - start);
                blocks[i] = block;
                writer.write(block);
                writer.newLine();
            }
        }
        return blocks;
    }
}
