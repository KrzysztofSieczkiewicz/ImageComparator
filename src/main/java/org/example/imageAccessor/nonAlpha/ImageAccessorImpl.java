package org.example.imageAccessor.nonAlpha;

import org.example.imageAccessor.ImageAccessor;

/**
 * Implementation of ImageAccessor for images without alpha channel
 * When alpha channel data is requested methods return 0 or false
 */
public abstract class ImageAccessorImpl implements ImageAccessor {

    // Image dimensions
    protected final int width;
    protected final int height;

    protected static final int ALPHA_MASK = 255 << 24;


    public ImageAccessorImpl(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /*
         Pixel
     */
    @Override
    public int getPixel(int index) {
        return (ALPHA_MASK | getRed(index) << 16) | (getGreen(index) << 8) | (getBlue(index));
    }

    @Override
    public int getPixel(int x, int y) {
        return getPixel(get1dIndex(x, y));
    }

    @Override
    public int[][] getPixels() {
        int[][] rgb = new int[width][height];

        for (int i = 0; i < width * height; i++) {
            int x = i % width;   // Calculate x-coordinate
            int y = i / width;   // Calculate y-coordinate
            rgb[x][y] = getPixel(x, y);
        }
        return rgb;
    }

    /*
        ALPHA CHANNEL
     */
    @Override
    public final int getAlpha(int index) {
        return 0;
    }

    @Override
    public int getAlpha(int x, int y) {
        return 0;
    }

    @Override
    public int[][] getAlpha() {
        return new int[width][height];
    }

    /*
        RED CHANNEL
     */
    @Override
    public abstract int getRed(int index);

    @Override
    public final int getRed(int x, int y) {
        return getRed(get1dIndex(x, y));
    }

    @Override
    public final int[][] getRed() {
        int[][] imageRed = new int[width][height];
        for (int i=0; i<width*height; i++) {
            int x = i % width;
            int y = i / width;
            imageRed[x][y] = getRed(x,y);
        }
        return imageRed;
    }

    /*
        GREEN CHANNEL
     */
    @Override
    public abstract int getGreen(int index);

    @Override
    public final int getGreen(int x, int y) {
        return getGreen(get1dIndex(x, y));
    }

    @Override
    public final int[][] getGreen() {
        int[][] imageGreen = new int[width][height];
        for (int i=0; i<width*height; i++) {
            int x = i % width;
            int y = i / width;
            imageGreen[x][y] = getGreen(x,y);
        }
        return imageGreen;
    }

    /*
        BLUE CHANNEL
     */
    @Override
    public abstract int getBlue(int index);

    @Override
    public final int getBlue(int x, int y) {
        return getBlue(get1dIndex(x, y));
    }

    @Override
    public final int[][] getBlue() {
        int[][] imageBlue = new int[width][height];
        for (int i=0; i<width*height; i++) {
            int x = i % width;
            int y = i / width;
            imageBlue[x][y] = getBlue(x,y);
        }
        return imageBlue;
    }

/*
    HELPERS
 */
    /**
     * Maps the XY coordinates to the 1D array index.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return 1D array index
     */
    public int get1dIndex(int x, int y) {
        return (y * width) + x;
    }
}