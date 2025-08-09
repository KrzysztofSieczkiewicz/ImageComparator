package org.example.utils;

import org.example.utils.accessor.ImageAccessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageUtil {

    /**
     * Resizes an image to the requested dimensions using Graphics2D with high-quality rendering hints.
     *
     * @param image BufferedImage to be resized
     * @param width requested width to scale to
     * @param height requested height to scale to
     * @return new BufferedImage containing the resized image
     */
    public static BufferedImage resizeBilinear(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = resizedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();

        return resizedImage;
    }

    /**
     * Calculates greyscale space from provided RGB image
     *
     * @param image rgb BufferedImage
     * @return 1D array of greyscale int values
     */
    public static int[] extractGreyscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] gImage = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);

                gImage[y * width + x] =
                    ((pixel >> 16) & 0xFF) +
                    ((pixel >> 8) & 0xFF) +
                    (pixel & 0xFF);
            }
        }
        return gImage;
    }

    /**
     * Calculates greyscale space from provided RGB image
     *
     * @param image rgb BufferedImage
     * @return 2D array of greyscale int values
     */
    public static int[][] extractGreyscaleArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][] gImage = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);

                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                gImage[y][x] = (red + green + blue) / 3;
            }
        }
        return gImage;
    }

    /**
     * Calculates Y channel (YCbCr) from provided RGB image
     *
     * @param image rgb BufferedImage
     * @return 1D array of Y channel int values
     */
    public static int[] extractLuminosity(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] yImage = new int[width*height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);

                double yValue =
                        ((pixel >> 16) & 0xFF) * 0.299 +
                        ((pixel >> 8) & 0xFF) * 0.587 +
                        (pixel & 0xFF) * 0.114;

                int yInt = (int) Math.round(yValue);
                yInt = Math.max(0, Math.min(255, yInt));

                yImage[y * width + x] = yInt;
            }
        }
        return yImage;
    }

    /**
     * Convolves image with provided kernel
     * @param imageData 1D int array containing image data
     * @param imageWidth processed image width (when in 2D format)
     * @param imageHeight processed image height (when in 2D format)
     * @param kernel AWT Kernel
     * @return new instance of convolved imageData
     */
    public static double[] convolve(int[] imageData, int imageWidth, int imageHeight, Kernel kernel) {
        double[] outputMap = new double[imageData.length];
        int kernelHalf = kernel.getWidth() / 2;

        float[] kernelData = kernel.getKernelData(null);

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                double sumWeightedValue = 0.0;

                for (int ky = 0; ky < kernel.getHeight(); ky++) {
                    for (int kx = 0; kx < kernel.getWidth(); kx++) {
                        int imgX = x + kx - kernelHalf;
                        int imgY = y + ky - kernelHalf;

                        if (imgX < 0) imgX = 0;
                        if (imgY < 0) imgY = 0;
                        if (imgX >= imageWidth) imgX = imageWidth - 1;
                        if (imgY >= imageHeight) imgY = imageHeight - 1;

                        double weight = kernelData[ky * kernel.getWidth() + kx];
                        int pixelIndex = imgY * imageWidth + imgX;
                        sumWeightedValue += (double) imageData[pixelIndex] * weight;
                    }
                }
                outputMap[y * imageWidth + x] = sumWeightedValue;
            }
        }

        return outputMap;
    }

    /**
     * Perform deep copy of BufferedImage
     *
     * @param original BufferedImage to be copied
     * @return image deep copy
     */
    public static BufferedImage deepCopy(BufferedImage original) {
        BufferedImage copy = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                original.getType());

        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();

        return copy;
    }

    /**
     * Generates Gaussian blur kernel
     *
     * @param sigma std deviation of the Gaussian distribution used for the blur
     * @return normalized awt Kernel
     */
    public static Kernel generateGaussianKernel(int dimension, double sigma) {
        float[] kernelData = new float[dimension * dimension];
        double sum = 0;
        int halfSize = dimension / 2;

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                double x = j - halfSize;
                double y = i - halfSize;

                float value = (float) Math.exp(-(x * x + y * y) / (2 * sigma * sigma));

                kernelData[i * dimension + j] = value;
                sum += value;
            }
        }

        for (int i = 0; i < kernelData.length; i++) {
            kernelData[i] = (float) (kernelData[i] / sum);
        }

        return new Kernel(dimension, dimension, kernelData);
    }
}