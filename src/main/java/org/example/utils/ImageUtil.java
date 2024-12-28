package org.example.utils;

import org.example.utils.accessor.ImageAccessor;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.*;

public class ImageUtil {

    /**
     * Resizes image to requested dimensions. If image is being downsized it might require gaussian blurring to fix over-sharpening
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
     * Converts image to greyscale color space using ColorConvertOp
     * @param image BufferedImage to be converted
     * @return new BuffedImage containing image in greyscale color space
     */
    public static BufferedImage greyscale(BufferedImage image) {
        ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

        BufferedImage greyscaleImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        colorConvert.filter(image, greyscaleImg);
        return greyscaleImg;
    }

    /**
     * Blurs image using convolve op with preset kernel.
     * @param image BufferedImage to be affected
     * @return new, blurred BuffedImage
     */
    public static BufferedImage gaussianBlur(BufferedImage image) {
        float[] kernelData = {
                0.0625f, 0.125f, 0.0625f,
                0.125f, 0.25f, 0.125f,
                0.0625f, 0.125f, 0.0625f
        };
        Kernel kernel = new Kernel(3, 3, kernelData);

        BufferedImage blurredImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

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
    // TODO: implement "resized output image" config and allow for reduced output image size
    public static BufferedImage deepCopy(BufferedImage original) {
        BufferedImage copy = new BufferedImage(
//                original.getWidth()/2,
//                original.getHeight()/2,
                original.getWidth(),
                original.getHeight(),
                original.getType());

        Graphics2D g2d = copy.createGraphics();
        //g2d.scale(0.5, 0.5);
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();

        return copy;
    }

}
