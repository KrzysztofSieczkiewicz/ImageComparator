package org.example.config;

public class HashComparatorConfig {
    private int size = 64;
    private int reducedSize = 24;

    public int getSize() {
        return size;
    }

    public int getReducedSize() {
        return reducedSize;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setReducedSize(int reducedSize) {
        this.reducedSize = reducedSize;
    }
}
