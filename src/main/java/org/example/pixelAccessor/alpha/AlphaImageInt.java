package org.example.pixelAccessor.alpha;

import org.example.utils.Util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Basic accessor for formats where image colors are saved in integers:<p>
 * TYPE_INT_ARGB,<p>
 * TYPE_INT_ARGB_PRE,<p>
 */
public class AlphaImageInt extends AlphaImageAccessorImpl {

    private final int[] imageDataInt;

    private final int maskRed;
    private final int maskGreen;
    private final int maskBlue;
    private final int maskAlpha;

    private final int offsetRed;
    private final int offsetGreen;
    private final int offsetBlue;
    private final int offsetAlpha;

    public AlphaImageInt(BufferedImage bufferedImage) {
        // Set underlying image properties
        super(
                bufferedImage.getWidth(),
                bufferedImage.getHeight() );

        // Read image data
        DataBufferInt dataBufferInt = (DataBufferInt) bufferedImage.getRaster().getDataBuffer();
        this.imageDataInt = dataBufferInt.getData();

        maskAlpha   = 0xff000000;
        maskRed     = 0x00ff0000;
        maskGreen   = 0x0000ff00;
        maskBlue    = 0x000000ff;

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
        return (imageDataInt[index] & maskAlpha) >>> offsetAlpha;
    }
}
