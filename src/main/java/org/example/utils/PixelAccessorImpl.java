package org.example.utils;

public abstract class PixelAccessorImpl implements PixelAccessor {

    // Image dimensions
    protected final int width;
    protected final int height;

    // Alpha
    protected static final int ALPHA_MASK = 255 << 24;
    protected boolean hasImageAlpha = false;    // set to true if image has an alpha channel
    protected int alphaPositionOffset;

    protected int alphaReplacementThreshold = -1;
    protected int replacementAlpha = -1;
    protected int replacementRed = -1;
    protected int replacementGreen = -1;
    protected int replacementBlue = -1;


    public PixelAccessorImpl(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /*
        ARGB
     */
    @Override
    public int getARGB(int index) {
        return (hasImageAlpha ? (getAlpha(index) << 24) : ALPHA_MASK) | (getRed(index) << 16)
                | (getGreen(index) << 8) | (getBlue(index));
    }

    @Override
    public int getARGB(int x, int y) {
        return getARGB(get1dIndex(x, y));
    }

    @Override
    public int[][] getARGB() {
        int[][] rgb = new int[width][height];

        for (int i = 0; i < width * height; i++) {
            int x = i % width;   // Calculate x-coordinate
            int y = i / width;   // Calculate y-coordinate
            rgb[x][y] = getARGB(x, y);
        }
        return rgb;
    }

    /*
        ALPHA CHANNEL
     */
    @Override
    public boolean isReplaceAlphaSet() {
        return alphaReplacementThreshold >= 0;
    }

    @Override
    public void setAlphaReplacement(int threshold, int red, int green, int blue, int alpha) {
        this.alphaReplacementThreshold = threshold;
        this.replacementRed = red;
        this.replacementGreen = green;
        this.replacementBlue = blue;
        this.replacementAlpha = alpha;
    }

    @Override
    public final int getAlpha(int index) {
        if (!hasImageAlpha || !isReplaceAlphaSet())
            return getRawAlpha(index);

        int rawAlpha = getRawAlpha(index);
        if (rawAlpha >= alphaReplacementThreshold)
            return replacementAlpha;

        return rawAlpha;
    }

    @Override
    public int getAlpha(int x, int y) {
        return getAlpha(get1dIndex(x, y));
    }

    @Override
    public int[][] getAlpha() {
        if (!hasImageAlpha)
            return new int[width][height];

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
    public int getRed(int index) {
        if (!hasImageAlpha)
            return getRawRed(index);

        if (getRawAlpha(index) >= alphaReplacementThreshold)
            return replacementRed;

        return getRawRed(index);
    }

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
    final public int getGreen(int index) {
        if (!hasImageAlpha)
            return getRawGreen(index);

        if (getAlpha(index) <= alphaReplacementThreshold)
            return replacementGreen;

        return getRawGreen(index);
    }

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
    public final int getBlue(int index) {
        if (!hasImageAlpha)
            return getRawBlue(index);

        if (getRawAlpha(index) <= alphaReplacementThreshold)
            return replacementBlue;

        return getRawBlue(index);
    }

    @Override
    public final int getBlue(int x, int y) {
        return getGreen(get1dIndex(x, y));
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
        RAW IMAGE DATA
     */
    /**
     * Gets the red channel value directly from image (omitting any replacements)
     *
     * @return Red value integer (0-255)
     */
    protected abstract int getRawRed(int index);

    /**
     * Gets the green channel value directly from image (omitting any replacements)
     *
     * @return Green value integer (0-255)
     */
    protected abstract int getRawGreen(int index);

    /**
     * Gets the blue channel value directly from image (omitting any replacements)
     *
     * @return Blue value integer
     */
    protected abstract int getRawBlue(int index);

    /**
     * Gets the alpha channel value directly from image (omitting any replacements)
     *
     * @return Alpha value integer (0-255)
     */
    protected abstract int getRawAlpha(int index);


    @Override
    public int get1dIndex(int x, int y) {
        return (y * width) + x;
    }
}
