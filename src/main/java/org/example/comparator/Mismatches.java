package org.example.comparator;

import java.awt.*;
import java.util.HashSet;

public class Mismatches {

    private final int totalMismatched;
    private HashSet<int[]> mismatchedPixels;

    public Mismatches(HashSet<int[]> mismatchedPixels, int totalMismatched) {
        this.totalMismatched = totalMismatched;
        this.mismatchedPixels = mismatchedPixels;
    }

    public int getMismatchesCount() {
        return totalMismatched;
    }

    public HashSet<int[]> getPixels() {
        return mismatchedPixels;
    }

    public void setMismatchedPixels(HashSet<int[]> mismatchedPixels) {
        this.mismatchedPixels = mismatchedPixels;
    }

    public void excludeResults(boolean[][] excluded) {
        mismatchedPixels.removeIf(pixel -> excluded[pixel[0]][pixel[1]]);
    }
}
