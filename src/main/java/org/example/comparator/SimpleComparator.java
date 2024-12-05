package org.example.comparator;

import org.example.accessor.ImageAccessor;
import org.example.config.ColorSpace;
import org.example.utils.PixelColorUtil;

import java.awt.image.BufferedImage;
import java.util.function.BiFunction;

// TODO - ADD THRESHOLD AND DISTANCE NORMALISATION

public class SimpleComparator implements ByPixelComparator{
    private final BiFunction<BufferedImage, BufferedImage, Mismatches> comparisonMethod;

    private final float distanceThreshold = 5f*5f;
    private final ColorSpace comparisonSpace = ColorSpace.RGB;


    public SimpleComparator() {

        switch (comparisonSpace) {
            case RGB -> comparisonMethod = this::compareRGB;
            case WEIGHTED_RGB -> comparisonMethod = this::compareWeightedRGB;
            case HSV -> comparisonMethod = this::compareHSV;
            case CIELAB -> throw new RuntimeException("CIE-Lab isn't yet supported");

            default -> comparisonMethod = this::compareRGB;
        }
    }

    @Override
    public Mismatches compare(BufferedImage actual, BufferedImage checked) {
        return comparisonMethod.apply(actual,checked);
    }

    public Mismatches compareRGB(BufferedImage actual, BufferedImage checked) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int count = 0;
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
                    count++;
                }
            }
        }
        return new Mismatches(mismatches, count);
    }

    public Mismatches compareWeightedRGB(BufferedImage actual, BufferedImage checked) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int count = 0;
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
                    count++;
                }
            }
        }
        return new Mismatches(mismatches, count);
    }

    public Mismatches compareHSV(BufferedImage actual, BufferedImage checked) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int count = 0;
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
                    count++;
                }
            }
        }
        return new Mismatches(mismatches, count);
    }

}