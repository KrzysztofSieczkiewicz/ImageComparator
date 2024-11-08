package org.example.utils;

import java.awt.image.BufferedImage;

public class PixelDefaultAccessor extends PixelAccessorImpl {

    private final int[] imageRGBData;

    public PixelDefaultAccessor(BufferedImage bufferedImage) {
        // Set underlying image properties
        super(bufferedImage.getWidth(),
              bufferedImage.getHeight() );

        // Check if image has an alpha channel
        hasImageAlpha = bufferedImage
                .getColorModel()
                .hasAlpha();

        // Read image RGB data from the BufferedImage
        imageRGBData = bufferedImage.getRGB(0,0, width, height, null, 0, width);
    }

    @Override
    protected int getRawAlpha(int index) {
        if (!hasImageAlpha) return -1;

        return (imageRGBData[index] & ALPHA_MASK) >>> 24;
    }

    @Override
    protected int getRawRed(int index) {
        return (imageRGBData[index] & ALPHA_MASK) >>> 16;
    }

    @Override
    protected int getRawGreen(int index) {
        return (imageRGBData[index] & ALPHA_MASK) >>> 8;
    }

    @Override
    protected int getRawBlue(int index) {
        return (imageRGBData[index] & ALPHA_MASK);
    }
}
