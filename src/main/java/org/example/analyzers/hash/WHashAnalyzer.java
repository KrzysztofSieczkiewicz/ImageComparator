package org.example.analyzers.hash;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class WHashAnalyzer {


    public WHashAnalyzer() {
    }


    public BitSet wHash(BufferedImage image) {
        BufferedImage greyscaled = ImageUtil.greyscale(image);

        ImageAccessor accessor = ImageAccessor.create(greyscaled);

        int[][] imageMatrix = accessor.getBlueMatrix();
        int[][] transformedMatrix = calculateHaarWaveletTransform(imageMatrix);

        int mean = calculateCoefficientsMean(transformedMatrix);

        BitSet hash = new BitSet();
        int bitIndex = 0;
        for (int[] row : transformedMatrix) {
            for (int coeff : row) {
                if (coeff > mean) hash.set(bitIndex);
                bitIndex++;
            }
        }

        return hash;
    }

    /**
     * Performs Haar wavelet transformation on provided matrix.
     *
     * @param matrix containing pixel color data
     * @return new matrix containing haar transformed data
     */
    private int[][] calculateHaarWaveletTransform(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] transformedMatrix = new int[rows][cols];

        while (rows > 1 || cols > 1) {
            if (cols > 1) {
                for (int i = 0; i < rows; i++) {
                    int halfColLen = cols / 2;
                    for (int j = 0; j < halfColLen; j++) {
                        transformedMatrix[i][j] = (matrix[i][2 * j] + matrix[i][2 * j + 1]) / 2;
                        transformedMatrix[i][halfColLen + j] = (matrix[i][2 * j] - matrix[i][2 * j + 1]) / 2;
                    }
                }
                cols /= 2;
            }

            if (rows > 1) {
                for (int j = 0; j < cols; j++) {
                    int halfRowLen = rows / 2;
                    for (int i = 0; i < halfRowLen; i++) {
                        transformedMatrix[i][j] = (matrix[2 * i][j] + matrix[2 * i + 1][j]) / 2;
                        transformedMatrix[halfRowLen + i][j] = (matrix[2 * i][j] - matrix[2 * i + 1][j]) / 2;
                    }
                }
                rows /= 2;
            }
        }

        return transformedMatrix;
    }

    private int calculateCoefficientsMean(int[][] matrix) {
        double sum = 0;
        int count = 0;
        for (int[] row : matrix) {
            for (int value : row) {
                sum += value;
                count++;
            }
        }

        return (int) (sum / count);
    }


}