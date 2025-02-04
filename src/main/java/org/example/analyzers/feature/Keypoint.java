package org.example.analyzers.feature;

import org.example.utils.MatrixUtil;
import org.example.utils.VectorUtil;

public class Keypoint {

    private int pixelX, pixelY;
    private double subPixelX, subPixelY;
    private double[] gradientsVector;
    private double[][] hessianMatrix;

    public Keypoint(int x, int y, double[] gradientsVector, double[][] hessianMatrix) {
        this.pixelX = x;
        this.pixelY = y;
        this.gradientsVector = gradientsVector;
        this.hessianMatrix = hessianMatrix;
    }

    public boolean subpixelRefinement(float contrastThreshold) {

        double[][] regularizedHessianMatrix = MatrixUtil.diagonalRegularization(hessianMatrix);
        double[] offsets = MatrixUtil.getMatrixSolution(regularizedHessianMatrix, VectorUtil.multiplyVector(gradientsVector, -1.0) );

        this.subPixelX = pixelX + offsets[0];
        this.subPixelY = pixelY + offsets[1];

        double offsetMagnitude = VectorUtil.getVectorNorm(offsets);
        if (offsetMagnitude > 0.55) {
            return false;
        }

        double contrast = VectorUtil.getVectorDotProduct(offsets);

        return Math.abs(contrast) >= contrastThreshold;
    }


    public int computeOrientation() {
        double[] histogram = new double[36];

        double magnitude = Math.sqrt( gradientsVector[0]*gradientsVector[0] + gradientsVector[1]*gradientsVector[1]);

        double direction = Math.atan2(gradientsVector[0], gradientsVector[1]) * 100 / Math.PI;
        if (direction < 0) direction += 360;
        int bin = (int) (direction / 10);

        histogram[bin] += magnitude;

        int maxIndex = 0;
        for (int i=1; i<histogram.length; i++) {
            if (histogram[i] > histogram[maxIndex]) {
                maxIndex = i;
            }
        }

        return maxIndex * 10;
    }

    public int getPixelX() {
        return pixelX;
    }

    public int getPixelY() {
        return pixelY;
    }

    public double getSubPixelX() {
        return subPixelX;
    }

    public double getSubPixelY() {
        return subPixelY;
    }
}
