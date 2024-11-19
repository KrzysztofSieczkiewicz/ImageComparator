package org.example.imageAccessor;

import org.example.imageAccessor.alpha.AlphaImageByte;
import org.example.imageAccessor.alpha.AlphaImageDefault;
import org.example.imageAccessor.alpha.AlphaImageInt;
import org.example.imageAccessor.nonAlpha.ImageByte;
import org.example.imageAccessor.nonAlpha.ImageInt;

import java.awt.image.BufferedImage;

public interface ImageAccessor {

    static ImageAccessor create(BufferedImage bufferedImage) {

        switch (bufferedImage.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR -> {
                return new ImageByte(bufferedImage);
            }
            case BufferedImage.TYPE_4BYTE_ABGR,
                 BufferedImage.TYPE_4BYTE_ABGR_PRE -> {
                return new AlphaImageByte(bufferedImage);
            }
            case BufferedImage.TYPE_INT_BGR,
                 BufferedImage.TYPE_INT_RGB -> {
                return new ImageInt(bufferedImage);
            }
            case BufferedImage.TYPE_INT_ARGB,
                 BufferedImage.TYPE_INT_ARGB_PRE -> {
                return new AlphaImageInt(bufferedImage);
            }
            default -> {
                return new AlphaImageDefault(bufferedImage);
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
    int getPixel(int index);

    /**
     * Gets ARGB integer of the pixel at specified 2D coordinates.
     * Uses TYPE_INT_ARGB color model
     *
     * @param x the X coordinate of the pixel in the image 2D array
     * @param y the Y coordinate of the pixel in the image 2D array
     * @return ARGB values (int32) using default int32 ARGB model
     */
    int getPixel(int x, int y);

    /**
     * Returns 2D array of RGB values from an entire image. Uses TYPE_INT_ARGB color model
     *
     * @return a 2D array of the ARGB integers of the entire image
     */
    int[][] getPixels();


    /*
        ALPHA CHANNEL
     */
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
}
