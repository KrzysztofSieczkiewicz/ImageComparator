package org.example.comparator;

import org.example.mismatchMarker.PixelPoint;

import java.awt.*;
import java.util.HashSet;

public class Mismatches {

    private final int totalMismatched;
    private HashSet<PixelPoint> mismatchedPixels;

    public Mismatches(HashSet<PixelPoint> mismatchedPixels, int totalMismatched) {
        this.totalMismatched = totalMismatched;
        this.mismatchedPixels = mismatchedPixels;
    }

    public int getMismatchesCount() {
        return totalMismatched;
    }

    public HashSet<PixelPoint> getPixels() {
        return mismatchedPixels;
    }

    public void setMismatchedPixels(HashSet<PixelPoint> mismatchedPixels) {
        this.mismatchedPixels = mismatchedPixels;
    }

    public void excludeResults(HashSet<PixelPoint> excluded) {
        mismatchedPixels.removeIf(excluded::contains);
    }
}
