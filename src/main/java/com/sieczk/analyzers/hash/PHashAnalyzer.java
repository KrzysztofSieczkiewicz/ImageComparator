package com.sieczk.analyzers.hash;

import com.sieczk.utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class PHashAnalyzer {

    /**
     * Computes pHash representing provided image
     *
     * @param image to hash
     * @return BitSet containing image hash
     */
    public BitSet pHash(BufferedImage image) {
        // 1: Resize image to a fixed size
        int size = 32;
        BufferedImage resized = ImageUtil.resizeBilinear(image, size, size);
        int[][] values = ImageUtil.extractLuminosityMatrix(resized);

        // 2: Calculate Discrete Cosine Transform
        double[][] freqDomainValues = generateFrequencyDomain(values);

        // 3: Take the top-left 8x8 block
        int blockSize = 8;
        double[][] dctBlock = new double[blockSize][blockSize];
        for (int i = 0; i < blockSize; i++) {
            System.arraycopy(freqDomainValues[i], 0, dctBlock[i], 0, blockSize);
        }

        // 4: Calculate the average of the block
        double total = 0.0;
        int count = 0;
        for (int x = 0; x < blockSize; x++) {
            for (int y = 0; y < blockSize; y++) {
                if (x != 0 || y != 0) {
                    total += dctBlock[x][y];
                    count++;
                }
            }
        }
        double average = total / count;

        // 5: Generate the hash based on the average
        BitSet bits = new BitSet(count);
        int bitIndex = 0;
        for (int x = 0; x < blockSize; x++) {
            for (int y = 0; y < blockSize; y++) {
                if (x != 0 || y != 0) {
                    bits.set(bitIndex++, dctBlock[x][y] > average);
                }
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

        double[][] frequencies = new double[size][size];

        for (int u = 0; u < size; u++) {
            for (int v = 0; v < size; v++) {
                double sum = 0.0;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        double cosTermU = Math.cos(((2 * i + 1) * u * Math.PI) / (2.0 * size));
                        double cosTermV = Math.cos(((2 * j + 1) * v * Math.PI) / (2.0 * size));
                        sum += pixels[i][j] * cosTermU * cosTermV;
                    }
                }
                double cu = (u == 0) ? 1.0 / Math.sqrt(size) : Math.sqrt(2.0 / size);
                double cv = (v == 0) ? 1.0 / Math.sqrt(size) : Math.sqrt(2.0 / size);
                frequencies[u][v] = cu * cv * sum;
            }
        }
        return frequencies;
    }

    private BufferedImage enforceImageSquareDimensions(BufferedImage image) {
        if(image.getWidth() == image.getHeight()) return image;

        if (image.getWidth() > image.getHeight())
            return ImageUtil.resizeBilinear(image, image.getWidth(), image.getWidth());
        return ImageUtil.resizeBilinear(image, image.getHeight(), image.getHeight());
    }
}