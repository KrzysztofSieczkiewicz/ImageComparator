package org.example.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Basic accessor for formats where image colors are saved in bytes:<p>
 * TYPE_3BYTE_BGR,<p>
 * TYPE_4BYTE_ABGR,<p>
 * Utilizes aws BufferedImage, doesn't support video memory caching
 */
public class PixelByteAccessor extends PixelAccessorImpl {

    private final byte[] imageDataBytes;
    private final int bytesPerColor;

    public PixelByteAccessor(BufferedImage bufferedImage) {
        // Set underlying image properties
        super(
                bufferedImage.getWidth(),
                bufferedImage.getHeight() );

        // Read image data
        DataBufferByte dataBuffByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
        this.imageDataBytes = dataBuffByte.getData();

        // Set accessor settings
        if (bufferedImage.getColorModel().hasAlpha()) {
            // Set properties for TYPE_3BYTE_ABGR
            hasImageAlpha = true;
            alphaPositionOffset = 1;
            bytesPerColor = 4;
        } else {
            // Set properties for TYPE_4BYTE_BGR
            hasImageAlpha = false;
            alphaPositionOffset = 0;
            bytesPerColor = 3;
        }
    }

    /**
     * Returns 2D array of RGB values from an entire image.
     * Differs from default implementation by including number of bytes used for each color
     * Thus accommodating for AGBR and GBR
     *
     * @return a 2D array of the ARGB integers of the entire image
     */
    @Override
    public int[][] getARGB() {
        int[][] rgb = new int[width][height];

        for (int i=0; i< imageDataBytes.length; i+=bytesPerColor) {
            int dividend = i / bytesPerColor;
            int x = dividend % width;
            int y = dividend / width;

            rgb[x][y] = getARGB(i);
        }
        return rgb;
    }

    /**
     * Gets the alpha channel of the entire image mapped to a 2d array representing image pixel coordinates
     * Differs from default implementation by including number of bytes used for each color
     *
     * @return Alpha values (or null if Alpha is not supported in the image format)
     */
    @Override
    public int[][] getAlpha() {
        if (!hasImageAlpha)
            return new int[width][height];

        int[][] imageAlpha = new int[width][height];

        for (int i=0; i<imageDataBytes.length; i+=bytesPerColor) {
            int x = i % width;
            int y = i / width;
            imageAlpha[x][y] = getAlpha(x,y);
        }
        return imageAlpha;
    }

    @Override
    public int getRawRed(int index) {
        return imageDataBytes[index + alphaPositionOffset + 2] & 0xFF;
    }

    @Override
    public int getRawGreen(int index) {
        return imageDataBytes[index + alphaPositionOffset + 1] & 0xFF;
    }

    @Override
    protected int getRawBlue(int index) {
        return imageDataBytes[index + alphaPositionOffset] & 0xFF;
    }

    @Override
    public int getRawAlpha(int index) {
        if (!hasImageAlpha)
            return -1;

        return imageDataBytes[index] & 0xFF;
    }

    /**
     * Maps the XY coordinates to the 1D array index
     * Differs from default implementation by including number of bytes used for each color
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
