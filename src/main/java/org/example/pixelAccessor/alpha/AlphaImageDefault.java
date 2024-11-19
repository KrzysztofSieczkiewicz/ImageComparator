package org.example.pixelAccessor.alpha;

import java.awt.image.BufferedImage;

/**
 * Default fallback accessor for images with alpha channel<p>
 * Accesses image data from image RGB matrix instead of utilizing buffer.
 * Should handle any BufferedImage type, but is noticeably slower than dedicated accessors
 */
public class AlphaImageDefault extends AlphaImageAccessorImpl {

    private static final int ALPHA_MASK = 255 << 24;
    private static final int RED_MASK = 255 << 16;
    private static final int GREEN_MASK = 255 << 8;
    private static final int BLUE_MASK = 255;

    private final int[] imageRGBData;

    public AlphaImageDefault(BufferedImage bufferedImage) {
        // Set underlying image properties
        super(bufferedImage.getWidth(),
              bufferedImage.getHeight() );

        // Read image RGB data from the BufferedImage
        imageRGBData = bufferedImage.getRGB(0,0, width, height, null, 0, width);
    }

    @Override
    protected int getRawAlpha(int index) {
        return (imageRGBData[index] & ALPHA_MASK) >>> 24;
    }

    @Override
    protected int getRawRed(int index) {
        return (imageRGBData[index] & RED_MASK) >>> 16;
    }

    @Override
    protected int getRawGreen(int index) {
        return (imageRGBData[index] & GREEN_MASK) >>> 8;
    }

    @Override
    protected int getRawBlue(int index) {
        return (imageRGBData[index] & BLUE_MASK);
    }
}
