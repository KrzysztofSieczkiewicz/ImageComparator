package org.example.utils;

public class MatrixUtil {

    public static int reflectMatrixCoordinate(int index, int max) {
        if (index < 0) return -index;
        if (index >= max) return 2 * (max-1) - index;
        return index;
    }

    /**
     * Creates a slice of matrix with given radius using reflected values if coordinates are out of bounds.
     * Output matrix will always have odd dimensions.
     *
     * @param matrix original values to be copied
     * @param x width coordinate of the input matrix point
     * @param y height coordinate of the input matrix point
     * @param radius max range from xy coordinates
     * @return new matrix float[2*radius+1][2*radius+1].
     */
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
