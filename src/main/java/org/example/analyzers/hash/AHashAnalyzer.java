package org.example.analyzers.hash;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class AHashAnalyzer {
    private int size = 32;

    public void aHash(BufferedImage image) {
        BufferedImage resized = ImageUtil.resize(image, size, size);
        BufferedImage greyscaled = ImageUtil.greyscale(resized);

        ImageAccessor accessor = ImageAccessor.create(greyscaled);

        int[][] pixelValues = accessor.getPixels();
    }


    public int averagePixelValues() {
        
    }

}
