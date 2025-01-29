package org.example.analyzers.feature;

import org.apache.commons.math3.linear.*;

import java.util.Arrays;

public class Keypoint {

    /**
     * Contrast threshold below which keypoint will be discarded during subpixel refinement
     */
    private double contrastThreshold = 0.04;

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

    public Keypoint(KeypointCandidate candidate) {
        this.pixelX = candidate.getX();
        this.pixelY = candidate.getY();
    }

    public boolean subpixelRefinement() {
        double lambda = 1e-6;

        System.out.print("Refinement: ");

        RealVector gradient = new ArrayRealVector(gradientsVector);
        RealMatrix hessian = new Array2DRowRealMatrix(hessianMatrix);
        RealMatrix regularizedHessian = hessian.add(MatrixUtils.createRealIdentityMatrix(hessian.getRowDimension()).scalarMultiply(lambda));

        DecompositionSolver solver = new LUDecomposition(regularizedHessian).getSolver();

        try {
            RealVector offset = solver.solve(gradient.mapMultiply(-1.0));
            this.subPixelX = pixelX + offset.getEntry(0);
            this.subPixelY = pixelY + offset.getEntry(1);

            double offsetMagnitude = offset.getNorm();
            if (offsetMagnitude > 0.55) {
                System.out.print("Magnitude too large: " + offsetMagnitude + "\n");
                return false;
            }

            double contrast = gradient.dotProduct(offset);
            if (Math.abs(contrast) < contrastThreshold) {
                System.out.print("Contrast too small: " + Math.abs(contrast) + "\n");
                return false;
            }
            return true;

        } catch (SingularMatrixException e) {
            return false;
        }
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
