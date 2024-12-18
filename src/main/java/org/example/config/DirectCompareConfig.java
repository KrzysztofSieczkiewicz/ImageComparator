package org.example.config;

public class DirectCompareConfig {
    private final ColorSpace colorSpace;
    private final int colorDistanceThreshold;
    private final int mismatchedPercentageThreshold;
    private final int minimalMismatchedGroupSize;
    private final ExcludedMarkingType excludedAreasMarking;
    private final MismatchMarkingType mismatchedAreasMarking;


    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    public int getColorDistanceThreshold() {
        return colorDistanceThreshold;
    }

    public int getMismatchedPercentageThreshold() {
        return mismatchedPercentageThreshold;
    }

    public int getMinimalMismatchedGroupSize() {
        return minimalMismatchedGroupSize;
    }

    public ExcludedMarkingType getExcludedAreasMarking() {
        return excludedAreasMarking;
    }

    public MismatchMarkingType getMismatchedAreasMarking() {
        return mismatchedAreasMarking;
    }

    public DirectCompareConfig(
            ColorSpace colorSpace,
            int colorDistanceThreshold,
            int mismatchedPercentageThreshold,
            int minimalMismatchedGroupSize,
            ExcludedMarkingType excludedAreasMarking,
            MismatchMarkingType mismatchedAreasMarking ) {

        this.colorSpace = colorSpace;
        this.colorDistanceThreshold = colorDistanceThreshold;
        this.mismatchedPercentageThreshold = mismatchedPercentageThreshold;
        this.minimalMismatchedGroupSize = minimalMismatchedGroupSize;
        this.excludedAreasMarking = excludedAreasMarking;
        this.mismatchedAreasMarking = mismatchedAreasMarking;
    }

    public static DirectCompareConfig defaultConfig() {
        return new DirectCompareConfigBuilder().build();
    }


    public static class DirectCompareConfigBuilder {
        private ColorSpace colorSpace = ColorSpace.RGB;
        private int colorDistanceThreshold = 1;

        private int mismatchedPercentageThreshold = 0;
        private int minimalMismatchedGroupSize = 0;

        private ExcludedMarkingType excludedAreasMarking = ExcludedMarkingType.OUTLINE;
        private MismatchMarkingType mismatchedAreasMarking = MismatchMarkingType.RECTANGLE;


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

        public DirectCompareConfigBuilder minimalMismatchedGroupSize(int size) {
            if (size < 0) {
                throw new IllegalArgumentException("Size must be larger than 0");
            }
            this.minimalMismatchedGroupSize = size;
            return this;
        }

        public DirectCompareConfigBuilder excludedAreasMarking(ExcludedMarkingType type) {
            this.excludedAreasMarking = type;
            return this;
        }

        public DirectCompareConfigBuilder mismatchedAreasMarking(MismatchMarkingType type) {
            this.mismatchedAreasMarking = type;
            return this;
        }

        public DirectCompareConfig build() {
            return new DirectCompareConfig(
                    colorSpace,
                    colorDistanceThreshold,
                    mismatchedPercentageThreshold,
                    minimalMismatchedGroupSize,
                    excludedAreasMarking,
                    mismatchedAreasMarking
            );
        }
    }
}
