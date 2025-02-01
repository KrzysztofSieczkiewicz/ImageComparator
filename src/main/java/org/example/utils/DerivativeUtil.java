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

    /**
     * Approximates first order derivatives using sobel kernel for space and central difference for scale
     *
     * @param previousScale image data from the previous scale (scale-1)
     * @param currentScale main image data (from the current scale)
     * @param nextScale  image data from the next scale (scale+1)
     * @param x pixel width coordinate
     * @param y pixel height coordinate
     * @return array containing derivatives {dx, dy, ds}
     */
    public static float[] approximateGradientVector(float[][] previousScale, float[][] currentScale, float[][] nextScale, int x, int y) {
        int range = 1;
        int width = currentScale.length;
        int height = currentScale[0].length;

        float dx=0, dy=0;
        int safeX, safeY;

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                safeX = MatrixUtil.reflectCoordinate(x + i, width);
                safeY = MatrixUtil.reflectCoordinate(y + j, height);
                float pixel = currentScale[safeX][safeY];
                dx += pixel * sobelDx[i + 1][j + 1];
                dy += pixel * sobelDy[i + 1][j + 1];
            }
        }

        float ds = (nextScale[x][y] - previousScale[x][y]) / 2f;

        return new float[] {dx, dy, ds};
    }

    /**
     * Approximates space derivatives (XY) of the image. Uses sobel kernels
     *
     * @param imageData image data
     * @param x pixel width coordinate
     * @param y pixel height coordinate
     * @return array of derivatives {dxx, dxy, dyy}
     */
    public static float[] approximateSpaceDerivatives(float[][] imageData, int x, int y) {
        int kernelRadius = 1;
        float[][] imageSlice = MatrixUtil.getSafeMatrixSlice(imageData, x, y, kernelRadius);

        float dxx = 0, dyy = 0, dxy = 0;
        for (int dx=-kernelRadius; dx<=kernelRadius; dx++) {
            for (int dy=-kernelRadius; dy<=kernelRadius; dy++) {

                float intensity = imageSlice[x + dx][y + dy];

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

        int xNext = MatrixUtil.reflectCoordinate(x + range, width);
        int xPrev = MatrixUtil.reflectCoordinate(x - range, width);
        int yNext = MatrixUtil.reflectCoordinate(y + range, height);
        int yPrev = MatrixUtil.reflectCoordinate(y - range, height);

        float dss = nextScale[x][y] - 2 * currentScale[x][y] + previousScale[x][y];
        float dxs = ((nextScale[xNext][y] - nextScale[xPrev][y]) - (previousScale[xNext][y] - previousScale[xPrev][y])) / 2f;
        float dys = ((nextScale[x][yNext] - nextScale[x][yPrev]) - (previousScale[x][yNext] - previousScale[x][yPrev])) / 2f;

        return new float[] { dss, dxs, dys };
    }

}
