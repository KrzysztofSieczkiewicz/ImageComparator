package org.example.comparator;

import org.example.accessor.ImageAccessor;
import org.example.utils.PixelColorUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SimpleComparator implements ImageComparator {

    @Override
    public void compare(BufferedImage actual, BufferedImage checked) {
        float threshold = 20f;

        ImageAccessor expectedAccessor = ImageAccessor.create(actual);
        ImageAccessor actualAccessor = ImageAccessor.create(checked);

        int width = actual.getWidth();
        int height = actual.getHeight();

        BufferedImage mismatch = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        ImageAccessor mismatchAccessor = ImageAccessor.create(mismatch);

        for (int x=0; x<actual.getWidth(); x++) {
            for (int y=0; y<actual.getHeight(); y++) {

                float[] expectedHSV = PixelColorUtil.convertRGBtoHSV(expectedAccessor.getPixel(x,y));
                float[] actualHSV = PixelColorUtil.convertRGBtoHSV(actualAccessor.getPixel(x,y));

                double distance = PixelColorUtil.calculateDistanceHSV(expectedHSV, actualHSV);

                if (distance >= threshold)
                    mismatchAccessor.setPixel(x,y, 0, 255, 0, 0);
            }
        }

        File outputfile = new File("image.jpg");
        try {
            ImageIO.write(mismatch, "jpg", outputfile);
        } catch (Exception ignored) {
            System.out.println("Failed to write mismatch image");
        }
    }

}