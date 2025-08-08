package utils;

import org.example.utils.ImageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

public class ImageUtilTest {
    @Test
    void testResizeBilinear_correctDimensions() {
        BufferedImage originalImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        int newWidth = 50;
        int newHeight = 50;

        BufferedImage resizedImage = ImageUtil.resizeBilinear(originalImage, newWidth, newHeight);

        Assertions.assertNotNull(resizedImage);
        Assertions.assertEquals(newWidth, resizedImage.getWidth());
        Assertions.assertEquals(newHeight, resizedImage.getHeight());
    }

    @Test
    void testResizeBilinear_maintainImageType() {
        BufferedImage originalImage = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
        int newWidth = 50;
        int newHeight = 50;

        BufferedImage resizedImage = ImageUtil.resizeBilinear(originalImage, newWidth, newHeight);

        Assertions.assertEquals(originalImage.getType(), resizedImage.getType());
    }

    @Test
    void testResizeBilinear_handleUpsizing() {
        BufferedImage originalImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        int newWidth = 40;
        int newHeight = 40;

        BufferedImage resizedImage = ImageUtil.resizeBilinear(originalImage, newWidth, newHeight);

        Assertions.assertEquals(newWidth, resizedImage.getWidth());
        Assertions.assertEquals(newHeight, resizedImage.getHeight());
    }

    @Test
    void testResizeBilinear_handleDownsizing() {
        BufferedImage originalImage = new BufferedImage(80, 80, BufferedImage.TYPE_INT_RGB);
        int newWidth = 40;
        int newHeight = 40;

        BufferedImage resizedImage = ImageUtil.resizeBilinear(originalImage, newWidth, newHeight);

        Assertions.assertEquals(newWidth, resizedImage.getWidth());
        Assertions.assertEquals(newHeight, resizedImage.getHeight());
    }

    @Test
    void testResizeBilinear_throwExceptionOnNullInput() {
        Assertions.assertThrows(NullPointerException.class, () -> ImageUtil.resizeBilinear(null, 50, 50));
    }
}
