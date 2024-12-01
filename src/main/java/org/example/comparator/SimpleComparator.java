package org.example.comparator;

import org.example.accessor.ImageAccessor;
import org.example.utils.PixelColorUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SimpleComparator implements ImageComparator {

    @Override
    public boolean[][] compare(BufferedImage actual, BufferedImage checked) {
        float threshold = 1f;

        ImageAccessor expectedAccessor = ImageAccessor.create(actual);
        ImageAccessor actualAccessor = ImageAccessor.create(checked);

        int width = actual.getWidth();
        int height = actual.getHeight();

        boolean[][] differences = new boolean[width][height];
        for (int x=0; x<actual.getWidth(); x++) {
            for (int y=0; y<actual.getHeight(); y++) {

                float[] expectedHSV = PixelColorUtil.convertRGBtoHSV(expectedAccessor.getPixel(x,y));
                float[] actualHSV = PixelColorUtil.convertRGBtoHSV(actualAccessor.getPixel(x,y));
                double distance = PixelColorUtil.calculateDistanceHSV(expectedHSV, actualHSV);

                if (distance >= threshold)
                    differences[x][y] = true;
            }
        }

        return differences;
    }

}