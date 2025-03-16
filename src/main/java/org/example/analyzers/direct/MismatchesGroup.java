package org.example.analyzers.direct;

import java.awt.Rectangle;

// TODO: Either introduce max mismatch group size or remove it from this class
public record MismatchesGroup(int size, int minX, int maxX, int minY, int maxY) {

    public Rectangle getBoundingRectangle() {
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
}
