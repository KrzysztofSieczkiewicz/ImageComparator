package org.example.analyzers.hash;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class AHashAnalyzer {
    private final int reducedImageSize;


    public AHashAnalyzer(int reducedImageSize) {
        this.reducedImageSize = reducedImageSize;
    }


    public BitSet aHash(BufferedImage image) {
        BufferedImage resized = ImageUtil.resize(image, reducedImageSize, reducedImageSize);
        BufferedImage greyscaled = ImageUtil.greyscale(resized);

        ImageAccessor accessor = ImageAccessor.create(greyscaled);

        int[] values = accessor.getPixelsArray();
        int averageValue = calculateAverage(values);

        BitSet hash = new BitSet();
        for (int i = 0; i < values.length; i++) {
            hash.set(i, values[i] <= averageValue);
        }

        return hash;
    }

    private int calculateAverage(int[] array) {
        int sum = 0;
        for (int value : array) {
            sum += value;
        }
        return sum / array.length;
    }

}
