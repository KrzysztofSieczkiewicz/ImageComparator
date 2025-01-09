package org.example.analyzers.hash;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class AHashAnalyzer {

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
        BufferedImage greyscaled = ImageUtil.greyscale(image);
        ImageAccessor accessor = ImageAccessor.create(greyscaled);

        int[] values = accessor.getBlueArray();
        int averageValue = calculateAverage(values);
        int length = values.length;

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
