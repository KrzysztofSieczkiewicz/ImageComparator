package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class KeypointCandidate {
    private int x,y;
    private double[][] hessianMatrix;
    private final double[] eigenvalues;

    public KeypointCandidate(BufferedImage image, int x, int y) {
        this.hessianMatrix = approxHessianMatrix(image, x, y);

        double trace = hessianMatrix[0][0] + hessianMatrix[1][1];
        double determinant = (hessianMatrix[0][0] * hessianMatrix[1][1]) - (hessianMatrix[0][1] * hessianMatrix[1][0]);
        double discriminant = Math.pow(trace, 2) - 4 * determinant;

        this.x = x;
        this.y = y;
        this.eigenvalues = calculateEigenvalues(trace, discriminant);
    }

    public KeypointCandidate(BufferedImage image, PixelPoint point) {
        this(image, point.getX(), point.getY());
    }


    public boolean isLowContrast(double contrastThreshold) {
        return eigenvalues[0] * eigenvalues[1] < contrastThreshold;
    }

    public boolean isEdgeResponse(double ratioThreshold) {
        return eigenvalues[0] / eigenvalues[1] < ratioThreshold;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double[][] getHessianMatrix() {
        return hessianMatrix;
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
        ImageAccessor prevAccessor = ImageAccessor.create(octave[scaleIndex-1]);
        ImageAccessor currentAccessor = ImageAccessor.create(octave[scaleIndex]);
        ImageAccessor nextAccessor = ImageAccessor.create(octave[scaleIndex+1]);

        double dxx = hessianMatrix[0][0];
        double dyy = hessianMatrix[1][1];
        double dxy = hessianMatrix[0][1];

        double dss = nextAccessor.getBlue(x,y) - 2 * currentAccessor.getBlue(x,y) + prevAccessor.getBlue(x,y);
        double dxs = (nextAccessor.getBlue(x+1,y) - nextAccessor.getBlue(x-1,y)) - (prevAccessor.getBlue(x+1,y) - prevAccessor.getBlue(x-1,y));
        double dys = (nextAccessor.getBlue(x,y+1) - nextAccessor.getBlue(x,y-1)) - (prevAccessor.getBlue(x,y+1) - prevAccessor.getBlue(x,y-1));

        double[][] hessian = {
                { dxx, dxy,  dxs},
                { dxy, dyy,  dys},
                { dxs, dys,  dss}
        };

        return new Keypoint(0f, 0f, hessian);
    }

    /**
     * Calculates hessian matrix eigenvalues
     * @return array {lambda1, lambda2}
     */
    private double[] calculateEigenvalues(double trace, double discriminant) {
        if (discriminant < 0) {
            return new double[]{-1, -1};
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double lambda1 = (trace + sqrtDiscriminant) / 2.0;
        double lambda2 = (trace - sqrtDiscriminant) / 2.0;

        return new double[]{lambda1, lambda2};
    }

    /**
     * Generates Hessian matrix for a single pixel using second order Sobel operators
     */
    private double[][] approxHessianMatrix(BufferedImage image, int x, int y) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] matrix = new double[2][2];

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        ImageAccessor imageAccessor = ImageAccessor.create(image);
        ImageAccessor resultAccessor = ImageAccessor.create(resultImage);

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
                { 0, -1,  0},
                {-1,  4, -1},
                { 0, -1,  0}
        };


        int dxx = 0, dyy = 0, dxy = 0;
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

        int hessianValue = (int) Math.sqrt(dxx * dxx + dyy * dyy + 2 * dxy * dxy);
        hessianValue = Math.min(255, Math.max(0, hessianValue));
        resultAccessor.setOpaquePixel(x, y, hessianValue, hessianValue, hessianValue);


        return matrix;
    }

    private int[][][] getNeighboursMatrix(BufferedImage[] octave,int scaleIndex, int x, int y) {
        int matrixSize = 3;
        int[][][] neighbours = new int[matrixSize][matrixSize][matrixSize];
        for (int i=0; i<matrixSize; i++) {
            for (int j=0; j<matrixSize; j++) {
                for (int k=0; k<matrixSize; k++) {
                    ImageAccessor accessor = ImageAccessor.create(octave[scaleIndex+k-1]);
                    neighbours[i][j][k] = accessor.getBlue(x+i-1,y+j-1);
                }
            }
        }

        return neighbours;
    }
}