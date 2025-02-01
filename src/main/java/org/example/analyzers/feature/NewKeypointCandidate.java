package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class NewKeypointCandidate {

    /**
     * Config value. Determines the dimensions of the pixels window of neighbours used for hessians and gradients
     */
    private int windowSize = 3;


    private int x,y;
    private final float[][][] neighbouringMatrix;
    private final float[][] basicHessianMatrix;
    private final double hessianTrace;
    private final double hessianDeterminant;
    private final double hessianDiscriminant;
    private final double[] eigenvalues;

    public NewKeypointCandidate(BufferedImage[] scaleTriplet, int x, int y) {
        this.neighbouringMatrix = getNeighbouringPixels(scaleTriplet, x, y, windowSize);
        this.basicHessianMatrix = approxHessianMatrix(scaleTriplet[1], x, y);

        this.hessianTrace = basicHessianMatrix[0][0] + basicHessianMatrix[1][1];
        this.hessianDeterminant = (basicHessianMatrix[0][0] * basicHessianMatrix[1][1]) - (basicHessianMatrix[0][1] * basicHessianMatrix[1][0]);
        this.hessianDiscriminant = Math.pow(hessianTrace, 2) - 4 * hessianDeterminant;

        this.x = x;
        this.y = y;
        this.eigenvalues = calculateEigenvalues(hessianTrace, hessianDiscriminant);
    }

    public NewKeypointCandidate(BufferedImage[] scaleTriplet, PixelPoint point) {
        this(scaleTriplet, point.getX(), point.getY());
    }

    public NewKeypointCandidate(float[][][] octaveSlice, PixelPoint point) {
        this.x = point.getX();
        this.y = point.getY();
        this.neighbouringMatrix = extractPixelWindow3D(octaveSlice, x, y, 9);
        this.basicHessianMatrix = approxHessianMatrix(octaveSlice, x, y);

        this.hessianTrace = basicHessianMatrix[0][0] + basicHessianMatrix[1][1];
        this.hessianDeterminant = (basicHessianMatrix[0][0] * basicHessianMatrix[1][1]) - (basicHessianMatrix[0][1] * basicHessianMatrix[1][0]);
        this.hessianDiscriminant = Math.pow(hessianTrace, 2) - 4 * hessianDeterminant;

        this.eigenvalues = calculateEigenvalues(hessianTrace, hessianDiscriminant);
    }


    public boolean isLowContrast(double contrastThreshold) {
        return eigenvalues[0] * eigenvalues[1] < contrastThreshold;
    }

    public boolean isEdgeResponse(double ratioThreshold) {
        double r = hessianTrace*hessianTrace/hessianDeterminant;
        double edgeThreshold = Math.pow(ratioThreshold + 1, 2) / ratioThreshold;

        return r > edgeThreshold;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    /**
     * Refines candidate to a full keypoint on a given image
     */
    // TODO: I should not have to provide the entire octave to the pixel candidate.
    //  maybe during candidate initialization it could save the neighbouring values as int[x][y][s] so it wouldn't access
    //  other data and wouldn't need passing octave any more? It's a temporary class anyway?
    //  or does it make this class cumbersome - is there a need to store additional data in some temporary class?
    //  anyway - find a way to skip passing octave and scaleIndex -> this method is a Candidate transforming itself into a Keypoint
    public Keypoint refineCandidate(BufferedImage[] octave, int scaleIndex) {
        // approx 1st order derivatives with central difference approximation
        double dx = (neighbouringMatrix[1][x+1][y] - neighbouringMatrix[1][x-1][y]) / 2.0;
        double dy = (neighbouringMatrix[1][x][y+1] - neighbouringMatrix[1][x][y-1]) / 2.0;
        double ds = (neighbouringMatrix[2][x][y] - neighbouringMatrix[0][x][y]) / 2.0;

        ImageAccessor prevAccessor = ImageAccessor.create(octave[scaleIndex-1]);
        ImageAccessor currentAccessor = ImageAccessor.create(octave[scaleIndex]);
        ImageAccessor nextAccessor = ImageAccessor.create(octave[scaleIndex+1]);

        // reuse 2nd order space derivatives
        double dxx = basicHessianMatrix[0][0];
        double dyy = basicHessianMatrix[1][1];
        double dxy = basicHessianMatrix[0][1];

        // approx 2nd order derivatives with second-order central difference
        double dss = nextAccessor.getBlue(x,y) - 2 * currentAccessor.getBlue(x,y) + prevAccessor.getBlue(x,y);
        double dxs = (nextAccessor.getBlue(x+1,y) - nextAccessor.getBlue(x-1,y)) - (prevAccessor.getBlue(x+1,y) - prevAccessor.getBlue(x-1,y));
        double dys = (nextAccessor.getBlue(x,y+1) - nextAccessor.getBlue(x,y-1)) - (prevAccessor.getBlue(x,y+1) - prevAccessor.getBlue(x,y-1));

        double[] gradientVector = { dx, dy, ds };

        double[][] hessianMatrix = {
                { dxx, dxy,  dxs},
                { dxy, dyy,  dys},
                { dxs, dys,  dss}
        };

        return new Keypoint(0, 0, gradientVector, hessianMatrix);
    }

    // TODO: change this method so it performs calculations across the entire neighbouring matrix
    public Keypoint refineCandidate() {
        int localX = 1;
        int localY = 1;
        // approx 1st order derivatives with central difference approximation
        double dx = (neighbouringMatrix[1][localX+1][localY] - neighbouringMatrix[1][localX-1][localY]) / 2.0;
        double dy = (neighbouringMatrix[1][localX][localY+1] - neighbouringMatrix[1][localX][localY-1]) / 2.0;
        double ds = (neighbouringMatrix[2][localX][localY] - neighbouringMatrix[0][localX][localY]) / 2.0;

        // reuse 2nd order space derivatives
        double dxx = basicHessianMatrix[0][0];
        double dyy = basicHessianMatrix[1][1];
        double dxy = basicHessianMatrix[0][1];

        // approx 2nd order derivatives with second-order central difference
        double dss = neighbouringMatrix[2][localX][localY] - 2 * neighbouringMatrix[1][localX][localY] + neighbouringMatrix[0][localX][localY];
        double dxs = ((neighbouringMatrix[2][localX+1][localY] - neighbouringMatrix[2][localX-1][localY]) - (neighbouringMatrix[0][localX+1][localY] - neighbouringMatrix[0][localX-1][localY])) / 2.0;
        double dys = ((neighbouringMatrix[2][localX][localY+1] - neighbouringMatrix[2][localX][localY-1]) - (neighbouringMatrix[0][localX][localY+1] - neighbouringMatrix[0][localX][localY-1])) / 2.0;

        double[] gradientVector = { dx, dy, ds };

        double[][] hessianMatrix = {
                { dxx, dxy,  dxs},
                { dxy, dyy,  dys},
                { dxs, dys,  dss}
        };

        return new Keypoint(x, y, gradientVector, hessianMatrix);
    }

    /**
     * Calculates hessian matrix eigenvalues
     * @return array {lambda1, lambda2}
     */
    private double[] calculateEigenvalues(double trace, double discriminant) {
        if (discriminant < 0 && Math.abs(discriminant) > 1e-10) {
            return new double[]{Double.NaN, Double.NaN};
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double lambda1 = (trace + sqrtDiscriminant) / 2.0;
        double lambda2 = (trace - sqrtDiscriminant) / 2.0;

        return new double[]{lambda1, lambda2};
    }

    /**
     * Generates Hessian matrix for a single pixel using second order Sobel operators
     */
    // TODO: needs to be finished
    private float[][] approxHessianMatrix(BufferedImage image, int x, int y) {
        ImageAccessor imageAccessor = ImageAccessor.create(image);

        int[][] sobelX2 = {
                { 1, -2,  1},
                { 2, -4,  2},
                { 1, -2,  1}
        };
        int[][] sobelY2 = {
                { 1,  2,  1},
                {-2, -4, -2},
                { 1,  2,  1}
        };
        int[][] sobelXY = {
                { -1, 0, -1},
                {  0, 0,  0},
                {  1, 0, -1}
        };

        float dxx = 0, dyy = 0, dxy = 0;
        for (int dx=-1; dx<=1; dx++) {
            for (int dy=-1; dy<=1; dy++) {
                int pixelX = x + dx;
                int pixelY = y + dy;

                int red = imageAccessor.getRed(pixelX, pixelY);
                int green = imageAccessor.getGreen(pixelX, pixelY);
                int blue = imageAccessor.getBlue(pixelX, pixelY);
                int intensity = (int) (0.2989*red + 0.5870*green + 0.1140*blue);

                dxx += intensity * sobelX2[dx + 1][dy + 1];
                dyy += intensity * sobelY2[dx + 1][dy + 1];
                dxy += intensity * sobelXY[dx + 1][dy + 1];
            }
        }

        return new float[][] {
                {dxx, dxy},
                {dxy, dyy}
        };
    }

    /**
     * Generates Hessian matrix for a single pixel using second order Sobel operators
     */
    private float[][] approxHessianMatrix(float[][][] octaveSlice, int x, int y) {
        float[][] imageData = octaveSlice[octaveSlice.length / 2];

        int maxX = octaveSlice[0].length - 1;
        int maxY = octaveSlice[0][0].length - 1;

        int[][] sobelX2 = {
                { 1, -2,  1},
                { 2, -4,  2},
                { 1, -2,  1}
        };
        int[][] sobelY2 = {
                { 1,  2,  1},
                {-2, -4, -2},
                { 1,  2,  1}
        };
        int[][] sobelXY = {
                { 1, -2,  1},
                {-2,  4, -2},
                { 1, -2,  1}
        };

        float dxx = 0, dyy = 0, dxy = 0;
        for (int dx=-1; dx<=1; dx++) {
            int currentX = x + dx;
            currentX = safeguardPixelPosition(currentX, 0, maxX);

            for (int dy=-1; dy<=1; dy++) {
                int currentY = y + dy;
                currentY = safeguardPixelPosition(currentY, 0, maxY);

                float intensity = imageData[currentX][currentY];

                dxx += intensity * sobelX2[dx + 1][dy + 1];
                dyy += intensity * sobelY2[dx + 1][dy + 1];
                dxy += intensity * sobelXY[dx + 1][dy + 1];
            }
        }

        return new float[][]{
                {dxx, dxy},
                {dxy, dyy}
        };
    }

    // TODO: change to use ImageAccessor[] instead
    private float[][][] getNeighbouringPixels(BufferedImage[] scaleTriplet,int scaleIndex, int x, int y) {
        int matrixSize = 3;
        float[][][] neighbours = new float[matrixSize][matrixSize][matrixSize];
        for (int i=0; i<matrixSize; i++) {
            for (int j=0; j<matrixSize; j++) {
                for (int k=0; k<matrixSize; k++) {
                    ImageAccessor accessor = ImageAccessor.create(scaleTriplet[scaleIndex+k-1]);
                    neighbours[i][j][k] = accessor.getBlue(x+i-1,y+j-1);
                }
            }
        }

        return neighbours;
    }

    private int[][][] getNeighbouringPixels(BufferedImage[] scaleTriplet, int x, int y) {
        int[][][] neighbours = new int[3][3][3];

        for (int ds=0; ds<scaleTriplet.length; ds++) {
            BufferedImage image = scaleTriplet[ds];
            ImageAccessor accessor = ImageAccessor.create(image);
            for (int dx=0; dx<accessor.getWidth(); dx++) {
                for (int dy=0; dy<accessor.getHeight(); dy++) {
                    neighbours[ds][dx][dy] = accessor.getBlue(x+dx-1, y+dy-1);
                }
            }
        }

        return neighbours;
    }

    private int[][][] getNeighbouringPixels(int[][][] scaleTriplet, int x, int y) {
        int matrixDimension = scaleTriplet.length;
        int[][][] neighbours = new int[3][3][3];

        for (int ds=0; ds<matrixDimension; ds++) {
            for (int dx=0; dx<matrixDimension; dx++) {
                for (int dy=0; dy<matrixDimension; dy++) {
                    neighbours[ds][dx][dy] = scaleTriplet[1+ds-1][x+dx-1][y+dy-1];
                }
            }
        }

        return neighbours;
    }

    private float[][][] extractPixelWindow3D(float[][][] octaveSlice, int pixelX, int pixelY, int pixelWindowSize) {
        int scaleDepth = octaveSlice.length;
        int halfWindowSize = pixelWindowSize / 2;

        float[][][] pixels = new float[scaleDepth][pixelWindowSize][pixelWindowSize];

        int maxX = octaveSlice[0].length - 1;
        int maxY = octaveSlice[0][0].length - 1;

        for (int currentScale=0; currentScale<scaleDepth; currentScale++) {
            float[][] scaleLayer = octaveSlice[currentScale];
            float[][] windowLayer = pixels[currentScale];

            for (int dx=-halfWindowSize; dx<=halfWindowSize; dx++) {
                int currentX = pixelX + dx;
                currentX = safeguardPixelPosition(currentX, 0, maxX);

                for (int dy=-halfWindowSize; dy<=halfWindowSize; dy++) {
                    int currentY = pixelY + dy;
                    currentY = safeguardPixelPosition(currentY, 0, maxY);

                    windowLayer[dx+halfWindowSize][dy+halfWindowSize] = scaleLayer[currentX][currentY];
                }
            }
        }

        return pixels;
    }

    private int safeguardPixelPosition(int coordinate, int min, int max) {
        if (coordinate < min) {
            return 2 * min - coordinate;
        } else if (coordinate > max) {
            return 2 * max - coordinate;
        }
        return coordinate;
    }
}