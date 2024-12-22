package org.example.config;

import java.awt.*;

public class DirectCompareConfig {
    private final ColorSpace colorSpace;
    private final int colorDistanceThreshold;
    private final int mismatchedPercentageThreshold;
    private final ExcludedMarkingType excludedAreasMarking;
    private final Color excludedMarkingColor;
    private final MismatchMarkingType mismatchedAreasMarking;
    private final Color mismatchMarkingColor;
    private final int rectangleMarkingOffset;
    private final int markingLineThickness;


    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    public int getColorDistanceThreshold() {
        return colorDistanceThreshold;
    }

    public int getMismatchedPercentageThreshold() {
        return mismatchedPercentageThreshold;
    }

    public ExcludedMarkingType getExcludedAreasMarking() {
        return excludedAreasMarking;
    }
    public Color getExcludedMarkingColor() {
        return excludedMarkingColor;
    }

    public MismatchMarkingType getMismatchedAreasMarking() {
        return mismatchedAreasMarking;
    }

    public Color getMismatchMarkingColor() {
        return mismatchMarkingColor;
    }

    public int getRectangleMarkingOffset() {
        return rectangleMarkingOffset;
    }

    public int getMarkingLineThickness() { return markingLineThickness; }

    public DirectCompareConfig(
            ColorSpace colorSpace,
            int colorDistanceThreshold,
            int mismatchedPercentageThreshold,
            ExcludedMarkingType excludedAreasMarking,
            Color excludedMarkingColor,
            MismatchMarkingType mismatchedAreasMarking,
            Color mismatchMarkingColor,
            int rectangleMarkingOffset,
            int markingLineThickness ) {

        this.colorSpace = colorSpace;
        this.colorDistanceThreshold = colorDistanceThreshold;
        this.mismatchedPercentageThreshold = mismatchedPercentageThreshold;
        this.excludedAreasMarking = excludedAreasMarking;
        this.excludedMarkingColor = excludedMarkingColor;
        this.mismatchedAreasMarking = mismatchedAreasMarking;
        this.mismatchMarkingColor = mismatchMarkingColor;
        this.rectangleMarkingOffset = rectangleMarkingOffset;
        this.markingLineThickness = markingLineThickness;
    }

    public static DirectCompareConfig defaultConfig() {
        return new DirectCompareConfigBuilder().build();
    }


    public static class DirectCompareConfigBuilder {
        private ColorSpace colorSpace = ColorSpace.RGB;
        private int colorDistanceThreshold = 1;

        private int mismatchedPercentageThreshold = 0;
        private ExcludedMarkingType excludedAreasMarking = ExcludedMarkingType.OUTLINE;
        private Color excludedMarkingColor = Color.GREEN;
        private MismatchMarkingType mismatchedAreasMarking = MismatchMarkingType.RECTANGLE;
        private Color mismatchedMarkingColor = Color.RED;
        private int rectangleMarkingOffset = 3;
        private int markingLineThickness = 3;


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

        public DirectCompareConfigBuilder excludedAreasMarking(ExcludedMarkingType type) {
            this.excludedAreasMarking = type;
            return this;
        }

        public DirectCompareConfigBuilder excludedMarkingColor(Color color) {
            this.excludedMarkingColor = color;
            return this;
        }

        public DirectCompareConfigBuilder mismatchedAreasMarking(MismatchMarkingType type) {
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

        public DirectCompareConfig build() {
            return new DirectCompareConfig(
                    colorSpace,
                    colorDistanceThreshold,
                    mismatchedPercentageThreshold,
                    excludedAreasMarking,
                    excludedMarkingColor,
                    mismatchedAreasMarking,
                    mismatchedMarkingColor,
                    rectangleMarkingOffset,
                    markingLineThickness
            );
        }
    }
}
