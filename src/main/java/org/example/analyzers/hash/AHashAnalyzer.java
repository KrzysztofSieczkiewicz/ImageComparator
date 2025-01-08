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


    /**
     * Computes aHash representing provided image.
     * Hashing is performed in steps:
     * 1. Convert image to greyscale </p>
     * 2. Calculate average pixel intensity </p>
     * 3. Iterate through all pixels of the resized image. If value exceeds average, set hash bit to 1 </p>
     *
     * @param image to hash
     * @return BitSet containing image hash
     */
    public BitSet aHash(BufferedImage image) {
        BufferedImage resized = ImageUtil.resize(image, reducedImageSize, reducedImageSize);
        BufferedImage greyscaled = ImageUtil.greyscale(resized);

        ImageAccessor accessor = ImageAccessor.create(greyscaled);

        int[] values = accessor.getBlueArray();
        int length = values.length;
        int averageValue = calculateAverage(values);

        BitSet hash = new BitSet(length);
        for (int i = 0; i < length; i++) {
            hash.set(i, values[i] >= averageValue);
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
