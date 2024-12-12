package org.example.comparator;

import org.example.accessor.ImageAccessor;
import org.example.config.ColorSpace;
import org.example.mismatchMarker.PixelPoint;
import org.example.utils.PixelColorUtil;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.function.BiFunction;

// TODO - ADD THRESHOLD AND DISTANCE NORMALISATION
// TODO: Move reoccurring code to separate function - no need to repeat the same checks

public class SimpleComparator {
    private final BiFunction<BufferedImage, BufferedImage, Mismatches> comparisonMethod;
    private final int distanceThreshold = 25;
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

    public Mismatches compare(BufferedImage actual, BufferedImage checked) {
        return comparisonMethod.apply(actual,checked);
    }


    private Mismatches compareImage(BufferedImage actual, BufferedImage checked, BiFunction<Integer, Integer, Integer> distanceCalculator) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int count = 0;
        int width = actual.getWidth();
        int height = actual.getHeight();

        HashSet<PixelPoint> mismatches = new HashSet<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int actualRGB = actualAccessor.getPixel(x, y);
                int checkedRGB = checkedAccessor.getPixel(x, y);
                int distance = distanceCalculator.apply(actualRGB, checkedRGB);

                if (distance >= distanceThreshold) {
                    mismatches.add(new PixelPoint(x, y));
                    count++;
                }
            }
        }
        return new Mismatches(mismatches, count);
    }

    public Mismatches compareRGB(BufferedImage actual, BufferedImage checked) {
        return compareImage(actual, checked, PixelColorUtil::normalizedDistanceRGB);
    }

    public Mismatches compareWeightedRGB(BufferedImage actual, BufferedImage checked) {
        return compareImage(actual, checked, PixelColorUtil::normalizedDistanceWeightedRGB);
    }

    public Mismatches compareHSV(BufferedImage actual, BufferedImage checked) {
        return compareImage(actual, checked, (actualRGB, checkedRGB) -> {
            float[] actualHSV = PixelColorUtil.convertRGBtoHSV(actualRGB);
            float[] checkedHSV = PixelColorUtil.convertRGBtoHSV(checkedRGB);
            return PixelColorUtil.normalizedDistanceHSV(actualHSV, checkedHSV);
        });
    }

}