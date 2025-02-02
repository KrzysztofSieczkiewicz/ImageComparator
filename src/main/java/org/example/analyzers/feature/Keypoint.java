package org.example.analyzers.feature;

import org.apache.commons.math3.linear.*;
import org.example.utils.MatrixUtil;
import org.example.utils.VectorUtil;

import java.util.Arrays;

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
        double lambda = 1e-6;

        System.out.print("Refinement: ");

        RealVector gradient = new ArrayRealVector(gradientsVector);
        //gradient = gradient.mapDivide(gradient.getNorm());
        double gradientMagnitude = gradient.getNorm();
        if (gradientMagnitude > 0) {
            gradient = gradient.mapDivide(gradientMagnitude);  // Normalize gradient to unit length
        }

        RealMatrix hessian = new Array2DRowRealMatrix(hessianMatrix);
        RealMatrix regularizedHessian = hessian.add(MatrixUtils.createRealIdentityMatrix(hessian.getRowDimension()).scalarMultiply(lambda));
        double[][] regularizedHessianMatrix = MatrixUtil.diagonalRegularization(hessianMatrix);
        double[][] regularizedHessianMatrixTikhonov = MatrixUtil.performTikhonovRegularization(hessianMatrix, 0.001);

        System.out.println("Auth: " + "diagonal regularized hessian:" + Arrays.deepToString(regularizedHessianMatrix));
        System.out.println("Auth: " + "Tikhonov regularized hessian:" + Arrays.deepToString(regularizedHessianMatrixTikhonov));
        System.out.println("Math3: " + "Regularized hessian:" + Arrays.deepToString(regularizedHessian.getData()));

        DecompositionSolver solver = new LUDecomposition(regularizedHessian).getSolver();

        try {
            double[] offsets = MatrixUtil.getMatrixSolution(regularizedHessianMatrix, VectorUtil.multiplyVector(gradientsVector, -1.0) );
            RealVector offset = solver.solve(gradient.mapMultiply(-1.0));

            System.out.println("Auth: " + "1:" + offsets[0] + ", 2:" + offsets[1]);
            System.out.println("Math3: " + "1:" + offset.getEntry(0) + ", 2:" + offset.getEntry(1));

            this.subPixelX = pixelX + offsets[0];
            this.subPixelY = pixelY + offsets[1];

            double offsetMagnitude = VectorUtil.getVectorNorm(offsets);
            //double offsetMagnitude = offset.getNorm();
            if (offsetMagnitude > 0.55) {
                System.out.print("Magnitude too large: " + offsetMagnitude + "\n");
                return false;
            }

            double contrast = VectorUtil.getVectorDotProduct(offsets);
            //double contrast = gradient.dotProduct(offset);
            if (Math.abs(contrast) < 0.02) {
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
