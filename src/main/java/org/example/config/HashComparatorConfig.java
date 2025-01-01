package org.example.config;

public class HashComparatorConfig {
    private int reducedImageSize = 64;
    private int reducedSize = 24;

    public int getReducedImageSize() {
        return reducedImageSize;
    }

    public int getReducedSize() {
        return reducedSize;
    }

    public void setReducedImageSize(int reducedImageSize) {
        this.reducedImageSize = reducedImageSize;
    }

    public void setReducedSize(int reducedSize) {
        this.reducedSize = reducedSize;
    }
}
