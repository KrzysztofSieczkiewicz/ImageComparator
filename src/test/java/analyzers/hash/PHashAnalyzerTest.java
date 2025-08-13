package analyzers.hash;

import org.example.analyzers.hash.PHashAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.BitSet;

public class PHashAnalyzerTest {

    private PHashAnalyzer pHashAnalyzer;

    @BeforeEach
    public void setUp() {
        pHashAnalyzer = new PHashAnalyzer();
    }

    /**
     * Helper method to create a simple BufferedImage for testing.
     */
    private BufferedImage createTestImage(int size, Color pixelValue) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                image.setRGB(i, j, pixelValue.getRGB());
            }
        }
        return image;
    }

    @Test
    public void testPHash_identicalImages() {
        BufferedImage image1 = createTestImage(128, Color.BLACK);
        BufferedImage image2 = createTestImage(128, Color.BLACK);

        BitSet hash1 = pHashAnalyzer.pHash(image1);
        BitSet hash2 = pHashAnalyzer.pHash(image2);

        Assertions.assertEquals(hash1, hash2);
    }

    @Test
    public void testPHash_differentImages() {
        BufferedImage image1 = createTestImage(128, Color.BLACK);
        BufferedImage image2 = createTestImage(128, Color.WHITE);

        BitSet hash1 = pHashAnalyzer.pHash(image1);
        BitSet hash2 = pHashAnalyzer.pHash(image2);

        Assertions.assertNotEquals(hash1, hash2);
    }

    @Test
    public void testPHash_similarImages() {
        BufferedImage image1 = createTestImage(128, Color.BLACK);
        BufferedImage image2 = createTestImage(128, Color.BLACK);

        int changeSize = 16;
        for (int i = 0; i < changeSize; i++) {
            for (int j = 0; j < changeSize; j++) {
                image2.setRGB(i, j, Color.WHITE.getRGB());
            }
        }

        BitSet hash1 = pHashAnalyzer.pHash(image1);
        BitSet hash2 = pHashAnalyzer.pHash(image2);

        Assertions.assertNotEquals(hash1, hash2, "Hashes of similar images should be different.");
    }
}