package analyzers.ssim;

import org.example.analyzers.ssim.SSIMAnalyzer;
import org.example.comparators.SSIMComparatorConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SSIMAnalyzerTest {
    private SSIMAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        SSIMComparatorConfig config = new SSIMComparatorConfig();
        analyzer = new SSIMAnalyzer(config);
    }

    /**
     * Helper method to create a simple BufferedImage for testing.
     */
    private BufferedImage createTestImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }

    @Test
    public void testCalculateImagesSSIM_identicalImages() {
        BufferedImage image1 = createTestImage(64, 64, Color.BLACK);
        BufferedImage image2 = createTestImage(64, 64, Color.BLACK);

        double ssim = analyzer.calculateImagesSSIM(image1, image2);

        Assertions.assertEquals(1.0, ssim, 0.001);
    }

    @Test
    public void testCalculateImagesSSIM_differentImages() {
        BufferedImage image1 = createTestImage(64, 64, Color.BLACK);
        BufferedImage image2 = createTestImage(64, 64, Color.WHITE);

        double ssim = analyzer.calculateImagesSSIM(image1, image2);

        Assertions.assertEquals(0.0, ssim, 0.001);
    }

    @Test
    public void testCalculateImagesSSIM_similarImages() {
        BufferedImage image1 = createTestImage(64, 64, Color.BLACK);
        BufferedImage image2 = createTestImage(64, 64, Color.BLACK);

        Color grey = new Color(128, 128, 128);
        int changeSize = 8;
        for (int x = 0; x < changeSize; x++) {
            for (int y = 0; y < changeSize; y++) {
                image2.setRGB(x, y, grey.getRGB());
            }
        }

        double ssim = analyzer.calculateImagesSSIM(image1, image2);

        Assertions.assertTrue(ssim > 0.0 && ssim < 1.0);
    }

    @Test
    public void testCalculateImagesSSIM_simplifiedCalculationMethod() {
        SSIMComparatorConfig simplifiedConfig = new SSIMComparatorConfig();
        SSIMAnalyzer simplifiedAnalyzer = new SSIMAnalyzer(simplifiedConfig);

        BufferedImage image1 = createTestImage(64, 64, Color.BLACK);
        BufferedImage image2 = createTestImage(64, 64, Color.BLACK);

        double ssim = simplifiedAnalyzer.calculateImagesSSIM(image1, image2);

        Assertions.assertEquals(1.0, ssim, 0.001);
    }

    @Test
    public void testCalculateImagesSSIM_differentCalculationMethod() {
        SSIMComparatorConfig weightedConfig = new SSIMComparatorConfig()
                .alpha(0.8)
                .beta(0.9)
                .gamma(1.1);
        SSIMAnalyzer weightedAnalyzer = new SSIMAnalyzer(weightedConfig);

        BufferedImage image1 = createTestImage(64, 64, Color.BLACK);
        BufferedImage image2 = createTestImage(64, 64, Color.BLACK);

        double ssim = weightedAnalyzer.calculateImagesSSIM(image1, image2);

        Assertions.assertEquals(1.0, ssim, 0.001);
    }
}
