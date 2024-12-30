package org.example.utils;

import java.util.Arrays;
import java.util.BitSet;

public class HashUtil {

    /**
     * Converts image pixels into Frequency Domain (discrete cosine space). Can be expensive to calculate.
     * It's advised to resize() images beforehand
     *
     * @param pixels matrix representing image pixels (pref. greyscale)
     * @return matrix of doubles containing different frequency values - will be of identical size as matrix provided
     */
    public static double[][] generateFrequencyDomain(int[][] pixels) {
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

    /**
     * Calculates Hamming Distance - number of differing bits between two Hashes (XOR)
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
     * Calculates similarity between images based on hammingDistance and compared bits amount
     *
     * @param hammingDistance hamming distance for compared hashes
     * @return normalized difference
     */
    public static double calculateSimilarity(int hammingDistance, int reducedImageSize) {
        return 1.0 - ((double) hammingDistance / (reducedImageSize*reducedImageSize));
    }

}