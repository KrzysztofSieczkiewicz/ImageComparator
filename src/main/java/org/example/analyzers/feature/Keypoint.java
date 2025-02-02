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

    public boolean subpixelRefinement() {

        double[][] regularizedHessianMatrix = MatrixUtil.diagonalRegularization(hessianMatrix);
        double[] offsets = MatrixUtil.getMatrixSolution(regularizedHessianMatrix, VectorUtil.multiplyVector(gradientsVector, -1.0) );

        this.subPixelX = pixelX + offsets[0];
        this.subPixelY = pixelY + offsets[1];

        double offsetMagnitude = VectorUtil.getVectorNorm(offsets);
        if (offsetMagnitude > 0.55) {
            //System.out.print("Magnitude too large: " + offsetMagnitude + "\n");
            return false;
        }

        double contrast = VectorUtil.getVectorDotProduct(offsets);
        if (Math.abs(contrast) < 0.02) {
            //System.out.print("Contrast too small: " + Math.abs(contrast) + "\n");
            return false;
        }

        return true;
    }

    // TODO: finish this
    private void computeOrientation() {
        int[] histogramm = new int[36];

        double magnitude = Math.sqrt( gradientsVector[0]*gradientsVector[0] + gradientsVector[1]*gradientsVector[1]);
        double direction = Math.atan2(gradientsVector[0], gradientsVector[1]) * 100 / Math.PI;

        if (direction < 0) direction += 360;
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
