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


    private static int[][] sobel3x3Dxx = {
            { 1, -2,  1},
            { 2, -4,  2},
            { 1, -2,  1}
    };

    private static int[][] sobel5x5Dxx = {
            { 1,  0, -2,  0,  1},
            { 2,  0, -4,  0,  2},
            { 4,  0, -8,  0,  4},
            { 2,  0, -4,  0,  2},
            { 1,  0, -2,  0,  1}
    };

    private static int[][] sobel3x3Dyy = {
            { 1,  2,  1},
            {-2, -4, -2},
            { 1,  2,  1}
    };

    private static int[][] sobel5x5Dyy = {
            { 1,  2,  4,  2,  1},
            { 0,  0,  0,  0,  0},
            {-2, -4, -8, -4, -2},
            { 0,  0,  0,  0,  0},
            { 1,  2,  4,  2,  1}
    };

    private static int[][] sobel3x3Dxy = {
            { 1,  0, -1},
            { 0,  0,  0},
            {-1,  0,  1}
    };

    private static int[][] sobel5x5Dxy = {
            { 1,  2,  0, -2, -1},
            { 2,  4,  0, -4, -2},
            { 0,  0,  0,  0,  0},
            {-2, -4,  0,  4,  2},
            {-1, -2,  0,  2,  1}
    };

    /**
     * Approximates first order derivatives around (X,Y) point using 3x3 sobel kernel
     *
     * @param imageData float[][] matrix with image data
     * @param x pixel width coordinate
     * @param y pixel height coordinate
     * @return array containing derivatives {dx, dy}
     */
    public static float[] approximateGradientVector(float[][] imageData, int x, int y) {

        int sobelRadius = (sobelDx.length - 1) / 2;

        int maxWidth = imageData.length;
        int maxHeight = imageData[0].length;

        float dx=0, dy=0;
        int safeX, safeY;

        for (int i = -sobelRadius; i <= sobelRadius; i++) {
            for (int j = -sobelRadius; j <= sobelRadius; j++) {
                safeX = MatrixUtil.safeReflectCoordinate(x + i, maxWidth);
                safeY = MatrixUtil.safeReflectCoordinate(y + j, maxHeight);
                float pixel = imageData[safeX][safeY];
                dx += pixel * sobelDx[i + sobelRadius][j + sobelRadius];
                dy += pixel * sobelDy[i + sobelRadius][j + sobelRadius];
            }
        }

        return new float[] {dx, dy};
    }

    /**
     * Approximates first order derivatives using 3x3 sobel kernel for space and central difference for scale
     *
     * @param previousScale image data from the previous scale (scale-1)
     * @param currentScale main image data (from the current scale)
     * @param nextScale  image data from the next scale (scale+1)
     * @param x pixel width coordinate
     * @param y pixel height coordinate
     * @return array containing derivatives {dx, dy, ds}
     */
    public static float[] approximateGradientVector(float[][] previousScale, float[][] currentScale, float[][] nextScale, int x, int y) {
        float[] spaceGradients = approximateGradientVector(currentScale, x, y);
        float ds = (nextScale[x][y] - previousScale[x][y]) / 2f;

        return new float[] {spaceGradients[0], spaceGradients[1], ds};
    }

    /**
     * Approximates space derivatives (XY) of the image using 3x3 sobel kernels.
     *
     * @param imageData image data
     * @param x pixel width coordinate
     * @param y pixel height coordinate
     * @return array of derivatives {dxx, dxy, dyy}
     */
    public static float[] approximateSpaceDerivatives3x3(float[][] imageData, int x, int y) {
        return approximateSpaceDerivatives(
                imageData, x, y,
                sobel3x3Dxx, sobel3x3Dyy, sobel3x3Dxy
        );
    }

    /**
     * Approximates space derivatives (XY) of the image using 5x5 sobel kernels.
     *
     * @param imageData image data
     * @param x pixel width coordinate
     * @param y pixel height coordinate
     * @return array of derivatives {dxx, dxy, dyy}
     */
    public static float[] approximateSpaceDerivatives5x5(float[][] imageData, int x, int y) {
        return approximateSpaceDerivatives(
          imageData, x, y,
          sobel5x5Dxx, sobel5x5Dyy, sobel5x5Dxy
        );
    }

    /**
     * Internal method. Approximates space derivatives (XY) of the image using provided kernels.
     * @return array of derivatives {dxx, dxy, dyy}
     */
    private static float[] approximateSpaceDerivatives(
            float[][] imageData, int x, int y,
            int[][] sobelDxx, int[][] sobelDyy, int[][] sobelDxy) {

        int maxWidth = imageData.length;
        int maxHeight = imageData[0].length;
        int kernelRadius = (sobelDxx.length - 1) / 2;

        float dxx = 0, dyy = 0, dxy = 0;
        for (int dx = -kernelRadius; dx <= kernelRadius; dx++) {
            for (int dy = -kernelRadius; dy <= kernelRadius; dy++) {
                int currX = MatrixUtil.safeReflectCoordinate(x + dx, maxWidth);
                int currY = MatrixUtil.safeReflectCoordinate(y + dy, maxHeight);

                float intensity = imageData[currX][currY];

                dxx += intensity * sobelDxx[dx + kernelRadius][dy + kernelRadius];
                dyy += intensity * sobelDyy[dx + kernelRadius][dy + kernelRadius];
                dxy += intensity * sobelDxy[dx + kernelRadius][dy + kernelRadius];
            }
        }

        return new float[] {dxx, dxy, dyy};
    }

    /**
     * Approximates scale derivatives for an octave slice
     *
     * @param previousScale image data from the previous scale (scale-1)
     * @param currentScale main image data (from the current scale)
     * @param nextScale  image data from the next scale (scale+1)
     * @param x pixel width coordinate
     * @param y pixel height coordinate
     * @return array {dss, dxs, dys} containing derivatives
     */
    public static float[] approximateScaleDerivatives(float[][] previousScale, float[][] currentScale, float[][] nextScale, int x, int y) {
        int range = 1;

        int width = currentScale.length;
        int height = currentScale[0].length;

        int xNext = MatrixUtil.safeReflectCoordinate(x + range, width);
        int xPrev = MatrixUtil.safeReflectCoordinate(x - range, width);
        int yNext = MatrixUtil.safeReflectCoordinate(y + range, height);
        int yPrev = MatrixUtil.safeReflectCoordinate(y - range, height);

        float dss = nextScale[x][y] - 2 * currentScale[x][y] + previousScale[x][y];
        float dxs = ((nextScale[xNext][y] - nextScale[xPrev][y]) - (previousScale[xNext][y] - previousScale[xPrev][y])) / 2f;
        float dys = ((nextScale[x][yNext] - nextScale[x][yPrev]) - (previousScale[x][yNext] - previousScale[x][yPrev])) / 2f;

        return new float[] { dss, dxs, dys };
    }

}
