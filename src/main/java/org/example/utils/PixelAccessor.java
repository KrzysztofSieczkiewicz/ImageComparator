package org.example.utils;

import java.awt.image.BufferedImage;

public interface PixelAccessor {

    static PixelAccessor create(BufferedImage bufferedImage) {

        switch (bufferedImage.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR,
                 BufferedImage.TYPE_4BYTE_ABGR,
                 BufferedImage.TYPE_4BYTE_ABGR_PRE -> {
                return new PixelByteAccessor(bufferedImage);
            }
            case BufferedImage.TYPE_INT_BGR,
                 BufferedImage.TYPE_INT_RGB,
                 BufferedImage.TYPE_INT_ARGB,
                 BufferedImage.TYPE_INT_ARGB_PRE -> {
                return new PixelIntAccessor(bufferedImage);
            }
            default -> {
                return new PixelDefaultAccessor(bufferedImage);
            }
        }
    }

    /*
        ARGB
     */

    /**
     * Gets ARGB integer of the pixel at specified 1D array index
     *
     * @return ARGB values stored in the single integer <p>
     * Alpha in the bits (24-31)<p>
     * Red in the bits (16-23)<p>
     * Green in the bits (8-15)<p>
     * Blue in the bits (0-7)
     */
    int getARGB(int index);

    /**
     * Gets ARGB integer of the pixel at specified 2D coordinates.
     * Uses TYPE_INT_ARGB color model
     *
     * @param x the X coordinate of the pixel in the image 2D array
     * @param y the Y coordinate of the pixel in the image 2D array
     * @return ARGB values (int32) using default int32 ARGB model
     */
    int getARGB(int x, int y);

    /**
     * Returns 2D array of RGB values from an entire image. Uses TYPE_INT_ARGB color model
     *
     * @return a 2D array of the ARGB integers of the entire image
     */
    int[][] getARGB();


    /*
        ALPHA CHANNEL
     */
    /**
     * Check if opacity replacement threshold is set to the value equal or larger than 0.
     * Setting this threshold is resolved by  calling {@link #setAlphaReplacement(int, int, int, int, int)}
     */
    boolean isReplaceAlphaSet();

    /**
     * Sets alpha replacement threshold and values for replacement color
     * @param alphaThreshold alpha value above which replacement should occur
     * @param red red component of replacement color
     * @param green green component of replacement color
     * @param blue blue component of replacement color
     * @param alpha alpha component of replacement color
     */
    void setAlphaReplacement(int alphaThreshold, int red, int green, int blue, int alpha);

    /**
     * Gets an alpha value from the pixel at specified 1D array index
     *
     * @return Alpha value stored in the integer
     */
    int getAlpha(int index);

    /**
     * Gets Alpha value of the pixel at specified 2D coordinates.
     *
     * @param x the X coordinate of the pixel in the image 2D array
     * @param y the Y coordinate of the pixel in the image 2D array
     * @return Alpha value (int32)
     */
    int getAlpha(int x, int y);

    /**
     * Gets the alpha channel of the entire image mapped to a 2d array representing image pixel coordinates
     *
     * @return Alpha values (or null if Alpha is not supported in the image format)
     */
    int[][] getAlpha();


    /*
        RED CHANNEL
     */
    /**
     * Get the red value at the specified offset
     *
     * @return the red value (int32) of the pixel
     */
    int getRed(int index);

    /**
     * Get the red value of the specified pixel
     *
     * @param x the X coordinate of the pixel in the image 2D array
     * @param y the Y coordinate of the pixel in the image 2D array
     * @return the red value (int32) of the pixel
     */
    int getRed(int x, int y);

    /**
     * Gets the red channel of the entire image mapped to a 2d array representing image pixel coordinates
     *
     * @return Red values (int32) array of the pixel
     */
    int[][] getRed();


    /*
        GREEN CHANNEL
     */
    /**
     * Get the green value at the specified offset
     *
     * @return the green value (int32) of the pixel
     */
    int getGreen(int index);

    /**
     * Get the green value of the specified pixel
     *
     * @param x the X coordinate of the pixel in the image 2D array
     * @param y the Y coordinate of the pixel in the image 2D array
     * @return the green value (int32) of the pixel
     */
    int getGreen(int x, int y);

    /**
     * Gets the green channel of the entire image mapped to a 2d array representing image pixel coordinates
     *
     * @return Green values (int32) array of the pixel
     */
    int[][] getGreen();


    /*
        BLUE CHANNEL
     */
    /**
     * Get the blue value at the specified offset
     *
     * @return the blue value (int32) of the pixel
     */
    int getBlue(int index);

    /**
     * Get the blue value of the specified pixel
     *
     * @param x the X coordinate of the pixel in the image 2D array
     * @param y the Y coordinate of the pixel in the image 2D array
     * @return the blue value (int32) of the pixel
     */
    int getBlue(int x, int y);

    /**
     * Gets the blue channel of the entire image mapped to a 2d array representing image pixel coordinates
     *
     * @return Blue values (int32) array of the pixel
     */
    int[][] getBlue();


    /*
        OTHERS
     */

    /**
     * Maps the XY coordinates to the 1D array index
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return 1D array index
     */
    int get1dIndex(int x, int y);

}
