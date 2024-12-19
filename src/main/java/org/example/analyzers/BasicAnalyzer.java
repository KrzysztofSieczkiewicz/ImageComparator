package org.example.analyzers;

import org.example.accessor.ImageAccessor;
import org.example.config.ColorSpace;
import org.example.config.DirectCompareConfig;
import org.example.mismatchMarker.PixelPoint;
import org.example.utils.PixelColorUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.BiFunction;

public class BasicAnalyzer {
    private BiFunction<BufferedImage, BufferedImage, Mismatches> comparisonMethod = this::compareRGB;

    private final int distanceThreshold;


    public BasicAnalyzer(DirectCompareConfig config) {
        this.distanceThreshold = config.getColorDistanceThreshold();
        ColorSpace comparisonSpace = config.getColorSpace();

        switch (comparisonSpace) {
            case RGB -> comparisonMethod = this::compareRGB;
            case WEIGHTED_RGB -> comparisonMethod = this::compareWeightedRGB;
            case HSV -> comparisonMethod = this::compareHSV;
            case CIELAB -> throw new RuntimeException("CIE-Lab isn't yet supported");
        }
    }

    public Mismatches compare(BufferedImage actual, BufferedImage checked) {
        return comparisonMethod.apply(actual,checked);
    }


    private Mismatches performComparison(BufferedImage actual, BufferedImage checked, BiFunction<Integer, Integer, Integer> distanceCalculator) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int width = actual.getWidth();
        int height = actual.getHeight();

        ArrayList<PixelPoint> mismatches = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int actualRGB = actualAccessor.getPixel(x, y);
                int checkedRGB = checkedAccessor.getPixel(x, y);
                int distance = distanceCalculator.apply(actualRGB, checkedRGB);

                if (distance > distanceThreshold) {
                    mismatches.add(new PixelPoint(x, y));
                }
            }
        }
        return new Mismatches(mismatches);
    }

    public Mismatches compareRGB(BufferedImage actual, BufferedImage checked) {
        return performComparison(actual, checked, PixelColorUtil::normalizedDistanceRGB);
    }

    public Mismatches compareWeightedRGB(BufferedImage actual, BufferedImage checked) {
        return performComparison(actual, checked, PixelColorUtil::normalizedDistanceWeightedRGB);
    }

    public Mismatches compareHSV(BufferedImage actual, BufferedImage checked) {
        return performComparison(actual, checked, (actualRGB, checkedRGB) -> {
            float[] actualHSV = PixelColorUtil.convertRGBtoHSV(actualRGB);
            float[] checkedHSV = PixelColorUtil.convertRGBtoHSV(checkedRGB);
            return PixelColorUtil.normalizedDistanceHSV(actualHSV, checkedHSV);
        });
    }
}