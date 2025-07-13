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

    /**
     * Generates an integral in which each pixel contains sum of all previous row and column values reduced by diagonal values
     * S(x,y)=∑i∑j I(i,j), where S(x,y)=I(x,y)+S(x−1,y)+S(x,y−1)−S(x−1,y−1)
     * @param imageData - 1D array of image pixels
     * @param imageWidth image X dimension
     * @param imageHeight image Y dimension
     * @return long array containing integral image data
     */
    public long[] getSumIntegralImage(int[] imageData, int imageWidth, int imageHeight) {
        long[] integralImage = new long[imageData.length];

        for (int y=0; y<imageHeight; y++) {
            for (int x=0; x<imageWidth; x++) {
                int currentIndex = x + (y * imageWidth);
                int currentValue = imageData[currentIndex];

                long valAbove = (y > 0) ? integralImage[(y - 1) * imageWidth + x] : 0;
                long valLeft = (x > 0) ? integralImage[y * imageWidth + (x - 1)] : 0;
                long valDiagonal = (y > 0 && x > 0) ? integralImage[(y - 1) * imageWidth + (x - 1)] : 0;

                integralImage[currentIndex] = currentValue + valAbove + valLeft - valDiagonal;
            }
        }
        return integralImage;
    }

    /**
     * Generates an integral in which each pixel contains squared sum of all previous row and column values reduced by diagonal values
     * S(x,y)=∑i∑j I(i,j), where S(x,y)=I(x,y)+S(x−1,y)+S(x,y−1)−S(x−1,y−1)
     * @param imageData - 1D array of image pixels
     * @param imageWidth image X dimension
     * @param imageHeight image Y dimension
     * @return long array containing integral image data
     */
    public long[] getSquaredSumIntegralImage(int[] imageData, int imageWidth, int imageHeight) {
        long[] integralImage = new long[imageData.length];

        for (int y=0; y<imageHeight; y++) {
            for (int x=0; x<imageWidth; x++) {
                int currentIndex = x + (y * imageWidth);
                long currentValue = (long) imageData[currentIndex] * imageData[currentIndex];

                long valAbove = (y > 0) ? integralImage[(y - 1) * imageWidth + x] : 0;
                long valLeft = (x > 0) ? integralImage[y * imageWidth + (x - 1)] : 0;
                long valDiagonal = (y > 0 && x > 0) ? integralImage[(y - 1) * imageWidth + (x - 1)] : 0;

                integralImage[currentIndex] = currentValue + valAbove + valLeft - valDiagonal;
            }
        }
        return integralImage;
    }

    /**
     * Generates an integral in which each pixel contains products of two images of all previous row and column values reduced by diagonal values
     * S(x,y)=∑i∑j I(i,j), where S(x,y)=I(x,y)+S(x−1,y)+S(x,y−1)−S(x−1,y−1)
     * @param firstImageData - 1D array of image pixels for the first image
     * @param secondImageData - 1D array of image pixels for the second image
     * @param imageWidth image X dimension
     * @param imageHeight image Y dimension
     * @return long array containing integral image data
     */
    public long[] getProductIntegralImage(int[] firstImageData, int[] secondImageData, int imageWidth, int imageHeight) {
        long[] integralImage = new long[firstImageData.length];

        for (int y=0; y<imageHeight; y++) {
            for (int x=0; x<imageWidth; x++) {
                int currentIndex = x + (y * imageWidth);
                long currentValue = (long) firstImageData[currentIndex] * secondImageData[currentIndex];

                long valAbove = (y > 0) ? integralImage[(y - 1) * imageWidth + x] : 0;
                long valLeft = (x > 0) ? integralImage[y * imageWidth + (x - 1)] : 0;
                long valDiagonal = (y > 0 && x > 0) ? integralImage[(y - 1) * imageWidth + (x - 1)] : 0;

                integralImage[currentIndex] = currentValue + valAbove + valLeft - valDiagonal;
            }
        }
        return integralImage;
    }
}