package com.sieczk.comparators;

import com.sieczk.analyzers.direct.ColorSpace;
import com.sieczk.analyzers.direct.MarkingType;

import java.awt.Color;

public class DirectComparatorConfig extends BaseComparatorConfig {

    /**
     * Which color space should be used when calculating pixel color distance
     */
    private ColorSpace colorSpace = ColorSpace.RGB;

    /**
     * Above what color distance will the pixels be considered different
     * If set to 1, any difference will result in pixels being marked as different
     */
    private int colorDistanceThreshold = 1;

    /**
     * Above what percent of mismatched pixels in the image, will the image be marked as mismatched
     */
    private int mismatchedPercentageThreshold = 0;

    /**
     * Should the comparison return output image with marked mismatches and excluded areas
     */
    private boolean produceOutputImage = true;

    /**
     * How excluded areas are supposed to be marked in the output image
     */
    private MarkingType excludedAreasMarking = MarkingType.OUTLINE;

    /**
     * What color are excluded areas supposed to be marked with
     */
    private Color excludedMarkingColor = Color.GREEN;

    /**
     * How mismatched areas are supposed to be marked in the output image
     */
    private MarkingType mismatchedAreasMarking = MarkingType.OUTLINE;

    /**
     * What color are mismatched areas supposed to be marked with
     */
    private Color mismatchedMarkingColor = Color.RED;

    /**
     * How far are excluded/mismatched markings to be offset from actual pixels.
     * If set to 0 the marking will overlap
     */
    private int markingOffset = 3;

    /**
     * Line thickness for excluded/mismatched marking
     */
    private int markingLineThickness = 3;

    /**
     * Works only with fastCompare method. Switches to every n-th pixel comparison (e.g. setting to 3 results in every third pixel being compared in each dimension).
     * Results in faster, but less sensitive comparison.
     */
    private int pixelsSkipped = 0;

    /**
     * In what radius will the mismatches be grouped. Reduces number of separate areas found
     */
    private int mismatchesGroupingRadius = 3;


    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    public DirectComparatorConfig colorSpace(ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        return this;
    }

    public int getColorDistanceThreshold() {
        return colorDistanceThreshold;
    }

    public DirectComparatorConfig colorDistanceThreshold(int distance) {
        if (distance < 0 || distance > 100)
            throw new IllegalArgumentException("Distance must be between 0 and 100");

        this.colorDistanceThreshold = distance;
        return this;
    }

    public int getMismatchedPercentageThreshold() {
        return mismatchedPercentageThreshold;
    }

    public DirectComparatorConfig mismatchedPercentageThreshold(int threshold) {
        if (threshold < 0 || threshold > 100)
            throw new IllegalArgumentException("Threshold must be between 0 and 100");

        this.mismatchedPercentageThreshold = threshold;
        return this;
    }

    public boolean isProduceOutputImage() {
        return produceOutputImage;
    }

    public DirectComparatorConfig returnOutputImage(boolean shouldProduce) {
        this.produceOutputImage = shouldProduce;
        return this;
    }

    public MarkingType getExcludedAreasMarking() {
        return excludedAreasMarking;
    }

    public DirectComparatorConfig excludedAreasMarking(MarkingType type) {
        this.excludedAreasMarking = type;
        return this;
    }

    public Color getExcludedMarkingColor() {
        return excludedMarkingColor;
    }

    public DirectComparatorConfig excludedMarkingColor(Color color) {
        this.excludedMarkingColor = color;
        return this;
    }

    public MarkingType getMismatchedAreasMarking() {
        return mismatchedAreasMarking;
    }

    public DirectComparatorConfig mismatchedAreasMarking(MarkingType type) {
        this.mismatchedAreasMarking = type;
        return this;
    }

    public Color getMismatchedMarkingColor() {
        return mismatchedMarkingColor;
    }

    public DirectComparatorConfig mismatchedMarkingColor(Color color) {
        this.mismatchedMarkingColor = color;
        return this;
    }

    public int getMarkingOffset() {
        return markingOffset;
    }

    public DirectComparatorConfig markingOffset(int offset) {
        if (offset < 0)
            throw new IllegalArgumentException("Marking offset cannot be lower than 0");

        this.markingOffset = offset;
        return this;
    }

    public int getMarkingLineThickness() {
        return markingLineThickness;
    }

    public DirectComparatorConfig markingLineThickness(int thickness) {
        if (thickness < 1)
            throw new IllegalArgumentException("Marking line thickness must be at least 1");

        this.markingLineThickness = thickness;
        return this;
    }

    public int getPixelsSkipped() {
        return pixelsSkipped;
    }

    public DirectComparatorConfig pixelsSkipped(int number) {
        if (number < 0)
            throw new IllegalArgumentException("Pixel gap cannot be lower than 0");

        this.pixelsSkipped = number;
        return this;
    }

    public int getMismatchesGroupingRadius() {
        return mismatchesGroupingRadius;
    }

    public DirectComparatorConfig mismatchesGroupingRadius(int radius) {
        if (radius < 1)
            throw new IllegalArgumentException("Grouping cannot be lower than 1");

        this.mismatchesGroupingRadius = radius;
        return this;
    }

}
