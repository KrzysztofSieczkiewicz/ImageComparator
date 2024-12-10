package org.example.comparator;

import org.example.mismatchMarker.PixelPoint;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashSet;

public class ExcludedAreas {

    // TODO: KEEP AS LIST OF RECTANGLES
    // - convert to HashSet on demand - is this really necessary? - consider more efficient omission of excluded areas
    // - convert to Polygon on demand - much faster excluded area marking
    private Area excluded = new Area();
    private final HashSet<PixelPoint> excludedPixels = new HashSet<>();

    public void excludeAreas(ArrayList<Rectangle> rectangles) {
        rectangles.forEach(this::excludeArea);
    }

    public void excludeArea(Rectangle rectangle) {
        excluded.add(new Area(rectangle));
    }

    public void includeAreas(ArrayList<Rectangle> rectangles) {
        rectangles.forEach(this::includeArea);
    }

    public void includeArea(Rectangle rectangle) {
        excluded.subtract(new Area(rectangle));
    }

    public HashSet<PixelPoint> getPixels() {
        return excludedPixels;
    }

    public boolean contains(int x, int y) {
        return excluded.contains(x,y);
    }
}