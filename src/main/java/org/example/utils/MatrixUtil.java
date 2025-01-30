package org.example.utils;

public class MatrixUtil {

    /**
     * Creates a slice of matrix with given radius. Uses reflected values if requested pixels are out of bounds of provided matrix.
     * Ensures that matrix will have odd dimensions
     *
     * @param matrix original values to be copied
     * @param x X coordinate of the matrix value around which slice will be created
     * @param y Y coordinate of the matrix value around which slice will be created
     * @param radius value copying distance
     * @return new matrix containing copied values
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
