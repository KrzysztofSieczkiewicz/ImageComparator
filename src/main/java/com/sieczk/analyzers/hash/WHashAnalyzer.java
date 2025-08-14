package com.sieczk.analyzers.hash;

import com.sieczk.utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class WHashAnalyzer {

    private final double hashSizeCoefficient;

    public WHashAnalyzer(double hashSizeCoefficient) {
        this.hashSizeCoefficient = hashSizeCoefficient;
    }

    /**
     * Computes wHash representing provided image.
     * Hashing is performed in steps:
     * 1. Convert image to greyscale </p>
     * 2. Perform Haar wavelet transformation </p>
     * 3. Calculate average values for wavelet coefficients </p>
     * 4. Iterate through comparison matrix and set hash bytes if the coefficient exceeds mean
     *
     * @param image to hash
     * @return BitSet containing image hash
     */
    public BitSet wHash(BufferedImage image) {
        int[][] imageMatrix = ImageUtil.extractLuminosityMatrix(image);
        performHaarWaveletTransform(imageMatrix);

        int mean = calculateCoefficientsMean(imageMatrix);

        return calculateHash(imageMatrix, mean);
    }

    private void performHaarWaveletTransform(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        while (rows > 1 || cols > 1) {
            if (cols > 1) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols / 2; j++) {
                        int sum = matrix[i][2 * j] + matrix[i][2 * j + 1];
                        int diff = matrix[i][2 * j] - matrix[i][2 * j + 1];
                        matrix[i][j] = sum / 2;
                        matrix[i][cols / 2 + j] = diff / 2;
                    }
                }
                cols /= 2;
            }

            if (rows > 1) {
                for (int j = 0; j < cols; j++) {
                    for (int i = 0; i < rows / 2; i++) {
                        int sum = matrix[2 * i][j] + matrix[2 * i + 1][j];
                        int diff = matrix[2 * i][j] - matrix[2 * i + 1][j];
                        matrix[i][j] = sum / 2;
                        matrix[rows / 2 + i][j] = diff / 2;
                    }
                }
                rows /= 2;
            }
        }
    }

    private int calculateCoefficientsMean(int[][] matrix) {
        double sum = 0;
        int count = 0;

        int rows = matrix.length / 2;
        int cols = matrix[0].length / 2;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum += matrix[i][j];
                count++;
            }
        }

        return (int) (sum / count);
    }

    private BitSet calculateHash(int[][] matrix, int mean) {
        int hashRows = (int) (matrix.length * hashSizeCoefficient);
        int hashCols = (int) (matrix[0].length * hashSizeCoefficient);

        BitSet hash = new BitSet(hashRows * hashCols);

        int bitIndex = 0;
        for (int i = 0; i < hashRows; i++) {
            for (int j = 0; j < hashCols; j++) {
                if (matrix[i][j] > mean) {
                    hash.set(bitIndex);
                }
                bitIndex++;
            }
        }

        return hash;
    }
}