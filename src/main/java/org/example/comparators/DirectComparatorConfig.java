package org.example.comparators;

import org.example.analyzers.direct.ColorSpace;
import org.example.analyzers.direct.MarkingType;

import java.awt.*;

// TODO: drop builder pattern - seems to be a bit too complex for the required solution
//  make simple object with chained setters, default values and no-args constructor
public class DirectComparatorConfig {
    private final ColorSpace colorSpace;
    private final int colorDistanceThreshold;

    private final int mismatchedPercentageThreshold;

    private final boolean produceOutputImage;
    private final MarkingType excludedAreasMarking;
    private final Color excludedMarkingColor;
    private final MarkingType mismatchedAreasMarking;
    private final Color mismatchMarkingColor;
    private final int rectangleMarkingOffset;
    private final int markingLineThickness;

    private final int pixelsSkip;
    private final int mismatchesGroupingRadius;


    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    public int getColorDistanceThreshold() {
        return colorDistanceThreshold;
    }

    public int getMismatchedPercentageThreshold() {
        return mismatchedPercentageThreshold;
    }

    public boolean isProduceOutputImage() {
        return produceOutputImage;
    }

    public MarkingType getExcludedAreasMarking() {
        return excludedAreasMarking;
    }

    public Color getExcludedMarkingColor() {
        return excludedMarkingColor;
    }

    public MarkingType getMismatchedAreasMarking() {
        return mismatchedAreasMarking;
    }

    public Color getMismatchMarkingColor() {
        return mismatchMarkingColor;
    }

    public int getRectangleMarkingOffset() {
        return rectangleMarkingOffset;
    }

    public int getMarkingLineThickness() { return markingLineThickness; }

    public int getPixelsSkip() {
        return pixelsSkip;
    }
    public int getMismatchesGroupingRadius() {
        return mismatchesGroupingRadius;
    }

    public DirectComparatorConfig(
            ColorSpace colorSpace,
            int colorDistanceThreshold,
            int mismatchedPercentageThreshold,
            boolean produceOutputImage,
            MarkingType excludedAreasMarking,
            Color excludedMarkingColor,
            MarkingType mismatchedAreasMarking,
            Color mismatchMarkingColor,
            int rectangleMarkingOffset,
            int markingLineThickness,
            int pixelsSkip,
            int mismatchesGroupingRadius) {

        this.colorSpace = colorSpace;
        this.colorDistanceThreshold = colorDistanceThreshold;
        this.mismatchedPercentageThreshold = mismatchedPercentageThreshold;
        this.produceOutputImage = produceOutputImage;
        this.excludedAreasMarking = excludedAreasMarking;
        this.excludedMarkingColor = excludedMarkingColor;
        this.mismatchedAreasMarking = mismatchedAreasMarking;
        this.mismatchMarkingColor = mismatchMarkingColor;
        this.rectangleMarkingOffset = rectangleMarkingOffset;
        this.markingLineThickness = markingLineThickness;
        this.pixelsSkip = pixelsSkip;
        this.mismatchesGroupingRadius = mismatchesGroupingRadius;
    }

    public static DirectComparatorConfig defaultConfig() {
        return new DirectCompareConfigBuilder().build();
    }


    public static class DirectCompareConfigBuilder {
        private ColorSpace colorSpace = ColorSpace.RGB;
        private int colorDistanceThreshold = 1;

        private int mismatchedPercentageThreshold = 0;

        private boolean produceOutputImage = true;
        private MarkingType excludedAreasMarking = MarkingType.OUTLINE;
        private Color excludedMarkingColor = Color.GREEN;
        private MarkingType mismatchedAreasMarking = MarkingType.OUTLINE;
        private Color mismatchedMarkingColor = Color.RED;
        private int rectangleMarkingOffset = 3;
        private int markingLineThickness = 3;

        private int pixelsSkip = 1;
        private int mismatchesGroupingRadius = 3;


        public DirectCompareConfigBuilder colorSpace(ColorSpace colorSpace) {
            this.colorSpace = colorSpace;
            return this;
        }

        public DirectCompareConfigBuilder colorDistanceThreshold(int distance) {
            if (distance < 0 || distance > 100) {
                throw new IllegalArgumentException("Distance must be between 0 and 100");
            }
            this.colorDistanceThreshold = distance;
            return this;
        }

        public DirectCompareConfigBuilder mismatchedPercentageThreshold(int threshold) {
            if (threshold < 0 || threshold > 100) {
                throw new IllegalArgumentException("Threshold must be between 0 and 100");
            }
            this.mismatchedPercentageThreshold = threshold;
            return this;
        }

        public DirectCompareConfigBuilder produceOutputImage(boolean shouldProduce) {
            this.produceOutputImage = shouldProduce;
            return this;
        }

        public DirectCompareConfigBuilder excludedAreasMarking(MarkingType type) {
            this.excludedAreasMarking = type;
            return this;
        }

        public DirectCompareConfigBuilder excludedMarkingColor(Color color) {
            this.excludedMarkingColor = color;
            return this;
        }

        public DirectCompareConfigBuilder mismatchedAreasMarking(MarkingType type) {
            this.mismatchedAreasMarking = type;
            return this;
        }

        public DirectCompareConfigBuilder mismatchedMarkingColor(Color color) {
            this.mismatchedMarkingColor = color;
            return this;
        }

        public DirectCompareConfigBuilder rectangleMarkingOffset(int offset) {
            this.rectangleMarkingOffset = offset;
            return this;
        }

        public DirectCompareConfigBuilder markingLineThickness(int thickness) {
            if (thickness < 1) {
                throw new IllegalArgumentException("Line thickness must be at least 1");
            }

            this.markingLineThickness = thickness;
            return this;
        }

        public DirectCompareConfigBuilder pixelsSkip(int number) {
            if (number<0) {
                throw new IllegalArgumentException("Cannot set pixel gap to lower than 0");
            }

            this.pixelsSkip = number;
            return this;
        }

        public DirectCompareConfigBuilder mismatchesGroupingRadius(int radius) {
            if (radius<1) {
                throw new IllegalArgumentException("Cannot set grouping radius to lower than 1");
            }

            this.mismatchesGroupingRadius = radius;
            return this;
        }

        public DirectComparatorConfig build() {
            return new DirectComparatorConfig(
                    colorSpace,
                    colorDistanceThreshold,
                    mismatchedPercentageThreshold,
                    produceOutputImage,
                    excludedAreasMarking,
                    excludedMarkingColor,
                    mismatchedAreasMarking,
                    mismatchedMarkingColor,
                    rectangleMarkingOffset,
                    markingLineThickness,
                    pixelsSkip,
                    mismatchesGroupingRadius
            );
        }
    }
}
