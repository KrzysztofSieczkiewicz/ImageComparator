package org.example.analyzers.hash;

import org.example.config.HashComparatorConfig;
import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.BitSet;

public class PHashAnalyzer {
    private final int reducedSize;

    public PHashAnalyzer(HashComparatorConfig config) {
        this.reducedSize = config.getReducedSize();
    }

    /**
     * Computes pHash representing provided image.
     * Hashing is performed in steps:
     * 1. Convert image to greyscale </p>
     * 2. Calculate discrete frequency values for the image </p>
     * 3. Calculate average values for image based on comparison size </p>
     * 4. Iterate through comparison results matrix and set hash bytes if value exceeds calculated mean
     *
     * @param image to hash
     * @return BitSet containing image hash
     */
    public BitSet pHash(BufferedImage image) {
        BufferedImage greyscaled = ImageUtil.greyscale(image);

        ImageAccessor imageAccessor = ImageAccessor.create(greyscaled);
        int[][] blueValues = imageAccessor.getBlueMatrix();

        double[][] freqDomainValues = generateFrequencyDomain(blueValues);
        double total = -freqDomainValues[0][0];
        for (int x=0; x<reducedSize; x++) {
            for (int y=0; y<reducedSize; y++) {
                total += freqDomainValues[x][y];
            }
        }
        double average = total / (double) (reducedSize*reducedSize -1);

        BitSet bits = new BitSet(reducedSize*reducedSize);
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

    /**
     * Converts image pixels into Frequency Domain (discrete cosine space). Can be expensive to calculate.
     * It's advised to resize() images beforehand
     *
     * @param pixels matrix representing image pixels (pref. greyscale)
     * @return matrix of doubles containing different frequency values - will be of identical size as matrix provided
     */
    private double[][] generateFrequencyDomain(int[][] pixels) {
        int size = pixels.length;

        double[] normalizations = new double[size];

        Arrays.setAll(normalizations, i -> i * 2);
        normalizations[0] = 1 / Math.sqrt(2.0);

        double[][] cosTermsU = new double[size][size];
        double[][] cosTermsV = new double[size][size];
        for (int u = 0; u < size; u++) {
            for (int i = 0; i < size; i++) {
                cosTermsU[u][i] = Math.cos(((2 * i + 1) / (2.0 * size)) * u * Math.PI);
            }
        }
        for (int v = 0; v < size; v++) {
            for (int j = 0; j < size; j++) {
                cosTermsV[v][j] = Math.cos(((2 * j + 1) / (2.0 * size)) * v * Math.PI);
            }
        }

        double[][] frequencies = new double[size][size];
        for (int u = 0; u < size; u++) {
            for (int v = 0; v < size; v++) {
                double freq = 0.0;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        freq += cosTermsU[u][i] * cosTermsV[v][j] * pixels[i][j];
                    }
                }
                freq *= ((normalizations[u] * normalizations[v]) / 4.0);
                frequencies[u][v] = freq;
            }
        }
        return frequencies;
    }
}