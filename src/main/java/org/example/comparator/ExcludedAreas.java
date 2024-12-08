package org.example.comparator;

import java.awt.*;
import java.util.ArrayList;

public class ExcludedAreas {

    private final boolean[][] excludedPixels;

    public ExcludedAreas(int imageWidth, int imageHeight) {
        excludedPixels = new boolean[imageWidth][imageHeight];
    }

    public void excludeAreas(ArrayList<Rectangle> areas) {
        areas.forEach(this::excludeArea);
    }

    public void excludeAreas(boolean[][] pixels) {
        int width = pixels.length;
        int height = pixels[0].length;

        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if (pixels[x][y]) excludedPixels[x][y] = true;
            }
        }
    }

    public void excludeArea(Rectangle area) {
        for (int x=0; x<area.width; x++) {
            for (int y=0; y<area.height; y++) {
                excludedPixels[area.x + x][area.y + y] = true;
            }
        }
    }

    public void includeAreas(ArrayList<Rectangle> areas) {
        areas.forEach(this::includeArea);
    }

    public void includeAreas(boolean[][] pixels) {
        int width = pixels.length;
        int height = pixels[0].length;

        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                if (pixels[x][y]) excludedPixels[x][y] = false;
            }
        }
    }

    public void includeArea(Rectangle area) {
        for (int x=0; x<area.width; x++) {
            for (int y=0; y<area.height; y++) {
                excludedPixels[area.x + x][area.y + y] = false;
            }
        }
    }

    public boolean[][] getPixels() {
        return excludedPixels;
    }
}