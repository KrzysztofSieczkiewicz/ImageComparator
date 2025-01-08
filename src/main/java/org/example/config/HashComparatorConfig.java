package org.example.config;

public class HashComparatorConfig {
    private int imageTargetSize = 128;
    private int reducedSize = 2;

    public int getImageTargetSize() {
        return imageTargetSize;
    }

    public int getReducedSize() {
        return reducedSize;
    }

    public void setImageTargetSize(int imageTargetSize) {
        this.imageTargetSize = imageTargetSize;
    }

    public void setReducedSize(int reducedSize) {
        this.reducedSize = reducedSize;
    }
}
