package utils;

import org.example.utils.ImageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
    void testExtractGreyscale_returnCorrectArraySize() {
        int width = 20;
        int height = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] greyscaleArray = ImageUtil.extractGreyscale(image);

        Assertions.assertNotNull(greyscaleArray);
        Assertions.assertEquals(width * height, greyscaleArray.length);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0, 0",             // black pixel
            "255, 255, 255, 765",     // white pixel
            "100, 150, 200, 450",     // mixed color
            "255, 0, 0, 255",         // red
            "0, 255, 0, 255",         // green
            "0, 0, 255, 255"          // blue
    })
    void testExtractGreyscale_correctPixelValue(int red, int green, int blue, int expectedSum) {
        int width = 1;
        int height = 1;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int pixelColor = (red << 16) | (green << 8) | blue;
        image.setRGB(0, 0, pixelColor);

        int[] greyscaleArray = ImageUtil.extractGreyscale(image);

        Assertions.assertNotNull(greyscaleArray);
        Assertions.assertEquals(expectedSum, greyscaleArray[0]);
    }

    @Test
    void testExtractGreyscale_blackAndWhiteImage() {
        int width = 2;
        int height = 1;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int blackPixel = 0x000000;
        int whitePixel = 0xFFFFFF;
        image.setRGB(0, 0, blackPixel);
        image.setRGB(1, 0, whitePixel);

        int[] greyscaleArray = ImageUtil.extractGreyscale(image);

        Assertions.assertEquals(0, greyscaleArray[0]);
        Assertions.assertEquals(255 + 255 + 255, greyscaleArray[1]);
    }

    @Test
    void testExtractGreyscaleArray_correctDimensions() {
        int width = 50;
        int height = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[][] greyscaleArray = ImageUtil.extractGreyscaleArray(image);

        Assertions.assertNotNull(greyscaleArray);
        Assertions.assertEquals(height, greyscaleArray.length);
        Assertions.assertEquals(width, greyscaleArray[0].length);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0, 0",             // black
            "255, 255, 255, 255",     // white
            "100, 150, 200, 150",     // mixed color
            "255, 0, 0, 85",          // red
            "0, 255, 0, 85",          // green
            "0, 0, 255, 85"           // blue
    })
    void testExtractGreyscaleArray_correctPixelValue(int red, int green, int blue, int expectedAverage) {
        int width = 1;
        int height = 1;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int pixelColor = (red << 16) | (green << 8) | blue;
        image.setRGB(0, 0, pixelColor);

        int[][] greyscaleArray = ImageUtil.extractGreyscaleArray(image);

        Assertions.assertNotNull(greyscaleArray);
        Assertions.assertEquals(expectedAverage, greyscaleArray[0][0]);
    }
}
