package org.example.pixelAccessor.alpha;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Basic accessor for formats where image colors are saved in bytes:<p>
 * TYPE_4BYTE_ABGR,<p>
 */
public class AlphaImageByte extends AlphaImageAccessorImpl {

    private final byte[] imageDataBytes;
    private final int bytesPerColor = 4;

    public AlphaImageByte(BufferedImage bufferedImage) {
        // Set underlying image properties
        super(
                bufferedImage.getWidth(),
                bufferedImage.getHeight() );

        // Read image data
        DataBufferByte dataBuffByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
        this.imageDataBytes = dataBuffByte.getData();

        // Set properties for TYPE_3BYTE_ABGR
        alphaPositionOffset = 1;
    }

    /**
     * Returns 2D array of RGB values from an entire image.
     * Differs from default implementation by including number of bytes used for each color
     * Thus accommodating for AGBR and GBR
     *
     * @return a 2D array of the ARGB integers of the entire image
     */
    @Override
    public int[][] getPixels() {
        int[][] rgb = new int[width][height];

        for (int i=0; i< imageDataBytes.length; i+=bytesPerColor) {
            int dividend = i / bytesPerColor;
            int x = dividend % width;
            int y = dividend / width;

            rgb[x][y] = getPixel(i);
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
        return imageDataBytes[index] & 0xFF;
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