package org.example.utils;

import org.example.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.BitSet;

public class PHashUtil {
    private final int size = 64;
    private static final int reducedSize = 24;


    /**
     * Computes pHash representing provided image. Hash size depends on configuration (reducedSize squared)
     *
     * @param img BufferedImage to be hashed
     * @return BitSet representing Image (A set of bits with value)
     */
    public BitSet getImageHash(BufferedImage img) {
        img = ImageUtil.resize(img, size, size);
        img = ImageUtil.greyscale(img);

        ImageAccessor imageAccessor = ImageAccessor.create(img);
        int[][] blueValues = imageAccessor.getBlue();

        double[][] freqDomainValues = generateFrequencyDomain(blueValues);
        double total = 0.0;
        for (int x=0; x<reducedSize; x++) {
            for (int y=0; y<reducedSize; y++) {
                total += freqDomainValues[x][y];
            }
        }
        total -= freqDomainValues[0][0]; // Skip first val
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

    /**
     * Converts image pixels into Frequency Domain (discrete cosine space). Can be expensive to calculate.
     * It's advised to resize() images beforehand
     *
     * @param pixels matrix representing image pixels (pref. greyscale)
     * @return matrix of doubles containing different frequency values - will be of identical size as matrix provided
     */
    private double[][] generateFrequencyDomain(int[][] pixels) {
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

    /**
     * Calculates Hamming Distance - number of differing bits between two Hashes (XOR operation)
     *
     * @param hash1 hash representing first image
     * @param hash2 hash representing second image
     * @return number of differing bits
     */
    public static int calculateHammingDistance(BitSet hash1, BitSet hash2) {
        BitSet xorResult = (BitSet) hash1.clone();
        xorResult.xor(hash2);
        return xorResult.cardinality();
    }

    /**
     * Calculates normalized similarity between images (between 0 and 1)
     * 
     * @param hammingDistance hamming distance for compared hashes
     * @return normalized difference
     */
    public static double calculateSimilarity(int hammingDistance) {
        return 1.0 - ((double) hammingDistance / (reducedSize*reducedSize));
    }

}