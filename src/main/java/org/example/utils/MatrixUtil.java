package org.example.utils;

// TODO: FIX MATRIX INDICES:
//  is x for width or y
public class MatrixUtil {

    /**
     * Safeguards array coordinate against out of boundary issue by reflecting value by the boundary.
     * Doesn't work against trespasses larger than array length
     *
     * @param index index to be checked
     * @param max largest allowed index
     * @return index within 0-max limit
     */
    public static int reflectCoordinate(int index, int max) {
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

    /**
     * Calculates matrix trace, works only for square matrices
     */
    public static float getMatrixTrace(float[][] matrix) {
        int size = matrix.length;

        float trace = 0;
        for (int i=0; i<size; i++) {
            trace += matrix[i][i];
        }

        return trace;
    }

    /**
     * Calculates matrix determinant.
     * Works only with square matrices with max size 2x2
     */
    public static float get2x2MatrixDeterminant(float[][] matrix) {
        int length = matrix.length;

        if (length == 1) {
            return matrix[0][0];
        } else {
            return (matrix[0][0] * matrix[1][1]) - (matrix[0][1] * matrix[1][0]);
        }
    }

    /**
     * Calculates matrix determinant.
     * Works only with square matrices with max size 2x2
     */
    public static float get2x2MatrixDiscriminant(float trace, float determinent) {
        return (float) Math.pow(trace, 2) - 4 * determinent;
    }

    public static float[] get2x2MatrixEigenvalues(float trace, float discriminant) {
        if (discriminant < 0 && Math.abs(discriminant) > 1e-10) {
            return new float[]{Float.NaN, Float.NaN};
        }

        float sqrtDiscriminant = (float) Math.sqrt(discriminant);
        float lambda1 = (trace + sqrtDiscriminant) / 2f;
        float lambda2 = (trace - sqrtDiscriminant) / 2f;

        return new float[]{lambda1, lambda2};
    }

}
