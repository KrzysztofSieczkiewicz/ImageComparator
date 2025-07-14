package org.example.utils;

import org.example.utils.accessor.ImageAccessor;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageUtil {

    public static int[] getWindowData(int[] imageData, int imageWidth, int windowDimension, int startX, int startY) {
        int[] windowData = new int[windowDimension * windowDimension];

        for (int currRow=0; currRow<windowDimension; currRow++) {
            for (int currCol=0; currCol<windowDimension; currCol++) {
                int pixelX = startX + currRow;
                int pixelY = startY + currCol;

                int indexImage = pixelY * imageWidth + pixelX;
                int indexWindow = currRow * windowDimension + currCol;

                windowData[indexWindow] = imageData[indexImage];
            }
        }
        return  windowData;
    }

    /**
     * Resizes image to requested dimensions. If image is being downsized it might require gaussian blurring to fix over-sharpening
     *
     * @param image BufferedImage to be resized
     * @param width requested width to scale to
     * @param height requested height to scale to
     * @return new BuffedImage containing resized image
     */
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage rescaledImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ImageAccessor imgAccessor = ImageAccessor.create(image);

        int x, y;
        int ww = image.getWidth();
        int hh = image.getHeight();

        for (x = 0; x < width; x++) {
            for (y = 0; y < height; y++) {
                int col = imgAccessor.getPixel(x * ww / width, y * hh / height);
                rescaledImg.setRGB(x, y, col);
            }
        }

        return rescaledImg;
    }

    /**
     * Converts image to greyscale color space using TYPE_BYTE_GRAY
     *
     * @param image BufferedImage to be converted
     * @return new BuffedImage containing image in greyscale color space
     */
    public static BufferedImage greyscale(BufferedImage image) {
        BufferedImage greyscaleImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        ImageAccessor imageAccessor = ImageAccessor.create(image);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int grey = (int) (imageAccessor.getRed(x,y) * 0.21 +
                        imageAccessor.getGreen(x,y) * 0.72 +
                        imageAccessor.getBlue(x,y) * 0.07);

                int rgb = (grey << 16) | (grey << 8) | grey;
                greyscaleImg.setRGB(x, y, rgb);
            }
        }

        return greyscaleImg;
    }

    public long[] convolve(int[] imageData, int imageWidth, int imageHeight, Kernel kernel) {
        long[] outputMap = new long[imageData.length];
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
                outputMap[y * imageWidth + x] = Math.round(sumWeightedValue);
            }
        }

        return outputMap;
    }

    /**
     * Blurs image using convolve op with preset kernel.
     * Used kernel size is 6 times sigma rounded up to the next odd integer.
     *
     * @param image BufferedImage to be affected
     * @param sigma std deviation of th Gaussian distribution used for blurring
     * @return new, blurred BuffedImage
     */
    public static BufferedImage gaussianBlur(BufferedImage image, double sigma) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage blurredImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Kernel kernel = generateGaussianKernel(sigma);

        ConvolveOp conv = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        conv.filter(image, blurredImg);

        return blurredImg;
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
     * Internal util method. Generates Gaussian blur kernel. Size is set to be ~6 times sigma and odd.
     *
     * @param sigma std deviation of the Gaussian distribution used for the blur
     * @return normalized awt Kernel
     */
    public static Kernel generateGaussianKernel(double sigma) {
        int size = (int) (5 * sigma);
        if (size % 2 == 0) size++;

        float[] kernelData = new float[size * size];
        int halfSize = size / 2;
        float sum = 0;

        for (int x=-halfSize; x<=halfSize; x++) {
            for (int y =-halfSize; y<=halfSize; y++) {
                float value = (float) ((1 / (2 * Math.PI * sigma*sigma)) * Math.exp(-(x*x + y*y) / (2 * sigma*sigma)));
                kernelData[(x+halfSize)*size + (y+halfSize)] = value;
                sum += value;
            }
        }

        for (int i=0; i<kernelData.length; i++) {
            kernelData[i] /= sum;
        }

        return new Kernel(size, size, kernelData);
    }

    /**
     * Generates Gaussian blur kernel
     *
     * @param sigma std deviation of the Gaussian distribution used for the blur
     * @return normalized awt Kernel
     */
    public static double[] generateGaussianKernel(int dimension, double sigma) {
        double[] kernel = new double[dimension * dimension];
        double sum = 0;
        int halfSize = dimension / 2;

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                double x = j - halfSize;
                double y = i - halfSize;

                double value = Math.exp(-(x * x + y * y) / (2 * sigma * sigma));

                kernel[i * dimension + j] = value;
                sum += value;
            }
        }

        for (int i = 0; i < kernel.length; i++) {
            kernel[i] = kernel[i] / sum;
        }
        return kernel;
    }
}