package org.example.comparator;

import org.example.accessor.ImageAccessor;
import org.example.utils.PixelColorUtil;

import java.awt.image.BufferedImage;

public class SimpleComparator implements ByPixelComparator {

    // TODO: IN THE CONSTRUCTOR METHOD ADD SWITCH CASE THAT WILL ASSIGN DIFFERENT DISTANCE CALCULATION METHOD BASED ON CONFIG OPTION
    // HSV, WEIGHTED_RGB, RGB
    // ALSO - ADD THRESHOLD AND DISTANCE NORMALISATION
    // ALSO - SWITCH TO THRESHOLD*THRESHOLD INSTEAD OF SQRT OF EACH PIXEL

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

//                float[] expectedHSV = PixelColorUtil.convertRGBtoHSV(expectedAccessor.getPixel(x,y));
//                float[] actualHSV = PixelColorUtil.convertRGBtoHSV(actualAccessor.getPixel(x,y));
//                double distance = PixelColorUtil.calculateDistanceHSV(expectedHSV, actualHSV);

                int expectedRGB = expectedAccessor.getPixel(x,y);
                int actualRGB = actualAccessor.getPixel(x,y);
                double distance = PixelColorUtil.calculateDistanceWeightedRGB(expectedRGB, actualRGB);

                if (distance >= threshold) {
                    differences[x][y] = true;
                }
            }
        }

        return differences;
    }

}