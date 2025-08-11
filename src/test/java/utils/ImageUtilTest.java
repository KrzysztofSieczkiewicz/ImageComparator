package utils;

import org.example.utils.ImageUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public class ImageUtilTest {
    private static final float DELTA = 0.0001f;

    @Test
    public void testResizeBilinear_correctDimensions() {
        BufferedImage originalImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        int newWidth = 50;
        int newHeight = 50;

        BufferedImage resizedImage = ImageUtil.resizeBilinear(originalImage, newWidth, newHeight);

        Assertions.assertNotNull(resizedImage);
        Assertions.assertEquals(newWidth, resizedImage.getWidth());
        Assertions.assertEquals(newHeight, resizedImage.getHeight());
    }

    @Test
    public void testResizeBilinear_maintainImageType() {
        BufferedImage originalImage = new BufferedImage(100, 100, BufferedImage.TYPE_4BYTE_ABGR);
        int newWidth = 50;
        int newHeight = 50;

        BufferedImage resizedImage = ImageUtil.resizeBilinear(originalImage, newWidth, newHeight);

        Assertions.assertEquals(originalImage.getType(), resizedImage.getType());
    }

    @Test
    public void testResizeBilinear_handleUpsizing() {
        BufferedImage originalImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        int newWidth = 40;
        int newHeight = 40;

        BufferedImage resizedImage = ImageUtil.resizeBilinear(originalImage, newWidth, newHeight);

        Assertions.assertEquals(newWidth, resizedImage.getWidth());
        Assertions.assertEquals(newHeight, resizedImage.getHeight());
    }

    @Test
    public void testResizeBilinear_handleDownsizing() {
        BufferedImage originalImage = new BufferedImage(80, 80, BufferedImage.TYPE_INT_RGB);
        int newWidth = 40;
        int newHeight = 40;

        BufferedImage resizedImage = ImageUtil.resizeBilinear(originalImage, newWidth, newHeight);

        Assertions.assertEquals(newWidth, resizedImage.getWidth());
        Assertions.assertEquals(newHeight, resizedImage.getHeight());
    }

    @Test
    public void testExtractLuminosityArray_returnCorrectArraySize() {
        int width = 20;
        int height = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[] greyscaleArray = ImageUtil.extractLuminosityArray(image);

        Assertions.assertNotNull(greyscaleArray);
        Assertions.assertEquals(width * height, greyscaleArray.length);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0, 0",             // black pixel
            "255, 255, 255, 255",     // white pixel
            "100, 150, 200, 141",     // mixed color
            "255, 0, 0, 76",         // red
            "0, 255, 0, 150",         // green
            "0, 0, 255, 29"          // blue
    })
    public void testExtractLuminosityArray_correctPixelValue(int red, int green, int blue, int expectedSum) {
        int width = 1;
        int height = 1;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int pixelColor = (red << 16) | (green << 8) | blue;
        image.setRGB(0, 0, pixelColor);

        int[] greyscaleArray = ImageUtil.extractLuminosityArray(image);

        Assertions.assertNotNull(greyscaleArray);
        Assertions.assertEquals(expectedSum, greyscaleArray[0]);
    }

    @Test
    public void testExtractLuminosityArray_blackAndWhiteImage() {
        int width = 2;
        int height = 1;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int blackPixel = 0x000000;
        int whitePixel = 0xFFFFFF;
        image.setRGB(0, 0, blackPixel);
        image.setRGB(1, 0, whitePixel);

        int[] greyscaleArray = ImageUtil.extractLuminosityArray(image);

        Assertions.assertEquals(0, greyscaleArray[0]);
        Assertions.assertEquals(255, greyscaleArray[1]);
    }

    @Test
    public void testExtractLuminosityMatrix_correctDimensions() {
        int width = 50;
        int height = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int[][] greyscaleArray = ImageUtil.extractLuminosityMatrix(image);

        Assertions.assertNotNull(greyscaleArray);
        Assertions.assertEquals(height, greyscaleArray.length);
        Assertions.assertEquals(width, greyscaleArray[0].length);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0, 0, 0",             // black
            "255, 255, 255, 255",     // white
            "100, 150, 200, 141",     // mixed color
            "255, 0, 0, 76",          // red
            "0, 255, 0, 150",          // green
            "0, 0, 255, 29"           // blue
    })
    public void testExtractLuminosityMatrix_correctPixelValue(int red, int green, int blue, int expectedAverage) {
        int width = 1;
        int height = 1;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int pixelColor = (red << 16) | (green << 8) | blue;
        image.setRGB(0, 0, pixelColor);

        int[][] greyscaleArray = ImageUtil.extractLuminosityMatrix(image);

        Assertions.assertNotNull(greyscaleArray);
        Assertions.assertEquals(expectedAverage, greyscaleArray[0][0]);
    }

    @Test
    public void testConvolve_identityKernel() {
        int imageWidth = 3;
        int imageHeight = 3;
        int[] imageData = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        float[] identityKernelData = {0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        Kernel identityKernel = new Kernel(3, 3, identityKernelData);

        double[] expectedOutput = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0};
        double[] actualOutput = ImageUtil.convolve(imageData, imageWidth, imageHeight, identityKernel);

        Assertions.assertArrayEquals(expectedOutput, actualOutput, DELTA);
    }

    @Test
    public void testConvolve_zeroKernel() {
        int imageWidth = 3;
        int imageHeight = 3;
        int[] imageData = {100, 150, 200, 250, 300, 350, 400, 450, 500};

        float[] kernelData = {0.0f, 0.0f, 0.0f, 0.0f};
        Kernel zeroKernel = new Kernel(2, 2, kernelData);

        double[] expectedOutput = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] actualOutput = ImageUtil.convolve(imageData, imageWidth, imageHeight, zeroKernel);

        Assertions.assertArrayEquals(expectedOutput, actualOutput, DELTA);
    }

    @Test
    public void testConvolve_boxBlurKernel() {
        int imageWidth = 3;
        int imageHeight = 3;
        int[] imageData = {10, 20, 30, 40, 50, 60, 70, 80, 90};

        float weight = 1.0f / 10f;
        float[] kernelData = {weight, weight, weight, weight, weight, weight, weight, weight, weight};
        Kernel blurKernel = new Kernel(3, 3, kernelData);

        double[] expectedOutput = {21.0, 27.0, 33.0, 39.0, 45.0, 51.0, 57.0, 63.0, 69.0};
        double[] actualOutput = ImageUtil.convolve(imageData, imageWidth, imageHeight, blurKernel);

        Assertions.assertArrayEquals(expectedOutput, actualOutput, DELTA);
    }

    @Test
    public void testConvolve_sobelKernel() {
        int imageWidth = 3;
        int imageHeight = 3;
        int[] imageData = {255, 100, 100, 255, 100, 100, 255, 100, 100};

        float[] kernelData = {-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f};
        Kernel sobelKernel = new Kernel(3, 3, kernelData);

        double[] expectedOutput = {-620.0, -620.0, 0.0, -620.0, -620.0, 0.0, -620.0, -620.0, 0.0};
        double[] actualOutput = ImageUtil.convolve(imageData, imageWidth, imageHeight, sobelKernel);

        Assertions.assertArrayEquals(expectedOutput, actualOutput, DELTA);
    }

    @Test
    public void testGenerateGaussianKernel_dimensions() {
        int dimension = 5;
        double sigma = 1.0;
        Kernel kernel = ImageUtil.generateGaussianKernel(dimension, sigma);

        Assertions.assertEquals(dimension, kernel.getWidth());
        Assertions.assertEquals(dimension, kernel.getHeight());
    }

    @Test
    public void testGenerateGaussianKernel_normalization() {
        int dimension = 3;
        double sigma = 1.0;
        Kernel kernel = ImageUtil.generateGaussianKernel(dimension, sigma);

        float[] kernelData = kernel.getKernelData(null);
        double sum = 0;
        for (float value : kernelData) {
            sum += value;
        }

        Assertions.assertEquals(1.0, sum, DELTA);
    }

    @Test
    public void testGenerateGaussianKernel_symmetry() {
        int dimension = 3;
        double sigma = 1.0;
        Kernel kernel = ImageUtil.generateGaussianKernel(dimension, sigma);
        float[] kernelData = kernel.getKernelData(null);

        Assertions.assertEquals(kernelData[0], kernelData[2], DELTA, "Horizontal symmetry failed");
        Assertions.assertEquals(kernelData[0], kernelData[6], DELTA, "Vertical symmetry failed");
        Assertions.assertEquals(kernelData[0], kernelData[8], DELTA, "Diagonal symmetry failed");

        Assertions.assertEquals(kernelData[1], kernelData[3], DELTA, "Mid-row symmetry failed");
        Assertions.assertEquals(kernelData[1], kernelData[5], DELTA, "Mid-column symmetry failed");
        Assertions.assertEquals(kernelData[1], kernelData[7], DELTA, "Mid-point symmetry failed");

        float centerValue = kernelData[4];
        Assertions.assertTrue(centerValue > kernelData[0], "Center value should be the maximum");
        Assertions.assertTrue(centerValue > kernelData[1], "Center value should be the maximum");
    }

    @Test
    public void testGenerateGaussianKernel_dataValues() {
        int dimension = 3;
        double sigma = 1.0;

        // Non-normalized values for a 3x3 kernel with sigma = 1.0
        // Center: (x:0, y:0) = 1.0
        // Sides:  (x:1, y:0) = 0.60653
        // Corners: (x:1, y:1) = 0.36788
        // Total (sum) = 4.89724

        double sum = 4.89724;
        float centerNormalized = (float) (1.0 / sum);
        float sideNormalized = (float) (0.60653 / sum);
        float cornerNormalized = (float) (0.36788 / sum);

        float[] expectedKernelData = {
                cornerNormalized, sideNormalized, cornerNormalized,
                sideNormalized, centerNormalized, sideNormalized,
                cornerNormalized, sideNormalized, cornerNormalized
        };
        float[] actualKernelData = ImageUtil.generateGaussianKernel(dimension, sigma).getKernelData(null);

        Assertions.assertArrayEquals(expectedKernelData, actualKernelData, DELTA);
    }
}
