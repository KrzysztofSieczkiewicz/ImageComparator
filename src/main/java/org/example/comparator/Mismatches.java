package org.example.comparator;

import org.example.mismatchMarker.PixelPoint;

import java.awt.*;
import java.util.HashSet;

public class Mismatches {

    private final int totalMismatched;
    private final HashSet<PixelPoint> mismatchedPixels;

    public Mismatches(HashSet<PixelPoint> mismatchedPixels, int totalMismatched) {
        this.totalMismatched = totalMismatched;
        this.mismatchedPixels = mismatchedPixels;
    }

    public int getMismatchesCount() {
        return totalMismatched;
    }

    public HashSet<PixelPoint> getPixels() {
        return mismatchedPixels;
    }

    public void excludePixels(ExcludedAreas excluded) {
        mismatchedPixels.removeIf(pixelPoint -> excluded.contains(
                pixelPoint.getX(), pixelPoint.getY()
        ));
    }
}
