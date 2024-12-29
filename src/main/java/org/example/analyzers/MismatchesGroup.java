package org.example.analyzers;

import java.awt.Rectangle;

public record MismatchesGroup(int size, int minX, int maxX, int minY, int maxY) {

    public Rectangle getBoundingRectangle() {
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
}
