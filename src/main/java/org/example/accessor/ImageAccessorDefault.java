package org.example.accessor;

import java.awt.image.BufferedImage;

/**
 * Default fallback accessor for images with alpha channel<p>
 * Accesses image data from image RGB matrix instead of utilizing buffer.
 * Should handle any BufferedImage type
 */
public class ImageAccessorDefault extends ImageAccessorImpl {

    private static final int FULL = 0xFFFFFFFF;
    private static final int ALPHA_MASK = 255 << 24;
    private static final int ALPHA_MASK_INVERTED = FULL & ~ALPHA_MASK;

    private static final int RED_MASK = 255 << 16;
    private static final int RED_MASK_INVERTED = FULL & ~RED_MASK;

    private static final int GREEN_MASK = 255 << 8;
    private static final int GREEN_MASK_INVERTED = FULL & ~GREEN_MASK;

    private static final int BLUE_MASK = 255;
    private static final int BLUE_MASK_INVERTED = FULL & ~BLUE_MASK;

    private final int[] imageRGBData;

    public ImageAccessorDefault(BufferedImage bufferedImage) {
        // Set underlying image properties
        super(bufferedImage.getWidth(),
              bufferedImage.getHeight() );

        // Read image RGB data from the BufferedImage
        imageRGBData = bufferedImage.getRGB(0,0, width, height, null, 0, width);
    }

    @Override
    public int getAlpha(int index) {
        return (imageRGBData[index] & ALPHA_MASK) >>> 24;
    }

    @Override
    public void setAlpha(int index, int alpha) {
        int pixel = getPixel(index)
                & ALPHA_MASK_INVERTED
                | (alpha << 24);

        imageRGBData[index] = pixel;
    }

    @Override
    public int getRed(int index) {
        return (imageRGBData[index] & RED_MASK) >>> 16;
    }

    @Override
    public void setRed(int index, int red) {
        int pixel = getPixel(index)
                & RED_MASK_INVERTED
                | (red << 24);

        imageRGBData[index] = pixel;
    }

    @Override
    public int getGreen(int index) {
        return (imageRGBData[index] & GREEN_MASK) >>> 8;
    }

    @Override
    public void setGreen(int index, int green) {
        int pixel = getPixel(index)
                & GREEN_MASK_INVERTED
                | (green << 24);

        imageRGBData[index] = pixel;
    }

    @Override
    public int getBlue(int index) {
        return (imageRGBData[index] & BLUE_MASK);
    }

    @Override
    public void setBlue(int index, int blue) {
        int pixel = getPixel(index)
                & BLUE_MASK_INVERTED
                | (blue << 24);

        imageRGBData[index] = pixel;
    }
}