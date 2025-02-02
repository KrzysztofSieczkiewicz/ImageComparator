package org.example.utils;

// TODO: FIX MATRIX INDICES:
//  is x for width or y
public class MatrixUtil {

    public static double[][] transpose(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] transposed = new double[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }

        return transposed;
    }

    public static double[][] multiplyMatrix(double[][] matrix, double value) {
        int rowsA = matrix.length;
        int colsA = matrix[0].length;

        double[][] result = new double[rowsA][colsA];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                result[i][j] += matrix[i][j] * value;
            }
        }

        return result;
    }

    public static double[][] multiplyMatrices(double[][] matrixA, double[][] matrixB) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        double[][] result = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return result;
    }

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

    /**
     * Solves square matrix using vector with in-place LU decomposition (Doolittle's method).
     */
    public static double[] getMatrixSolution(double[][] matrix, double[] vector) {
        int size = matrix.length;
        double[][] LU = new double[size][size];

        for (int x=0; x<size; x++) { // clone the matrix
                LU[x] = matrix[x].clone();
        }

        for (int k = 0; k < size; k++) { // Perform LU decomposition
            for (int i = k + 1; i < size; i++) {
                LU[i][k] = LU[i][k] / LU[k][k];
                for (int j = k + 1; j < size; j++) {
                    LU[i][j] -= LU[i][k] * LU[k][j];
                }
            }
        }

        double[] intermediateSolution = new double[size];
        for (int i = 0; i < size; i++) { // solve lower matrix with forward substitution
            intermediateSolution[i] = vector[i];
            for (int j = 0; j < i; j++) {
                intermediateSolution[i] -= LU[i][j] * intermediateSolution[j];
            }
        }

        double[] finalSolution = new double[size];
        for (int i = size - 1; i >= 0; i--) { // solve upper matrix with backward substitution
            finalSolution[i] = intermediateSolution[i];
            for (int j = i + 1; j < size; j++) {
                finalSolution[i] -= LU[i][j] * finalSolution[j];
            }
            finalSolution[i] /= LU[i][i];
        }

        return finalSolution;
    }

    /**
     * Regularizes the matrix with default lambda = 0.001 (Diagonal).
     * Works only on square matrices.
     *
     * @return new modified matrix
     */
    public static double[][] diagonalRegularization(double[][] matrix) {
        double lambda = 0.001;

        return diagonalRegularization(matrix, lambda);
    }

    /**
     * Regularizes the matrix by adding lambda to diagonal values (Diagonal).
     * Works only on square matrices.
     *
     * @return new modified matrix
     */
    public static double[][] diagonalRegularization(double[][] matrix, double lambda) {
        int n = matrix.length;
        int m = matrix[0].length;

        if (n != m) throw new IllegalArgumentException("Regularized matrix must be square");

        double[][] regularizedMatrix = new double[n][n];
        for (int i=0; i<n; i++) {
            for (int j=0; j<m; j++) {
                regularizedMatrix[i][j] = matrix[i][j];
                if (i == j) regularizedMatrix[i][j] += lambda;
            }
        }
        return regularizedMatrix;
    }

    /**
     * Regularizes the matrix (Tikhonov).
     * Works only on square matrices.
     *
     * @return new modified matrix
     */
    public static double[][] performTikhonovRegularization(double[][] matrix, double lambda) {
        int m = matrix.length;
        int n = matrix[0].length;
        double[][] regularizedMatrix;

        if (m >= n) {
            regularizedMatrix = multiplyMatrices(transpose(matrix), matrix);
        } else {
            regularizedMatrix = multiplyMatrices(matrix, transpose(matrix));
        }

        for (int i = 0; i < Math.min(m, n); i++) {
            regularizedMatrix[i][i] += lambda;
        }

        return regularizedMatrix;
    }
}
