package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.DerivativeUtil;
import org.example.utils.MatrixUtil;

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

        // TODO: needed here?
        this.hessianTrace = MatrixUtil.getMatrixTrace(hessianMatrix);
        this.hessianDeterminant = MatrixUtil.get2x2MatrixDeterminant(hessianMatrix);
        float hessianDiscriminant = MatrixUtil.get2x2MatrixDiscriminant(hessianTrace, hessianDeterminant);

        this.eigenvalues = MatrixUtil.get2x2MatrixEigenvalues(hessianTrace, hessianDiscriminant);
    }

    /**
     * Verification method for low contrast keypoint candidates
     *
     * @return true if candidate's contrast is above threshold
     */
    public boolean checkIsNotLowContrast(float contrastThreshold) {
        return (eigenvalues[0] * eigenvalues[1]) >= contrastThreshold;
    }

    /**
     * Verification method if keypoint candidate is an edge response.
     *
     * @return true if candidate is not an edge response
     */
    public boolean checkIsNotEdgeResponse(float ratioThreshold) {
        float r = (hessianTrace*hessianTrace) / hessianDeterminant;
        float edgeThreshold = (float) Math.pow( ratioThreshold+1, 2 ) / ratioThreshold;

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