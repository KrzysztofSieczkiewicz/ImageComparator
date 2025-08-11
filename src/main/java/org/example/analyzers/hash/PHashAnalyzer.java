package org.example.analyzers.hash;

import org.example.utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.BitSet;

public class PHashAnalyzer {

    /**
     * Computes pHash representing provided image.
     * Hashing is performed in steps:
     * 0. Resize image to square dimensions </p>
     * 1. Convert image to greyscale </p>
     * 2. Calculate discrete frequency values for the image </p>
     * 3. Calculate average values for image based on comparison size </p>
     * 4. Iterate through comparison results matrix and set hash bytes if value exceeds calculated mean
     *
     * @param image to hash
     * @return BitSet containing image hash
     */
    public BitSet pHash(BufferedImage image) {
        BufferedImage resized = enforceImageSquareDimensions(image);
        int[][] values = ImageUtil.extractLuminosityMatrix(resized);

        double[][] freqDomainValues = generateFrequencyDomain(values);
        double total = -freqDomainValues[0][0];
        int hashSize = image.getHeight();

        for (int x = 0; x< hashSize; x++) {
            for (int y = 0; y< hashSize; y++) {
                total += freqDomainValues[x][y];
            }
        }
        double average = total / (double) (hashSize * hashSize -1);

        BitSet bits = new BitSet(hashSize * hashSize);
        for (int x = 0; x< hashSize; x++) {
            for (int y = 0; y< hashSize; y++) {
                bits.set(
                        (y * hashSize) + x,
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

    private BufferedImage enforceImageSquareDimensions(BufferedImage image) {
        if(image.getWidth() == image.getHeight()) return image;

        if (image.getWidth() > image.getHeight())
            return ImageUtil.resizeBilinear(image, image.getWidth(), image.getWidth());
        return ImageUtil.resizeBilinear(image, image.getHeight(), image.getHeight());
    }
}