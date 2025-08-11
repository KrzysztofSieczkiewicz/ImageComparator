package analyzers.hash;

import org.example.analyzers.hash.AHashAnalyzer;
import org.example.utils.ImageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class AHashAnalyzerTest {
    private AHashAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new AHashAnalyzer();
    }

    @Test
    void aHash_generateHash() {
        BufferedImage image = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        int[] pixels = {100, 150, 100, 150, 120, 150, 100, 150, 100};
        int index = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int greyValue = pixels[index++];
                int rgb = (greyValue << 16) | (greyValue << 8) | greyValue;
                image.setRGB(x, y, rgb);
            }
        }

        int[] greyscaleValues = ImageUtil.extractLuminosityArray(image);
        int sum = 0;
        for (int value : greyscaleValues) {
            sum += value;
        }
        int average = sum / greyscaleValues.length;
        Assertions.assertEquals(124, average);

        BitSet expectedHash = new BitSet(9);
        expectedHash.set(1);
        expectedHash.set(3);
        expectedHash.set(5);
        expectedHash.set(7);
        BitSet actualHash = analyzer.aHash(image);

        Assertions.assertEquals(expectedHash, actualHash);
    }

    @Test
    void aHash_uniformImage() {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        int greyValue = 128;
        int rgb = (greyValue << 16) | (greyValue << 8) | greyValue;
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                image.setRGB(x, y, rgb);
            }
        }

        BitSet expectedHash = new BitSet(4);
        expectedHash.set(0, 4);
        BitSet actualHash = analyzer.aHash(image);

        Assertions.assertEquals(expectedHash, actualHash);
    }

    @Test
    void aHash_singlePixelImage() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        int greyValue = 200;
        int rgb = (greyValue << 16) | (greyValue << 8) | greyValue;
        image.setRGB(0, 0, rgb);

        BitSet expectedHash = new BitSet(1);
        expectedHash.set(0);
        BitSet actualHash = analyzer.aHash(image);

        Assertions.assertEquals(expectedHash, actualHash);
    }
}
