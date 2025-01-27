package org.example.analyzers.feature;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

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

    public boolean performSubpixelRefinement() {
        RealMatrix hessian = new Array2DRowRealMatrix(hessianMatrix);
        RealVector gradient = new ArrayRealVector(gradientsVector);

        DecompositionSolver solver = new LUDecomposition(hessian).getSolver();
        RealVector offset = solver.solve(gradient.mapMultiply(-1.0));

        this.subPixelX = pixelX + offset.getEntry(0);
        this.subPixelY = pixelY + offset.getEntry(1);

        double offsetMagnitude = offset.getNorm();
        if (offsetMagnitude > 0.5) {
            System.out.println("Offset too large: " + offsetMagnitude);
            return false;
        }

        double contrast = gradient.dotProduct(offset);
        if (Math.abs(contrast) < contrastThreshold) {
            System.out.println("Low contrast at refined position: " + contrast);
            return false;
        }

        return true;
    }

}
