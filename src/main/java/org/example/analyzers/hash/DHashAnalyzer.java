package org.example.analyzers.hash;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class DHashAnalyzer {
        private int reducedImageSize;

    public DHashAnalyzer(int size) {
        this.reducedImageSize = size;
    }


    /**
     * Computes dHash representing provided image.
     * Hashing is performed in steps:
     * 1. Convert image to greyscale </p>
     * 2. Calculate difference between horizontally neighbouring pixels </p>
     * 3. if difference is > 1 set hash bit to 1. </p>
     *
     * @param image to hash
     * @return BitSet containing image hash
     */
    public BitSet dHash(BufferedImage image) {
        int width = reducedImageSize + 1;
        int height = reducedImageSize;

        BufferedImage resized = ImageUtil.resize(image, width, height);
        BufferedImage greyscaled = ImageUtil.greyscale(resized);

        ImageAccessor accessor = ImageAccessor.create(greyscaled);

        int[] values = accessor.getBlueArray();

        BitSet hash = new BitSet(reducedImageSize * reducedImageSize);
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width - 1; x++) {
                int current = values[y * width + x];
                int next = values[y * width + x + 1];
                hash.set(index, current > next);
                index++;
            }
        }

        return hash;
    }
}
