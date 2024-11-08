package org.example.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class PixelIntAccessor extends PixelAccessorImpl {

    private final int[] imageDataInt;

    private int maskRed;
    private int maskGreen;
    private int maskBlue;
    private int maskAlpha;

    private final int offsetRed;
    private final int offsetGreen;
    private final int offsetBlue;
    private final int offsetAlpha;

    public PixelIntAccessor(BufferedImage bufferedImage) {
        // Set underlying image properties
        super(
                bufferedImage.getWidth(),
                bufferedImage.getHeight() );

        // Read image data
        DataBufferInt dataBufferInt = (DataBufferInt) bufferedImage.getRaster().getDataBuffer();
        this.imageDataInt = dataBufferInt.getData();

        // Set masks and color offsets
        switch (bufferedImage.getType()) {
            case BufferedImage.TYPE_INT_ARGB,
                 BufferedImage.TYPE_INT_ARGB_PRE -> {
                hasImageAlpha = true;
                maskAlpha   = 0xff000000;
                maskRed     = 0x00ff0000;
                maskGreen   = 0x0000ff00;
                maskBlue    = 0x000000ff;
            }
            case BufferedImage.TYPE_INT_RGB -> {
                hasImageAlpha = false;
                maskRed     = 0x00ff0000;
                maskGreen   = 0x0000ff00;
                maskBlue    = 0x000000ff;
                maskAlpha   = 0x00000000;
            }
            case BufferedImage.TYPE_INT_BGR -> {
                hasImageAlpha = false;
                maskBlue    = 0x00ff0000;
                maskGreen   = 0x0000ff00;
                maskRed     = 0x000000ff;
                maskAlpha   = 0x00000000;
            }
        }
        offsetAlpha = Util.findFirstSetBitIndex(maskAlpha);
        offsetRed   = Util.findFirstSetBitIndex(maskRed);
        offsetGreen = Util.findFirstSetBitIndex(maskGreen);
        offsetBlue  = Util.findFirstSetBitIndex(maskBlue);
    }

    @Override
    protected int getRawRed(int index) {
        return (imageDataInt[index] & maskRed) >>> offsetRed;
    }

    @Override
    protected int getRawGreen(int index) {
        return (imageDataInt[index] & maskGreen) >>> offsetGreen;
    }

    @Override
    protected int getRawBlue(int index) {
        return (imageDataInt[index] & maskBlue) >>> offsetBlue;
    }

    @Override
    protected int getRawAlpha(int index) {
        if (!hasImageAlpha) return -1;

        return (imageDataInt[index] & maskAlpha) >>> offsetAlpha;
    }
}
