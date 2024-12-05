package org.example.comparator;

import org.example.accessor.ImageAccessor;
import org.example.config.ColorSpace;
import org.example.utils.PixelColorUtil;

import java.awt.image.BufferedImage;
import java.util.function.BiFunction;

// TODO - ADD THRESHOLD AND DISTANCE NORMALISATION
// TODO - SWITCH TO THRESHOLD*THRESHOLD INSTEAD OF SQRT OF EACH PIXEL

public class SimpleComparator implements ByPixelComparator{
    private BiFunction<BufferedImage, BufferedImage, boolean[][]> comparisonMethod;

    private final float distanceThreshold;


    public SimpleComparator(ColorSpace comparisonSpace, float threshold) {

        // to avoid Math.sqrt() in distance calculations
        this.distanceThreshold = threshold*threshold;

        switch (comparisonSpace) {
            case RGB -> comparisonMethod = this::compareRGB;
            case WEIGHTED_RGB -> comparisonMethod = this::compareWeightedRGB;
            case HSV -> comparisonMethod = this::compareHSV;
            case CIELAB -> throw new RuntimeException("CIE-Lab isn't yet supported");

            default -> comparisonMethod = this::compareRGB;
        }
    }

    @Override
    public boolean[][] compare(BufferedImage actual, BufferedImage checked) {
        return comparisonMethod.apply(actual,checked);
    }

    public boolean[][] compareRGB(BufferedImage actual, BufferedImage checked) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int width = actual.getWidth();
        int height = actual.getHeight();

        boolean[][] mismatches = new boolean[width][height];
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {

                int actualRGB = actualAccessor.getPixel(x,y);
                int checkedRGB = checkedAccessor.getPixel(x,y);
                double distance = PixelColorUtil.calculateDistanceRGB(actualRGB, checkedRGB);

                if (distance >= distanceThreshold) {
                    mismatches[x][y] = true;
                }
            }
        }
        return mismatches;
    }

    public boolean[][] compareWeightedRGB(BufferedImage actual, BufferedImage checked) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int width = actual.getWidth();
        int height = actual.getHeight();

        boolean[][] mismatches = new boolean[width][height];
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {

                int actualRGB = actualAccessor.getPixel(x,y);
                int checkedRGB = checkedAccessor.getPixel(x,y);
                double distance = PixelColorUtil.calculateDistanceWeightedRGB(actualRGB, checkedRGB);

                if (distance >= distanceThreshold) {
                    mismatches[x][y] = true;
                }
            }
        }
        return mismatches;
    }

    public boolean[][] compareHSV(BufferedImage actual, BufferedImage checked) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int width = actual.getWidth();
        int height = actual.getHeight();

        boolean[][] mismatches = new boolean[width][height];
        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                float[] actualHSV = PixelColorUtil.convertRGBtoHSV(actualAccessor.getPixel(x,y));
                float[] expectedHSV = PixelColorUtil.convertRGBtoHSV(checkedAccessor.getPixel(x,y));
                double distance = PixelColorUtil.calculateDistanceHSV(actualHSV, expectedHSV);
                if (distance >= distanceThreshold) {
                    mismatches[x][y] = true;
                }
            }
        }
        return mismatches;
    }
}