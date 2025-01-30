package org.example.utils;

public class DerivativeUtil {

    private static int[][] sobelDx = {
            {-1,  0,  1},
            {-2,  0,  2},
            {-1,  0,  1}
    };

    private static int[][] sobelDy = {
            {-1, -2, -1},
            { 0,  0,  0},
            { 1,  2,  1}
    };

    private static int[][] sobelDxx = {
            { 1, -2,  1},
            { 2, -4,  2},
            { 1, -2,  1}
    };

    private static int[][] sobelDyy = {
            { 1,  2,  1},
            {-2, -4, -2},
            { 1,  2,  1}
    };

    private static int[][] sobelDxy = {
            {-1,  0,  1},
            { 0,  0,  0},
            { 1,  0, -1}
    };

    // TODO: move Hessian and gradient methods here -
    //  create two methods for Hessians, depending on number of parameters provided (x,y) and (x,y,s);
    //  it is not worth the trouble (and to sacrifice accuracy) to reuse 2x2 Hessian

    /**
     *
     */
    public static float[][] approximateHessianMatrix(float[][] matrix, int x, int y) {
        int kernelRadius = 1;
        float[][] slice = MatrixUtil.getSafeMatrixSlice(matrix, x, y, kernelRadius);

        float dxx = 0, dyy = 0, dxy = 0;
        for (int dx=-kernelRadius; dx<=kernelRadius; dx++) {
            for (int dy=-kernelRadius; dy<=kernelRadius; dy++) {

                float intensity = slice[x + dx][y + dy];

                dxx += intensity * sobelDxx[dx + kernelRadius][dy + kernelRadius];
                dyy += intensity * sobelDyy[dx + kernelRadius][dy + kernelRadius];
                dxy += intensity * sobelDxy[dx + kernelRadius][dy + kernelRadius];
            }
        }

        return new float[][]{
                {dxx, dxy},
                {dxy, dyy}
        };
    }

    public static float[][] approximateHessianMatrix(float[][][] tensor, int scale, int x, int y) {
        int kernelRadius = 1;
        float[][] slice = MatrixUtil.getSafeMatrixSlice(tensor[scale], x, y, kernelRadius);

        float dxx = 0, dyy = 0, dxy = 0;
        for (int dx=-kernelRadius; dx<=kernelRadius; dx++) {
            for (int dy=-kernelRadius; dy<=kernelRadius; dy++) {

                float intensity = slice[x + dx][y + dy];

                dxx += intensity * sobelDxx[dx + kernelRadius][dy + kernelRadius];
                dyy += intensity * sobelDyy[dx + kernelRadius][dy + kernelRadius];
                dxy += intensity * sobelDxy[dx + kernelRadius][dy + kernelRadius];
            }
        }

        // Approx scale derivatives by finite difference (central)
        float dss = tensor[2][x][y] - 2 * tensor[1][x][y] + tensor[0][x][y];
        float dxs = ((tensor[2][x+1][y] - tensor[2][x-1][y]) - (tensor[0][x+1][y] - tensor[0][x-1][y])) / 2f;
        float dys = ((tensor[2][x][y+1] - tensor[2][x][y-1]) - (tensor[0][x][y+1] - tensor[0][x][y-1])) / 2f;

        return new float[][] {
                { dxx, dxy,  dxs},
                { dxy, dyy,  dys},
                { dxs, dys,  dss}
        };
    }

    // TODO: instead of accessing matrix with safeguar, maybe it's better to have a method generate safe "window" of the matrix, that runs once before loops?

    public static float[][] approximateScaleDerivatives(float[][][] tensor, int x, int y) {
        int maxS = tensor.length - 1;
        int maxX = tensor[0].length - 1;
        int maxY = tensor[0][0].length - 1;
        int halfS = maxS/2;

        float dxx=0, dyy=0, dxy=0;
        for (int i=-1; i<=1; i++) {
            for (int j=-1; j<=1; j++) {
                float intensity = MatrixUtil.accessElementWithReflection(
                        tensor[halfS],
                        maxX,
                        maxY,
                        (x + i),
                        (y + j) );

                dxx += intensity * sobelDxx[i + 1][j + 1];
                dyy += intensity * sobelDyy[i + 1][j + 1];
                dxy += intensity * sobelDxy[i + 1][j + 1];
            }
        }

        // Approx scale by finite difference (central)
        float dss = tensor[2][x][y] - 2 * tensor[1][x][y] + tensor[0][x][y];
        float dxs = ((tensor[2][x+1][y] - tensor[2][x-1][y]) - (tensor[0][x+1][y] - tensor[0][x-1][y])) / 2f;
        float dys = ((tensor[2][x][y+1] - tensor[2][x][y-1]) - (tensor[0][x][y+1] - tensor[0][x][y-1])) / 2f;

        return new float[][] {
                { dxx, dxy,  dxs},
                { dxy, dyy,  dys},
                { dxs, dys,  dss}
        };
    }
}
