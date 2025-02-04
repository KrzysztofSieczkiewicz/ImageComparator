package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.DerivativeUtil;
import org.example.utils.MatrixUtil;

import java.util.Arrays;

// TODO: Current - keypoint candidates are not being filtered properly - eigenvalues seem a bit big - check calculations
//  Strong suspicion - DoG images require normalization as even entirely black areas seem to be "high contrast"
//  Probably both normalization and proper threshold

public class KeypointCandidate {

    private final int x,y;
    private final OctaveSlice imagesTriplet;
    private final float[][] hessianMatrix;

    private final float hessianTrace;
    private final float hessianDeterminant;
    private final float[] eigenvalues;


    public KeypointCandidate(OctaveSlice scalesTriplet, PixelPoint point) {
        this.x = point.getX();
        this.y = point.getY();
        this.imagesTriplet = scalesTriplet;

        float[] spaceDerivatives = DerivativeUtil.approximateSpaceDerivatives(
                scalesTriplet.getCurrentScale(),
                x, y );

        float[] scaleDerivatives = DerivativeUtil.approximateScaleDerivatives(
                scalesTriplet.getPreviousScale(),
                scalesTriplet.getCurrentScale(),
                scalesTriplet.getNextScale(),
                x, y );

        this.hessianMatrix = new float[][] {
                { spaceDerivatives[0], spaceDerivatives[1],  scaleDerivatives[1]},
                { spaceDerivatives[1], spaceDerivatives[2],  scaleDerivatives[2]},
                { scaleDerivatives[1], scaleDerivatives[2],  scaleDerivatives[0]}
        };

        this.hessianTrace = MatrixUtil.getMatrixTrace(hessianMatrix, 2);
        this.hessianDeterminant = MatrixUtil.get2x2MatrixDeterminant(hessianMatrix);
        float hessianDiscriminant = MatrixUtil.get2x2MatrixDiscriminant(hessianTrace, hessianDeterminant);
        this.eigenvalues = MatrixUtil.get2x2MatrixEigenvalues(hessianTrace, hessianDiscriminant);

        System.out.println("Candidate");
        System.out.println("X: " + x + ", Y: " + y);
        System.out.println("DoG Pixels: " + Arrays.deepToString(MatrixUtil.getSafeMatrixSlice(scalesTriplet.getCurrentScale(), x, y, 1)));
        System.out.println("Matrix: " + Arrays.deepToString(hessianMatrix));
        System.out.println("Trace: " + hessianTrace);
        System.out.println("Determinant: " + hessianDeterminant);
        System.out.println("Discriminant: " + hessianDiscriminant);
        System.out.println("Eigenvalues: " + Arrays.toString(eigenvalues));
        // Differences in determinants, discriminants and eigenvalues

        //Candidate
        //X: 1270, Y: 181
        //DoG Pixels: [[0.3473816, 0.39178467, 0.35684204], [0.43362427, 0.45986938, 0.4121933], [0.45321655, 0.4580841, 0.40647125]]
        //Matrix: [[-0.28366852, -0.05620575, 0.1199646], [-0.05620575, -0.26746368, -0.05607605], [0.1199646, -0.05607605, -0.1504364]]
        //Trace: -0.5511322
        //Determinant: 0.072711945
        //Discriminant: 0.012898922
        //Eigenvalues: [-0.21877939, -0.33235282]

        //Candidate
        //X: 1270, Y: 181
        //DoG Pixels: [[0.3473816, 0.39178467, 0.35684204], [0.43362427, 0.45986938, 0.4121933], [0.45321655, 0.4580841, 0.40647125]]
        //Matrix: [[-0.28366852, 0.012016296], [0.012016296, -0.26746368]]
        //Trace: -0.5511322021484375
        //Determinant: 0.07572663575410843
        //Discriminant: 8.401612285524607E-4
        //Eigenvalues: [-0.26107333366636615, -0.29005886848207135]
    }

    /**
     * Verification method for low contrast keypoint candidates
     *
     * @return true if candidate's contrast is above threshold
     */
    public boolean checkIsNotLowContrast(float contrastThreshold) {
//        System.out.println( "Contrast check: " + "1: " + eigenvalues[0] + ", 2: " + eigenvalues[1] +
//                ", product: " + eigenvalues[0] * eigenvalues[1] +
//                ", outcome: " + ((eigenvalues[0] * eigenvalues[1]) >= contrastThreshold) );

        return (eigenvalues[0] * eigenvalues[1]) >= contrastThreshold;
    }

    /**
     * Verification method if keypoint candidate is an edge response.
     *
     * @return true if candidate is not an edge response
     */
    public boolean checkIsNotEdgeResponse(float ratioThreshold) {
        float r = (hessianTrace*hessianTrace) / hessianDeterminant;
        float edgeThreshold = ((ratioThreshold+1)*(ratioThreshold+1)) / ratioThreshold;

        return r <= edgeThreshold;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Refines keypoint candidate into keypoint.
     * Passes pixel coordinates, gradients vector and hessian matrix for further calculations without the need for data
     *
     * @return new Keypoint instance
     */
    public Keypoint refineCandidate() {
        float[] gradientVector = DerivativeUtil.approximateGradientVector(
                imagesTriplet.getPreviousScale(),
                imagesTriplet.getCurrentScale(),
                imagesTriplet.getNextScale(),
                x, y);

        double[][] tempHessianMatrix = new double[][] {
                { hessianMatrix[0][0], hessianMatrix[0][1], hessianMatrix[0][2] },
                { hessianMatrix[1][0], hessianMatrix[1][1], hessianMatrix[1][2] },
                { hessianMatrix[2][0], hessianMatrix[2][1], hessianMatrix[2][2] }
        };

        double[] tempGradientVector = new double[] { gradientVector[0], gradientVector[1], gradientVector[2] };

        return new Keypoint(x, y, tempGradientVector, tempHessianMatrix);
    }
}