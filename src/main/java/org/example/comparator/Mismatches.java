package org.example.comparator;

public class Mismatches {
    private final int totalMismatched;
    private boolean[][] mismatchedPixels;

    public Mismatches(boolean[][] mismatchedPixels, int totalMismatched) {
        this.totalMismatched = totalMismatched;
        this.mismatchedPixels = mismatchedPixels;
    }

    public int getMismatchesCount() {
        return totalMismatched;
    }

    public boolean[][] getPixels() {
        return mismatchedPixels;
    }

    public void setMismatchedPixels(boolean[][] mismatchedPixels) {
        this.mismatchedPixels = mismatchedPixels;
    }
}
