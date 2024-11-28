package org.example.utils;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageUtil {

    public static BufferedImage resize(BufferedImage image, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();

        return resizedImage;
    }

    public static BufferedImage greyscale(BufferedImage img) {
        ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

        BufferedImage greyscaleImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        colorConvert.filter(img, greyscaleImg);
        return greyscaleImg;
    }

    public static BufferedImage gaussianBlur(BufferedImage img) {
        float kernelData[] = {
                0.0625f, 0.125f, 0.0625f,
                0.125f, 0.25f, 0.125f,
                0.0625f, 0.125f, 0.0625f
        };
        Kernel kernel = new Kernel(3, 3, kernelData);

        BufferedImage blurredImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

        ConvolveOp conv = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        conv.filter(img, img);

        return blurredImg;
    }

}
