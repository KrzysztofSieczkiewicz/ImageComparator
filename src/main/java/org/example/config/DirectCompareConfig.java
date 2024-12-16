package org.example.config;

public class DirectCompareConfig {
    private ColorSpace colorSpace;
    private int mismatchedPercentageThreshold;
    private int minimalMismatchedGroupSize;
    private ExcludedMarkingType excludedAreasMarking;
    private MismatchMarkingType mismatchedAreasMarking;

    public DirectCompareConfig(
            ColorSpace colorSpace,
            int mismatchedPercentageThreshold,
            int minimalMismatchedGroupSize,
            ExcludedMarkingType excludedAreasMarking,
            MismatchMarkingType mismatchedAreasMarking ) {

        this.colorSpace = colorSpace;
        this.mismatchedPercentageThreshold = mismatchedPercentageThreshold;
        this.minimalMismatchedGroupSize = minimalMismatchedGroupSize;
        this.excludedAreasMarking = excludedAreasMarking;
        this.mismatchedAreasMarking = mismatchedAreasMarking;
    }

    public class DirectCompareConfigBuilder {
        private ColorSpace colorSpace = ColorSpace.RGB;

        private int mismatchedPercentageThreshold = 0;
        private int minimalMismatchedGroupSize = 0;

        private ExcludedMarkingType excludedAreasMarking = ExcludedMarkingType.OUTLINE;
        private MismatchMarkingType mismatchedAreasMarking = MismatchMarkingType.RECTANGLE;


        public DirectCompareConfigBuilder colorSpace(ColorSpace colorSpace) {
            this.colorSpace = colorSpace;
            return this;
        }

        public DirectCompareConfigBuilder mismatchedPercentageThreshold(int threshold) {
            this.mismatchedPercentageThreshold = threshold;
            return this;
        }

        public DirectCompareConfigBuilder minimalMismatchedGroupSize(int size) {
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
                    mismatchedPercentageThreshold,
                    minimalMismatchedGroupSize,
                    excludedAreasMarking,
                    mismatchedAreasMarking
            );
        }
    }
}
