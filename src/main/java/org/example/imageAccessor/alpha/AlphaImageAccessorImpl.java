package org.example.imageAccessor.alpha;

import org.example.imageAccessor.ImageAccessor;

/**
 * Implementation of ImageAccessor for images with alpha channel
 * When alpha channel data is requested each image type is handled separately
 */
public abstract class AlphaImageAccessorImpl implements ImageAccessor {

    // Image dimensions
    protected final int width;
    protected final int height;

    // Alpha
    protected int alphaPositionOffset;


    public AlphaImageAccessorImpl(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /*
        ARGB
     */
    @Override
    public int getPixel(int index) {
        return (getAlpha(index) << 24) | (getRed(index) << 16)
                | (getGreen(index) << 8) | (getBlue(index));
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
    public abstract int getAlpha(int index);

    @Override
    public int getAlpha(int x, int y) {
        return getAlpha(get1dIndex(x, y));
    }

    @Override
    public int[][] getAlpha() {
        int[][] imageAlpha = new int[width][height];
        for (int i=0; i<width*height; i++) {
            int x = i % width;
            int y = i / width;
            imageAlpha[x][y] = getAlpha(x,y);
        }
        return imageAlpha;
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

    /**
     * Maps the XY coordinates to the 1D array index.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return 1D array index
     */
    protected int get1dIndex(int x, int y) {
        return (y * width) + x;
    }
}
