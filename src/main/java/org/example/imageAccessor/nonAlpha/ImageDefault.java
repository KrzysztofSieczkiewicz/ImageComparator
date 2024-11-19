package org.example.imageAccessor.nonAlpha;

import java.awt.image.BufferedImage;

/**
 * Default fallback accessor for images without alpha channel<p>
 * Accesses image data from image RGB matrix instead of buffer.
 * Should handle any BufferedImage type, but is noticeably slower than dedicated accessors
 */
public class ImageDefault extends ImageAccessorImpl {

    private final int[] imageRGBData;

    public ImageDefault(BufferedImage bufferedImage) {
        // Set underlying image properties
        super(bufferedImage.getWidth(),
              bufferedImage.getHeight() );

        // Read image RGB data from the BufferedImage
        imageRGBData = bufferedImage.getRGB(0,0, width, height, null, 0, width);
    }

    @Override
    public int getRed(int index) {
        return (imageRGBData[index]) >>> 16;
    }

    @Override
    public int getGreen(int index) {
        return (imageRGBData[index]) >>> 8;
    }

    @Override
    public int getBlue(int index) {
        return (imageRGBData[index]);
    }
}
