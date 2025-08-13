package org.example.utils.accessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Works only with formats where colors are saved in integers:<p>
 * TYPE_INT_ARGB,<p>
 * TYPE_INT_ARGB_PRE,<p>
 * TYPE_INT_RGB,<p>
 * TYPE_INT_BGR,<p>
 */
public class ImageAccessorInt extends ImageAccessorImpl {

    private final int[] imageDataInt;


    private final int maskRed;
    private final int maskGreen;
    private final int maskBlue;
    private final int maskAlpha;

    private final int offsetRed;
    private final int offsetGreen;
    private final int offsetBlue;
    private final int offsetAlpha;

    public ImageAccessorInt(BufferedImage bufferedImage) {
        super(
                bufferedImage.getWidth(),
                bufferedImage.getHeight() );

        switch (bufferedImage.getType()) {
            case BufferedImage.TYPE_INT_ARGB -> {
                maskAlpha = 0xff000000;
                maskRed = 0x00ff0000;
                maskGreen = 0x0000ff00;
                maskBlue = 0x000000ff;
                hasAlpha = true;
            }
            case BufferedImage.TYPE_INT_RGB -> {
                maskAlpha = 0x00000000;
                maskRed = 0x00ff0000;
                maskGreen = 0x0000ff00;
                maskBlue = 0x000000ff;
            }
            case BufferedImage.TYPE_INT_BGR -> {
                maskAlpha = 0x00000000;
                maskRed = 0x000000ff;
                maskGreen = 0x0000ff00;
                maskBlue = 0x00ff0000;
            }
            default -> throw new IllegalArgumentException("Unsupported image type: " + bufferedImage.getType());
        }

        this.imageDataInt = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

        offsetAlpha = findFirstSetBitIndex(maskAlpha);
        offsetRed   = findFirstSetBitIndex(maskRed);
        offsetGreen = findFirstSetBitIndex(maskGreen);
        offsetBlue  = findFirstSetBitIndex(maskBlue);
    }

    @Override
    public int getAlpha(int index) {
        return hasAlpha ? (imageDataInt[index] & maskAlpha) >>> offsetAlpha : 255;
    }

    @Override
    public void setAlpha(int index, int alpha) {
        if (!hasAlpha) return;
        imageDataInt[index] |= (alpha << offsetAlpha);
    }

    @Override
    public int getRed(int index) {
        return (imageDataInt[index] & maskRed) >>> offsetRed;
    }

    @Override
    public void setRed(int index, int red) {
        imageDataInt[index] = (imageDataInt[index] & (~maskRed)) | (red << offsetRed);
    }


    @Override
    public int getGreen(int index) {
        return (imageDataInt[index] & maskGreen) >>> offsetGreen;
    }

    @Override
    public void setGreen(int index, int green) {
        imageDataInt[index] = (imageDataInt[index] & (~maskGreen)) | (green << offsetGreen);
    }

    @Override
    public int getBlue(int index) {
        return (imageDataInt[index] & maskBlue) >>> offsetBlue;
    }

    @Override
    public void setBlue(int index, int blue) {
        imageDataInt[index] = (imageDataInt[index] & (~maskBlue)) | (blue << offsetBlue);
    }


    /**
     * Finds the first (least significant) bit that is set to 1
     * @param mask mask to be searched
     * @return index of the least significant set bit
     */
    private int findFirstSetBitIndex(int mask) {
        return (mask == 0) ? -1 : Integer.numberOfTrailingZeros(mask);
    }

}
