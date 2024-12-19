package org.example.analyzers;

import java.awt.*;

public class MismatchesGroup {
    private int size;

    private int minX;
    private int maxX;
    private int minY;
    private int maxY;

    public MismatchesGroup(int size, int minX, int maxX, int minY, int maxY) {
        this.size = size;

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public int getSize() {
        return size;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }
}
