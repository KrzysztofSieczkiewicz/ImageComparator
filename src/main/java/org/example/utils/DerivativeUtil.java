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

    // TODO: do dokończenia - na razie nie ma prawa działać
    public static float[] approximateGradientVector(float[][][] octaveSlice, int scale, int x, int y) {
        float dx = (octaveSlice[1][x+1][y] - octaveSlice[1][x-1][y]) / 2f;
        float dy = (octaveSlice[1][x][y+1] - octaveSlice[1][x][y-1]) / 2f;
        float ds = (octaveSlice[2][x][y] - octaveSlice[0][x][y]) / 2f;

        return new float[] {dx, dy, ds};
    }

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

    // TODO: do dokończenia
    //  hesjany są "bezpieczne", ale pochodna skali już nie - korzysta z oryginalnego tensora
    //  rozważ czy te metody nie wymagają rozdzielenia
    //  ale zastanów się też jak "zabezpieczyć" pochodną skali - nie ma co jej pozostawiać bez niczego
    //  najlepiej zaznacz już w metodach wyżej, że float[][][] ma zawsze pierwszy wymiar równy 3
    //  nie sprawdzamy przecież skali dalej niż bezpośredni sąsiad - może utworzyć klasę OctaveSlice trzymającą prevScale, currentScale, nextScale?
    //  to rozwiązuje kwestię out of bound dla skal, a xy jest zabezpieczone przez SafeMatrixSlice.
    //  a Keypoint i Keypoint candidate nie musiałyby trzymać ani neighbourMatrix ani OctaveSlice, jedynie wyniki działań z tej klasy

    public static float[][] approximateScaleDerivatives(float[][][] tensor, int scale, int x, int y) {
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

}
