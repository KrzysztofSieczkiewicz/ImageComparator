package org.example.utils.accessor;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.awt.image.Raster;

public class ImageDataUtil {

    /**
     * Retrieves the pixel data from a BufferedImage and stores it in a 2D int array, where each
     * int represents a pixel with the RGB value packed into a single integer.
     *
     * @param image The BufferedImage to extract data from.
     * @return A 2D array of ints representing the pixel data (RGB encoded).
     */
    public static int[][] getImageData(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] pixelData = new int[height][width];

        int channelsNo = 3;
        if (image.getColorModel().hasAlpha()) channelsNo = 4;

        Raster raster = image.getRaster();
        int[] pixelBuffer = new int[width * height * channelsNo];
        raster.getPixels(0, 0, width, height, pixelBuffer);

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (channelsNo == 4) index++;
                int red = pixelBuffer[index++];
                int green = pixelBuffer[index++];
                int blue = pixelBuffer[index++];

                // 0xRRGGBB
                pixelData[y][x] = (red << 16) | (green << 8) | blue;
            }
        }

        return pixelData;
    }

    /**
     * Resizes an image data matrix (int[][]) to the requested dimensions using averaging for downscaling.
     *
     * @param image 2D int array representing the input image pixels
     * @param width requested width to scale to
     * @param height requested height to scale to
     * @return 2D int array containing the resized image
     */
    public static int[][] resizeWithAveraging(int[][] image, int width, int height) {
        int originalWidth = image.length;
        int originalHeight = image[0].length;
        int[][] resizedImage = new int[width][height];

        double scaleX = (double) originalWidth / width;
        double scaleY = (double) originalHeight / height;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int startX = (int) (x * scaleX);
                int startY = (int) (y * scaleY);
                int endX = (int) Math.min((x + 1) * scaleX, originalWidth);
                int endY = (int) Math.min((y + 1) * scaleY, originalHeight);

                int pixelSumR = 0, pixelSumG = 0, pixelSumB = 0;
                int count = 0;

                for (int i = startX; i < endX; i++) {
                    for (int j = startY; j < endY; j++) {
                        int pixel = image[i][j];
                        pixelSumR += (pixel >> 16) & 0xFF;
                        pixelSumG += (pixel >> 8) & 0xFF;
                        pixelSumB += pixel & 0xFF;
                        count++;
                    }
                }

                int avgR = pixelSumR / count;
                int avgG = pixelSumG / count;
                int avgB = pixelSumB / count;
                resizedImage[x][y] = (avgR << 16) | (avgG << 8) | avgB;
            }
        }

        return resizedImage;
    }

    /**
     * Retrieves the red channel data from a pixel integer.
     *
     * @param imagePixel integer containing RGB channels values 0xRRGGBB
     * @return int representing the red channel
     */
    public static int getRedChannel(int imagePixel) {
        return (imagePixel >> 16) & 0xFF;
    }

    /**
     * Retrieves the green channel data from a pixel integer.
     *
     * @param imagePixel integer containing RGB channels values 0xRRGGBB
     * @return int representing the green channel
     */
    public static int getGreenChannel(int imagePixel) {
        return (imagePixel >> 8) & 0xFF;
    }

    /**
     * Retrieves the blue channel data from a pixel integer.
     *
     * @param imagePixel integer containing RGB channels values 0xRRGGBB
     * @return int representing the blue channel
     */
    public static int getBlueChannel(int imagePixel) {
        return imagePixel & 0xFF;
    }


    /**
     * Blurs image using convolve op.
     * Used kernel size is 6 times sigma rounded up to the next odd integer.
     *
     * @param imageData int[][] containing image pixels data
     * @param sigma std deviation of th Gaussian distribution used for blurring
     * @return new, blurred BuffedImage
     */
    public static int[][] gaussianBlur(int[][] imageData, double sigma) {
        int width = imageData.length;
        int height = imageData[0].length;
        int[][] blurredImageData = new int[width][height];

        float[] kernelData = generateGaussianKernelData(sigma);

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double r = 0, g = 0, b = 0, weightSum = 0;

                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int pixel = imageData[y + ky][x + kx];
                        float kernelValue = kernelData[ky + 1 + 3 * (kx + 1)];

                        int pixelR = (pixel >> 16) & 0xFF;
                        int pixelG = (pixel >> 8) & 0xFF;
                        int pixelB = pixel & 0xFF;

                        r += pixelR * kernelValue;
                        g += pixelG * kernelValue;
                        b += pixelB * kernelValue;
                        weightSum += kernelValue;
                    }
                }

                r = Math.min(255, Math.max(0, r / weightSum));
                g = Math.min(255, Math.max(0, g / weightSum));
                b = Math.min(255, Math.max(0, b / weightSum));

                int blurredPixel = (int) (r) << 16 | (int) (g) << 8 | (int) (b);
                blurredImageData[y][x] = blurredPixel;
            }
        }
        return blurredImageData;
    }

    /**
     * Internal util method. Generates Gaussian blur kernel. Size is set to be ~6 times sigma and odd.
     *
     * @param sigma std deviation of the Gaussian distribution used for the blur
     * @return awt Kernel
     */
    private static float[] generateGaussianKernelData(double sigma) {
        int size = (int) (6 * sigma);
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

        return kernelData;
    }
}
