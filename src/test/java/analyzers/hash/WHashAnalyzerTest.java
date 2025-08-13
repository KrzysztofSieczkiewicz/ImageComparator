package analyzers.hash;

import org.example.analyzers.hash.WHashAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.BitSet;

public class WHashAnalyzerTest {

    private WHashAnalyzer wHashAnalyzer;

    /**
     * Helper method to create a simple BufferedImage for testing.
     */
    private BufferedImage createTestImage(int width, int height, Color pixelValue) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                image.setRGB(i, j, pixelValue.getRGB());
            }
        }
        return image;
    }

    @Test
    public void testWHash_identicalImages() {
        double coefficient = 0.25;
        wHashAnalyzer = new WHashAnalyzer(coefficient);
        BufferedImage image1 = createTestImage(64, 64, Color.BLACK);
        BufferedImage image2 = createTestImage(64, 64, Color.BLACK);

        BitSet hash1 = wHashAnalyzer.wHash(image1);
        BitSet hash2 = wHashAnalyzer.wHash(image2);

        Assertions.assertEquals(hash1, hash2);
    }

    @Test
    public void testWHash_differentImages() {
        double coefficient = 0.25;
        wHashAnalyzer = new WHashAnalyzer(coefficient);
        BufferedImage image1 = createTestImage(64, 64, Color.BLACK);
        BufferedImage image2 = createTestImage(64, 64, Color.WHITE);

        BitSet hash1 = wHashAnalyzer.wHash(image1);
        BitSet hash2 = wHashAnalyzer.wHash(image2);

        Assertions.assertNotEquals(hash1, hash2);
    }

    @Test
    public void testWHash_similarImages() {
        double coefficient = 0.25;
        wHashAnalyzer = new WHashAnalyzer(coefficient);
        BufferedImage image1 = createTestImage(64, 64, Color.BLACK);
        BufferedImage image2 = createTestImage(64, 64, Color.BLACK);

        int changeSize = 16;
        for (int i = 0; i < changeSize; i++) {
            for (int j = 0; j < changeSize; j++) {
                image2.setRGB(i, j, Color.WHITE.getRGB());
            }
        }

        BitSet hash1 = wHashAnalyzer.wHash(image1);
        BitSet hash2 = wHashAnalyzer.wHash(image2);

        Assertions.assertNotEquals(hash1, hash2, "Hashes of similar images should be different.");
    }

    @Test
    public void testWHash_rectangularImages() {
        double coefficient = 0.25;
        wHashAnalyzer = new WHashAnalyzer(coefficient);
        BufferedImage image = createTestImage(128, 64, Color.BLACK);

        BitSet hash = wHashAnalyzer.wHash(image);

        Assertions.assertEquals(32 * 16, hash.size());
    }

    @Test
    public void testWHash_differentCoefficients() {
        WHashAnalyzer wHashAnalyzer1 = new WHashAnalyzer(0.25);
        WHashAnalyzer wHashAnalyzer2 = new WHashAnalyzer(0.5);
        BufferedImage image = createTestImage(64, 64, Color.BLACK);

        BitSet hash1 = wHashAnalyzer1.wHash(image);
        BitSet hash2 = wHashAnalyzer2.wHash(image);

        Assertions.assertNotEquals(hash1.size(), hash2.size());
        Assertions.assertEquals(16*16, hash1.size()); // (64*0.25)*(64*0.25)
        Assertions.assertEquals(32*32, hash2.size()); // (64*0.5)*(64*0.5)
    }
}
