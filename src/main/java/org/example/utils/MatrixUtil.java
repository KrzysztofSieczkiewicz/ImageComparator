package org.example.utils;

public class MatrixUtil {

    public static float accessElementWithReflection(float[][] matrix, int maxX, int maxY, int x, int y) {
        int reflectedX = x < 0 ? -x : (x >= maxX ? 2*maxX-x-1 : x);
        int reflectedY = y < 0 ? -y : (y >= maxY ? 2*maxY-y-1 : y);

        return matrix[reflectedX][reflectedY];
    }


    public static float accessElementWithReflection(float[][] matrix, int x, int y) {
        int matrixWidth = matrix.length;
        int matrixHeight = matrix[0].length;

        return accessElementWithReflection(matrix, matrixWidth, matrixHeight, x, y);
    }

    public static float[][] getSafeMatrixSlice(float[][] matrix, int x, int y, int radius) {
        int maxX = matrix.length;
        int maxY = matrix[0].length;

        float[][] slice = new float[2*radius+1][2*radius+1];

        for (int currX=x-radius; currX<x+radius; currX++) {
            for (int currY=y-radius; currY<y+radius; currY++) {
                int safeX = currX < 0 ? -currX : (currX >= maxX ? 2*maxX-currX-1 : currX);
                int safeY = currY < 0 ? -currY : (currY >= maxY ? 2*maxY-currY-1 : currY);

                slice[currX - (x - radius)][currY - (y - radius)] = matrix[safeX][safeY];
            }
        }

        return slice;
    }

}
