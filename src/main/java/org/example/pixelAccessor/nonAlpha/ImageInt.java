package org.example.pixelAccessor.nonAlpha;

import org.example.utils.Util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Basic accessor for formats where image colors are saved in integers:<p>
 * TYPE_INT_RGB,<p>
 * TYPE_INT_BGR,<p>
 */
public class ImageInt extends ImageAccessorImpl {

    private final int[] imageDataInt;

    private int maskRed;
    private int maskGreen;
    private int maskBlue;

    private final int offsetRed;
    private final int offsetGreen;
    private final int offsetBlue;

    public ImageInt(BufferedImage bufferedImage) {
        // Set underlying image properties
        super(
                bufferedImage.getWidth(),
                bufferedImage.getHeight() );

        // Read image data
        DataBufferInt dataBufferInt = (DataBufferInt) bufferedImage.getRaster().getDataBuffer();
        this.imageDataInt = dataBufferInt.getData();

        // Set masks and color offsets
        switch (bufferedImage.getType()) {
            case BufferedImage.TYPE_INT_RGB -> {
                maskRed     = 0xff0000;
                maskGreen   = 0x00ff00;
                maskBlue    = 0x0000ff;
            }
            case BufferedImage.TYPE_INT_BGR -> {
                maskBlue    = 0xff0000;
                maskGreen   = 0x00ff00;
                maskRed     = 0x0000ff;
            }
        }
        offsetRed   = Util.findFirstSetBitIndex(maskRed);
        offsetGreen = Util.findFirstSetBitIndex(maskGreen);
        offsetBlue  = Util.findFirstSetBitIndex(maskBlue);
    }

    @Override
    public int getRed(int index) {
        return (imageDataInt[index] & maskRed) >>> offsetRed;
    }

    @Override
    public int getGreen(int index) {
        return (imageDataInt[index] & maskGreen) >>> offsetGreen;
    }

    @Override
    public int getBlue(int index) {
        return (imageDataInt[index] & maskBlue) >>> offsetBlue;
    }
}
