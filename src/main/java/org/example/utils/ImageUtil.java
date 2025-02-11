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
//    public static BufferedImage greyscale(BufferedImage image) {
//        BufferedImage greyscaleImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//        ImageAccessor imageAccessor = ImageAccessor.create(image);
//        ImageAccessor greyscaleAccessor = ImageAccessor.create(greyscaleImg);
//
//        for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                int grey = (int) (imageAccessor.getRed(x,y) * 0.21 +
//                        imageAccessor.getGreen(x,y) * 0.72 +
//                        imageAccessor.getBlue(x,y) * 0.07);
//
//                greyscaleAccessor.setPixel(x, y, 255, grey, grey, grey);
////                greyscaleAccessor.setOpaquePixel(x, y, grey, grey, grey);
//            }
//        }
//
//        return greyscaleImg;
//    }

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
     * @return awt Kernel
     */
    // TODO: parametrize kernel size multiplier
    private static Kernel generateGaussianKernel(double sigma) {
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
}
