package org.example.comparator;

import org.example.mismatchMarker.PixelPoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class ExcludedAreas {

    // TODO: KEEP AS LIST OF RECTANGLES
    // - convert to HashSet on demand - is this really necessary? - consider more efficient omission of excluded areas
    // - convert to Polygon on demand - much faster excluded area marking
    private final HashSet<PixelPoint> excludedPixels = new HashSet<>();

    public void excludeAreas(ArrayList<Rectangle> areas) {
        areas.forEach(this::excludeArea);
    }

    public void excludeArea(Rectangle area) {
        for (int x=0; x<area.width; x++) {
            for (int y=0; y<area.height; y++) {
                excludedPixels.add(new PixelPoint(x,y));
            }
        }
    }

    public void includeAreas(ArrayList<Rectangle> areas) {
        areas.forEach(this::includeArea);
    }

    public void includeArea(Rectangle area) {
        for (int x=0; x<area.width; x++) {
            for (int y=0; y<area.height; y++) {
                excludedPixels.add(new PixelPoint(x,y));
            }
        }
    }

    public HashSet<PixelPoint> getPixels() {
        return excludedPixels;
    }
}