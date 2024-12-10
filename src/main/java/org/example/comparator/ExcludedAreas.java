package org.example.comparator;

import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;

public class ExcludedAreas {
    private Area excluded = new Area();


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


    public boolean contains(int x, int y) {
        return excluded.contains(x,y);
    }
}