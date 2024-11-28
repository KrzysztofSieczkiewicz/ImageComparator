package org.example.utils;

import org.example.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class PHashUtil {
    private final int size = 32;
    private final int reducedSize = 8;



    /**
     * Calculate 64 bit long pHash
     */
    public long getHash(BufferedImage img) {

        img = ImageUtil.resize(img, size, size);
        img = ImageUtil.greyscale(img);

        ImageAccessor imageAccessor = ImageAccessor.create(img);

        // TODO: TEST IF BLUE VALUES REQUIRE NORMALIZATION AND CONVERSION TO DOUBLE
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

    private double[][] generateFrequencyDomain(int[][] imageValues) {

        // Initialize normalization coeffs
        double[] c = new double[size];
        for (int i = 1; i < size; i++) {
            c[i] = 1;
        }
        c[0] = 1 / Math.sqrt(2.0);

        // Precompute cosine UV terms
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

        // Calc DCT
        double[][] F = new double[size][size];
        for (int u = 0; u < size; u++) {
            for (int v = 0; v < size; v++) {
                double sum = 0.0;

                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        sum += cosTermsU[u][i] * cosTermsV[v][j] * imageValues[i][j];
                    }
                }
                sum *= ((c[u] * c[v]) / 4.0);
                F[u][v] = sum;
            }
        }
        return F;
    }
}
