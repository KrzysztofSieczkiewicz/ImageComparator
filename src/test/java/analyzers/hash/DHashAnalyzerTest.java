package analyzers.hash;

import org.example.analyzers.hash.DHashAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class DHashAnalyzerTest {
    private DHashAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new DHashAnalyzer();
    }

    @Test
    void dHash_correctHash() {
        int width = 3;
        int height = 3;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] luminosityValues = {150, 100, 120, 100, 100, 150, 120, 150, 100};

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int greyValue = luminosityValues[index++];
                int rgb = (greyValue << 16) | (greyValue << 8) | greyValue;
                image.setRGB(x, y, rgb);
            }
        }

        BitSet actualHash = analyzer.dHash(image);
        BitSet expectedHash = new BitSet(height * (width - 1));
        index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                int current = luminosityValues[y * width + x];
                int next = luminosityValues[y * width + x + 1];
                if (current > next) {
                    expectedHash.set(index);
                }
                index++;
            }
        }

        Assertions.assertEquals(expectedHash, actualHash);
        Assertions.assertEquals(height * (width - 1), actualHash.length());
    }

    @Test
    void dHash_gradientValues() {
        int width = 4;
        int height = 2;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] luminosityValues = {
                100, 110, 120, 130,
                150, 160, 170, 180
        };

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int greyValue = luminosityValues[index++];
                int rgb = (greyValue << 16) | (greyValue << 8) | greyValue;
                image.setRGB(x, y, rgb);
            }
        }

        BitSet actualHash = analyzer.dHash(image);

        BitSet expectedHash = new BitSet(height * (width - 1));
        Assertions.assertEquals(expectedHash, actualHash);
    }

    @Test
    void dHash_shouldHandleHorizontallyDecreasingValues() {
        int width = 4;
        int height = 2;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] luminosityValues = {
                130, 120, 110, 100,
                180, 170, 160, 150
        };

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int greyValue = luminosityValues[index++];
                int rgb = (greyValue << 16) | (greyValue << 8) | greyValue;
                image.setRGB(x, y, rgb);
            }
        }

        BitSet actualHash = analyzer.dHash(image);
        BitSet expectedHash = new BitSet(height * (width - 1));
        expectedHash.set(0, height * (width - 1));

        Assertions.assertEquals(expectedHash, actualHash);
    }
}
