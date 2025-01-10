package org.example.utils;

import org.example.utils.accessor.ImageAccessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageUtil {

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

        Graphics g = greyscaleImg.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

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
        BufferedImage blurredImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int kernelSize = (int) (6 * sigma);
        if (kernelSize % 2 == 0) kernelSize++;

        float[] kernelData = generateGaussianKernel(sigma, kernelSize);
        Kernel kernel = new Kernel(kernelSize, kernelSize, kernelData);

        ConvolveOp conv = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        conv.filter(image, image);

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
     * Generates Gaussian blur kernel.
     *
     * @param sigma std deviation of the Gaussian distribution used for the blur
     * @param size kernel size. Should be an odd integer
     * @return float array containing kernel values
     */
    private static float[] generateGaussianKernel(double sigma, int size) {
        float[] kernel = new float[size * size];
        int halfSize = size / 2;
        float sum = 0;

        for (int x=-halfSize; x<=halfSize; x++) {
            for (int y =-halfSize; y<=halfSize; y++) {
                float value = (float) ((1 / (2 * Math.PI * sigma*sigma)) * Math.exp(-(x*x + y*y) / (2 * sigma*sigma)));
                kernel[(x+halfSize)*size + (y+halfSize)] = value;
                sum += value;
            }
        }

        for (int i=0; i<kernel.length; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }
}
