package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;

import java.util.ArrayList;

public class LocalExtremes {

    private ArrayList<PixelPoint> minima;
    private ArrayList<PixelPoint> maxima;


    public LocalExtremes() {
        minima = new ArrayList<>();
        maxima = new ArrayList<>();
    }


    public void addToMinima(int x, int y) {
        minima.add(new PixelPoint(x, y));
    }

    public void addToMaxima(int x, int y) {
        maxima.add(new PixelPoint(x, y));
    }


    public ArrayList<PixelPoint> getMinima() {
        return minima;
    }

    public ArrayList<PixelPoint> getMaxima() {
        return maxima;
    }
}
