package org.example.analyzers;

import org.example.utils.accessor.ImageAccessor;
import org.example.config.ColorSpace;
import org.example.config.DirectComparatorConfig;
import org.example.mismatchMarker.PixelPoint;
import org.example.utils.PixelColorUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.BiFunction;

// TODO: ADD A "FAST COMPARE" method that checks every n-other pixels instead of everything
public class BasicAnalyzer {
    private BiFunction<Integer, Integer, Integer> distanceCalculationMethod;

    private final int distanceThreshold;


    public BasicAnalyzer(DirectComparatorConfig config) {
        this.distanceThreshold = config.getColorDistanceThreshold();
        ColorSpace comparisonSpace = config.getColorSpace();

        switch (comparisonSpace) {
            case RGB -> {
                distanceCalculationMethod = PixelColorUtil::normalizedDistanceRGB;
            }
            case WEIGHTED_RGB -> {
                distanceCalculationMethod = PixelColorUtil::normalizedDistanceWeightedRGB;
            }
            case HSV -> {
                distanceCalculationMethod = (actualRGB, checkedRGB) -> {
                    float[] actualHSV = PixelColorUtil.convertRGBtoHSV(actualRGB);
                    float[] checkedHSV = PixelColorUtil.convertRGBtoHSV(checkedRGB);
                    return PixelColorUtil.normalizedDistanceHSV(actualHSV, checkedHSV);
                };
            }
            case CIELAB -> {
                throw new RuntimeException("CIE-Lab isn't yet supported");
            }
        }
    }

    public Mismatches compare(BufferedImage actual, BufferedImage checked) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int width = actual.getWidth();
        int height = actual.getHeight();

        ArrayList<PixelPoint> mismatches = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int actualRGB = actualAccessor.getPixel(x, y);
                int checkedRGB = checkedAccessor.getPixel(x, y);
                int distance = distanceCalculationMethod.apply(actualRGB, checkedRGB);

                if (distance > distanceThreshold) {
                    mismatches.add(new PixelPoint(x, y));
                }
            }
        }
        return new Mismatches(mismatches);
    }

    public Mismatches compareEveryNth(BufferedImage actual, BufferedImage checked, int pixelGap) {
        ImageAccessor actualAccessor = ImageAccessor.create(actual);
        ImageAccessor checkedAccessor = ImageAccessor.create(checked);

        int width = actual.getWidth();
        int height = actual.getHeight();

        pixelGap+=1;

        ArrayList<PixelPoint> mismatches = new ArrayList<>();
        for (int x = 0; x < width; x+=pixelGap) {
            for (int y = 0; y < height; y+=pixelGap) {
                int actualRGB = actualAccessor.getPixel(x, y);
                int checkedRGB = checkedAccessor.getPixel(x, y);
                int distance = distanceCalculationMethod.apply(actualRGB, checkedRGB);

                if (distance > distanceThreshold) {
                    mismatches.add(new PixelPoint(x, y));
                }
            }
        }
        return new Mismatches(mismatches);
    }

}