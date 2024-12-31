package org.example.analyzers.hash;

import org.example.config.HashComparatorConfig;
import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageUtil;
import org.example.utils.HashUtil;

import java.awt.image.BufferedImage;
import java.util.BitSet;

// TODO: research pHash and add all necessary steps to have this as a complete method
public class PHashAnalyzer {
    private final int size;
    private final int reducedSize;

    public PHashAnalyzer(HashComparatorConfig config) {
        this.size = config.getSize();
        this.reducedSize = config.getReducedSize();
    }

    public void compare(BufferedImage actual, BufferedImage expected) {
        BitSet actualHash = getImageHash(actual, size,size, reducedSize);
        BitSet checkedHash = getImageHash(expected, size,size, reducedSize);

        int hammingDistance = HashUtil.calculateHammingDistance(actualHash, checkedHash);
        double similarity = HashUtil.calculateSimilarity(hammingDistance, reducedSize);
    }

    /**
     * Computes pHash representing provided image. Hash size depends on configuration (reducedSize squared)
     * Hashing is performed in steps:
     * 1. Resize image to smaller size </p>
     * 2. Covert image to greyscale </p>
     * 3. Calculate discrete frequency values for the image </p>
     * 4. Calculate average values for image based on comparison size </p>
     * (the closer comparisonSize is to the imageSize, the more detailed comparison is) </p>
     * 5. Iterate through comparison matrix and set bytes values depending on their value
     * in relation to average that was calculated in step 4. </p>
     *
     * @param img BufferedImage to be hashed
     * @param targetWidth width to which image will be scaled before being hashed
     * @param targetHeight height to which image will be scaled before being hashed
     * @param comparisonSize hash matrix size
     * @return BitSet containing image hash
     */
    private BitSet getImageHash(BufferedImage img, int targetWidth, int targetHeight, int comparisonSize) {
        img = ImageUtil.resize(img, targetWidth, targetHeight);
        img = ImageUtil.greyscale(img);

        ImageAccessor imageAccessor = ImageAccessor.create(img);
        int[][] blueValues = imageAccessor.getBlue();

        double[][] freqDomainValues = HashUtil.generateFrequencyDomain(blueValues);
        double total = 0.0;
        for (int x=0; x<comparisonSize; x++) {
            for (int y=0; y<comparisonSize; y++) {
                total += freqDomainValues[x][y];
            }
        }
        total -= freqDomainValues[0][0];
        double average = total/ (double) (comparisonSize*comparisonSize -1);

        BitSet bits = new BitSet();
        for (int x=0; x<comparisonSize; x++) {
            for (int y=0; y<comparisonSize; y++) {
                bits.set(
                        (y * comparisonSize) + x,
                        freqDomainValues[x][y] > average
                );
            }
        }

        return bits;
    }
}
