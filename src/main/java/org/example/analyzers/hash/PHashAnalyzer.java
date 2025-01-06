package org.example.analyzers.hash;

import org.example.config.HashComparatorConfig;
import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageUtil;
import org.example.utils.HashUtil;

import java.awt.image.BufferedImage;
import java.util.BitSet;

// TODO: research pHash and add all necessary steps to have this as a complete class
public class PHashAnalyzer {
    private final int size;
    private final int reducedSize;

    public PHashAnalyzer(HashComparatorConfig config) {
        this.size = config.getReducedImageSize();
        this.reducedSize = config.getReducedSize();
    }

    public double compare(BufferedImage actual, BufferedImage expected) {
        BitSet actualHash = pHash(actual);
        BitSet checkedHash = pHash(expected);

        int hammingDistance = HashUtil.calculateHammingDistance(actualHash, checkedHash);
        return HashUtil.calculateSimilarity(hammingDistance, reducedSize);
    }

    /**
     * Computes pHash representing provided image.
     * Hashing is performed in steps:
     * 1. Resize image to smaller size </p>
     * 2. Covert image to greyscale </p>
     * 3. Calculate discrete frequency values for the image </p>
     * 4. Calculate average values for image based on comparison size </p>
     * (the closer comparisonSize is to the imageSize, the more detailed comparison is) </p>
     * 5. Iterate through comparison matrix and set hash bytes if value exceeds calculated mean
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
        double total = 0.0;
        for (int x=0; x<reducedSize; x++) {
            for (int y=0; y<reducedSize; y++) {
                total += freqDomainValues[x][y];
            }
        }
        total -= freqDomainValues[0][0];
        double average = total/ (double) (reducedSize*reducedSize -1);

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
