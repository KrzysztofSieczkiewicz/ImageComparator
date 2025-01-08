package org.example.analyzers.hash;

import org.example.config.HashComparatorConfig;
import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageUtil;
import org.example.utils.HashUtil;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class PHashAnalyzer {
    private final int size;
    private final int reducedSize;

    public PHashAnalyzer(HashComparatorConfig config) {
        this.size = config.getReducedImageSize();
        this.reducedSize = config.getReducedSize();
    }

    /**
     * Computes pHash representing provided image.
     * Hashing is performed in steps:
     * 1. Resize image to smaller size </p>
     * 2. Covert image to greyscale </p>
     * 3. Calculate discrete frequency values for the image </p>
     * 4. Calculate average values for image based on comparison size </p>
     * 5. Iterate through comparison results matrix and set hash bytes if value exceeds calculated mean
     *
     * @param img BufferedImage to be hashed
     * @return BitSet containing image hash
     */
    public BitSet pHash(BufferedImage img) {
        img = ImageUtil.resize(img, size, size);
        img = ImageUtil.greyscale(img);

        ImageAccessor imageAccessor = ImageAccessor.create(img);
        int[][] blueValues = imageAccessor.getBlueMatrix();

        double[][] freqDomainValues = HashUtil.generateFrequencyDomain(blueValues);
        double total = -freqDomainValues[0][0];
        for (int x=0; x<reducedSize; x++) {
            for (int y=0; y<reducedSize; y++) {
                total += freqDomainValues[x][y];
            }
        }
        double average = total / (double) (reducedSize*reducedSize -1);

        BitSet bits = new BitSet();
        for (int x=0; x<reducedSize; x++) {
            for (int y=0; y<reducedSize; y++) {
                bits.set(
                        (y * reducedSize) + x,
                        freqDomainValues[x][y] > average
                );
            }
        }

        return bits;
    }
}
