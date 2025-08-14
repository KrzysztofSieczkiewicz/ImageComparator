package com.sieczk.utils.accessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Basic accessor for formats where image colors are saved in bytes:<p>
 * TYPE_4BYTE_ABGR,<p>
 * TYPE_4BYTE_BGR,<p>
 */
public class ImageAccessorByte extends ImageAccessorImpl {

    private final byte[] imageDataBytes;
    private final int bytesPerColor;

    public ImageAccessorByte(BufferedImage bufferedImage) {
        super(
                bufferedImage.getWidth(),
                bufferedImage.getHeight() );

        switch (bufferedImage.getType()) {
            case BufferedImage.TYPE_3BYTE_BGR -> {
                alphaPositionOffset = 0;
                hasAlpha = false;
                bytesPerColor = 3;
            }
            case BufferedImage.TYPE_4BYTE_ABGR -> {
                alphaPositionOffset = 1;
                hasAlpha = true;
                bytesPerColor = 4;
            }
            default -> throw new IllegalArgumentException("Unsupported image type: " + bufferedImage.getType());
        }

        this.imageDataBytes = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    }

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
     * Returns 2D array of RGB values from an entire image.
     * Differs from default implementation by including number of bytes used for each color
     * accommodating for AGBR and GBR
     *
     * @return a 2D array of the ARGB integers of the entire image
     */
    @Override
    public int[][] getPixels() {
        int[][] pixels = new int[width][height];

        int x = 0;
        int y = 0;
        for (int i = 0; i < imageDataBytes.length; i = i + bytesPerColor) {
            pixels[x][y] = getPixel(i);
            if (++x == width) {
                x = 0;
                y++;
            }
        }
        return pixels;
    }

    @Override
    public int[] getPixelsArray() {
        int[] pixels = new int[width * height];

        int index = 0;
        for (int i = 0; i < imageDataBytes.length; i = i + bytesPerColor) {
            pixels[index++] = getPixel(i);
        }
        return pixels;
    }

    @Override
    public int getAlpha(int index) {
        if (!hasAlpha) return 255;
        return imageDataBytes[index] & 0xFF;
    }

    @Override
    public int[][] getAlphaMatrix() {
        int[][] imageAlpha = new int[width][height];

        if (!hasAlpha) return imageAlpha;

        int x = 0;
        int y = 0;
        for (int i = 0; i < imageDataBytes.length; i += bytesPerColor) {
            imageAlpha[x][y] = getAlpha(i);// (imageData[i] & 0xFF);
            if (++x == width) {
                x = 0;
                y++;
            }
        }
        return imageAlpha;
    }

    @Override
    public void setAlpha(int index, int alpha) {
        if (!hasAlpha) return;
        imageDataBytes[index] = (byte) (alpha);
    }

    @Override
    public int getRed(int index) {
        return imageDataBytes[index + alphaPositionOffset + 2] & 0xFF;
    }

    @Override
    public void setRed(int index, int red) {
        imageDataBytes[index + alphaPositionOffset + 2] = (byte) (red);
    }

    @Override
    public int getGreen(int index) {
        return imageDataBytes[index + alphaPositionOffset + 1] & 0xFF;
    }

    @Override
    public void setGreen(int index, int green) {
        imageDataBytes[index + alphaPositionOffset + 1] = (byte) (green);
    }

    @Override
    public int getBlue(int index) {
        return imageDataBytes[index + alphaPositionOffset] & 0xFF;
    }

    @Override
    public void setBlue(int index, int blue) {
        imageDataBytes[index + alphaPositionOffset] = (byte) (blue);
    }

    /**
     * Maps the XY coordinates to the 1D array index by including number of bytes used for each color
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @return 1D array index
     */
    @Override
    public int get1dIndex(int x, int y) {
        return (y * bytesPerColor * width) + (x * bytesPerColor);
    }
}
