package org.example.analyzers.hash;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class WHashAnalyzer {


    public WHashAnalyzer(int size) {
    }


    public BitSet wHash(BufferedImage image) {
        BufferedImage greyscaled = ImageUtil.greyscale(image);

        ImageAccessor accessor = ImageAccessor.create(greyscaled);

        return new BitSet();
    }

    public void calculateHaarWaveletTransform(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[][] temp = new int[rows][cols];

        while (rows > 1 || cols > 1) {
            if (rows > 1) {
                for (int i = 0; i < cols; i++) {
                    transformRow(matrix, temp, rows, i);
                }
                rows /= 2;
            }

            if (cols > 1) {
                for (int i = 0; i < rows; i++) {
                    transformColumn(matrix, temp, cols, i);
                }
                cols /= 2;
                cols /= 2;
            }
        }

    }

    private void transformRow(int[][] matrix, int[][] temp, int rowLen, int rowIndex) {
        int halfRowLen = rowLen / 2;

        for (int j = 0; j < halfRowLen; j++) {
            temp[rowIndex][j] = (matrix[rowIndex][2 * j] + matrix[rowIndex][2 * j + 1]) / 2; // Approximation
            temp[rowIndex][halfRowLen + j] = (matrix[rowIndex][2 * j] - matrix[rowIndex][2 * j + 1]) / 2; // Detail
        }
    }

    private void transformColumn(int[][] matrix, int[][] temp, int colLen, int colIndex) {
        int halfColLen = colLen / 2;

        for (int i = 0; i < halfColLen; i++) {
            temp[i][colIndex] = (matrix[2 * i][colIndex] + matrix[2 * i + 1][colIndex]) / 2; // Approximation
            temp[halfColLen + i][colIndex] = (matrix[2 * i][colIndex] - matrix[2 * i + 1][colIndex]) / 2; // Detail
        }
    }

}