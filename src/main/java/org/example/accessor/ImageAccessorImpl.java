package org.example.accessor;

public abstract class ImageAccessorImpl implements ImageAccessor {

    // Image dimensions
    protected final int width;
    protected final int height;

    // Alpha
    protected static final int FULL_ALPHA = 255 << 24;
    protected boolean hasAlpha = false;
    protected int alphaPositionOffset;


    public ImageAccessorImpl(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /*
        ARGB
     */
    /**
     * Returns RGB values for a single pixel based on index in a 1D array representing the image pixels.
     *
     * @return a ARGB integer of the selected pixel
     */
    @Override
    public int getPixel(int index) {
        return (hasAlpha ? getAlpha(index) << 24 : FULL_ALPHA)
                | getRed(index) << 16
                | (getGreen(index) << 8)
                | (getBlue(index));
    }

    /**
     * Returns RGB values for a single pixel based on pixel coordinates.
     *
     * @return a ARGB integer of the selected pixel
     */
    @Override
    public int getPixel(int x, int y) {
        return getPixel(get1dIndex(x, y));
    }

    /**
     * Returns RGB values for an entire image.
     *
     * @return 2D array of ARGB integers for the entire image
     */
    @Override
    public int[][] getPixels() {
        int[][] pixels = new int[width][height];

        int x = 0;
        int y = 0;
        for (int i = 0; i < width * height; i++) {
            pixels[x][y] = getPixel(i);
            x++;
            if (x == width) {
                x = 0;
                y++;
            }
        }
        return pixels;
    }

    /**
     * Overwrites pixel data under 1D array index
     * @param index image 1D array index
     * @param a alpha value
     * @param r red value
     * @param g green value
     * @param b blue value
     */
    @Override
    public void setPixel(int index, int a, int r, int g, int b) {
        setAlpha(index, a);
        setRed(index, r);
        setGreen(index, g);
        setBlue(index, b);
    }

    /**
     * Overwrites pixel data under image (X,Y) coordinates
     * @param x X coordinate of the pixel
     * @param y Y coordinate of the pixel
     * @param a alpha value
     * @param r red value
     * @param g green value
     * @param b blue value
     */
    @Override
    public void setPixel(int x, int y, int a, int r, int g, int b) {
        int index = get1dIndex(x, y);
        setAlpha(index, a);
        setRed(index, r);
        setGreen(index, g);
        setBlue(index, b);
    }


    /*
        ALPHA CHANNEL
     */
    @Override
    public int getAlpha(int x, int y) {
        return getAlpha(get1dIndex(x, y));
    }

    @Override
    public int[][] getAlpha() {
        int[][] imageAlpha = new int[width][height];

        if (!hasAlpha) return imageAlpha;

        for (int i=0; i<width*height; i++) {
            int x = i % width;
            int y = i / width;
            imageAlpha[x][y] = getAlpha(x,y);
        }
        return imageAlpha;
    }

    @Override
    public void setAlpha(int x, int y, int alpha) {
        setAlpha(get1dIndex(x, y), alpha);
    }


    /*
        RED CHANNEL
     */
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

    @Override
    public void setRed(int x, int y, int red) {
        setRed(get1dIndex(x, y), red);
    }


    /*
        GREEN CHANNEL
     */
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

    @Override
    public void setGreen(int x, int y, int green) {
        setGreen(get1dIndex(x, y), green);
    }


    /*
        BLUE CHANNEL
     */
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

    @Override
    public void setBlue(int x, int y, int blue) {
        setBlue(get1dIndex(x, y), blue);
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
