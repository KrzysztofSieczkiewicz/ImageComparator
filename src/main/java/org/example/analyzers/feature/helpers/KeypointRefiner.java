package org.example.analyzers.feature.helpers;

import org.example.analyzers.common.PixelPoint;
import org.example.analyzers.feature.Keypoint;
import org.example.config.SobelKernelSize;
import org.example.utils.DerivativeUtil;
import org.example.utils.MatrixUtil;
import org.example.utils.VectorUtil;

public class KeypointRefiner {
    private final DescriptorGenerator descriptorGenerator;

    /**
     * Offset magnitude threshold above which keypoint will be discarded.
     * Usually around 0.55
     */
    private final float magnitudeThreshold;

    /**
     * Contrast threshold below which keypoint will be discarded as noise.
     * Usually between 0.01 and 0.04
     */
    private final float contrastThreshold;

    /**
     * Hessian eigenvalues ratio below which keypoint will be discarded as edge keypoint.
     * Usually between 5 and 20
     */
    private final float edgeResponseThreshold;

    /**
     * Size of the Sobel kernel used for 2nd order derivatives approximation
     */
    private final SobelKernelSize sobelKernelSize;

    public KeypointRefiner(float magnitudeThreshold, float contrastThreshold, float keypointEdgeResponseRatio, SobelKernelSize sobelKernelSize) {
        this.magnitudeThreshold = magnitudeThreshold;
        this.contrastThreshold = contrastThreshold;
        this.edgeResponseThreshold = ((keypointEdgeResponseRatio+1)*(keypointEdgeResponseRatio+1)) / keypointEdgeResponseRatio;
        this.sobelKernelSize = sobelKernelSize;

        this.descriptorGenerator = new DescriptorGenerator();

    }

    public Keypoint refineKeypointCandidate(ScalesTriplet scalesTriplet, PixelPoint candidate, int neighboursWindowSize) {
        int pixelX = candidate.getX();
        int pixelY = candidate.getY();
        int octaveIndex = scalesTriplet.getOctaveIndex();

        float[][] hessianMatrix = calculateKeypointHessian(
                scalesTriplet,
                pixelX,
                pixelY );
        if ( !isCandidateValid(hessianMatrix) ) return null;

        float[] gradientVector;

        gradientVector = DerivativeUtil.approximateGradientVector(
                scalesTriplet.getPreviousScale(),
                scalesTriplet.getCurrentScale(),
                scalesTriplet.getNextScale(),
                pixelX, pixelY);

        float[] offsets = calculatePixelPositionsOffsets(hessianMatrix, gradientVector);
        float subPixelX = pixelX + offsets[0];
        float subPixelY = pixelY + offsets[1];
        if (verifySubpixelMagnitudeAndContrast(offsets) ) return null;

        float[][][] localGradients = computeKeypointLocalGradients(scalesTriplet.getCurrentScale(), pixelX, pixelY, neighboursWindowSize );
        float[] keypointDescriptor = descriptorGenerator.constructDescriptor(localGradients);

        return new Keypoint(octaveIndex, subPixelX, subPixelY, keypointDescriptor);
    }

    /**
     * Generates 3x3 approximated Hessian matrix for {x,y,x} dimensions
     * @param scalesTriplet three consecutive scales from within single octave
     * @param pixelX candidate's width coordinate
     * @param pixelY candidate's height coordinate
     *
     * @return float[][] Hessian matrix
     */
    private float[][] calculateKeypointHessian(ScalesTriplet scalesTriplet, int pixelX, int pixelY) {
        float[] spaceDerivatives;
        if (sobelKernelSize.equals(SobelKernelSize.SOBEL3x3)) {
            spaceDerivatives = DerivativeUtil.approximateSpaceDerivatives3x3(
                    scalesTriplet.getCurrentScale(),
                    pixelX,
                    pixelY );
        } else {
            spaceDerivatives = DerivativeUtil.approximateSpaceDerivatives5x5(
                    scalesTriplet.getCurrentScale(),
                    pixelX,
                    pixelY );
        }

        float[] scaleDerivatives = DerivativeUtil.approximateScaleDerivatives(
                scalesTriplet.getPreviousScale(),
                scalesTriplet.getCurrentScale(),
                scalesTriplet.getNextScale(),
                pixelX,
                pixelY );

        return new float[][] {
                { spaceDerivatives[0], spaceDerivatives[1],  scaleDerivatives[1]},
                { spaceDerivatives[1], spaceDerivatives[2],  scaleDerivatives[2]},
                { scaleDerivatives[1], scaleDerivatives[2],  scaleDerivatives[0]} };
    }

    /**
     * Checks if keypoint candidate is valid by checking for edge responses and low contrast
     * @return true if candidate's contrast is valid
     */
    private boolean isCandidateValid(float[][] hessianMatrix) {
        float trace = MatrixUtil.getMatrixTrace(hessianMatrix, 2);
        float determinant = MatrixUtil.get2x2MatrixDeterminant(hessianMatrix);
        float discriminant = MatrixUtil.get2x2MatrixDiscriminant(trace, determinant);
        float[] eigenvalues = MatrixUtil.get2x2MatrixEigenvalues(trace, discriminant);

        if ( (eigenvalues[0] * eigenvalues[1]) < contrastThreshold) return false;

        float r = (trace*trace) / determinant;

        return r <= edgeResponseThreshold;
    }


    /**
     * Calculates subpixel position offset of the keypoint.
     *
     * @param hessianMatrix 3x3 hessian matrix of the keypoint
     * @param gradientsVector {dx, dy, ds} vector with gradients in each dimension
     *
     * @return offsets {x,y} that, added to initial pixel coordinates, give precise keypoint location
     */
    private float[] calculatePixelPositionsOffsets(float[][] hessianMatrix, float[] gradientsVector) {
        float[][] regularizedHessianMatrix = MatrixUtil.diagonalRegularization(hessianMatrix);
        return MatrixUtil.getMatrixSolution(regularizedHessianMatrix, VectorUtil.multiplyVector(gradientsVector, -1.0f) );
    }

    /**
     * Verifies if the subpixel offset is significant enough by checking
     * its magnitude and associated contrast.
     *
     * @param offsets array[2] of subpixel position offsets {x, y}
     * @return true if the subpixel offset passes both checks
     */
    private boolean verifySubpixelMagnitudeAndContrast(float[] offsets) {
        float offsetMagnitude = VectorUtil.getVectorNorm(offsets);
        if (offsetMagnitude > magnitudeThreshold) {
            return false;
        }

        float contrast = VectorUtil.getVectorDotProduct(offsets);

        return !(Math.abs(contrast) >= contrastThreshold);
    }

    /**
     * Calculates local gradients of pixels inside window around given x,y coordinates.
     * Handles out of bound by edge reflection.
     *
     * @param imageData float matrix containing image pixel values
     * @param x central point x coordinate
     * @param y central point y coordinate
     * @param windowSize width/height of sliced window
     *
     * @return matrix of {dx, dy} gradients
     */
    private float[][][] computeKeypointLocalGradients(float[][] imageData, int x, int y, int windowSize) {
        float[][][] localGradients = new float[windowSize][windowSize][2];

        int radius = windowSize / 2;
        for (int i=-radius; i<radius; i++) {
            for (int j = -radius; j < radius; j++) {
                localGradients[i + radius][j + radius] = DerivativeUtil.approximateGradientVector(imageData, x + i, y + j);
            }
        }
        return localGradients;
    }

}