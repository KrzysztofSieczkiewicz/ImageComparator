package org.example.utils;

import org.example.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class PHashUtil {
    private final int size = 32;
    private final int reducedSize = 8;


    /**
     * Calculate 64 bit long pHash
     */
    public long getImageHash(BufferedImage img) {

        img = ImageUtil.resize(img, size, size);
        img = ImageUtil.greyscale(img);

        ImageAccessor imageAccessor = ImageAccessor.create(img);

        int[][] blueValues = imageAccessor.getBlue();

        double[][] freqDomainValues = generateFrequencyDomain(blueValues);

        // take only low freq dct values (take top left part of the matrix) and calculate average value
        double total = 0.0;
        for (int x=0; x<reducedSize; x++) {
            for (int y=0; y<reducedSize; y++) {
                total += freqDomainValues[x][y];
            }
        }
        total -= freqDomainValues[0][0]; // Skip first val
        double average = total/ (double) (reducedSize*reducedSize -1);

        // reduce frequency domain values set values of hash bits either to 0 or 1 depending on average value
        long hashBits = 0;

        for (int x=0; x<reducedSize; x++) {
            for (int y=0; y<reducedSize; y++) {
                hashBits = (freqDomainValues[x][y] > average ? (hashBits << 1) | 0x01 : (hashBits << 1) & 0xFFFFFFFFFFFFFFFEL);
            }
        }

        return hashBits;
    }

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
}
