package org.example.analyzers.feature.helpers;

import org.example.analyzers.common.PixelPoint;
import org.example.analyzers.feature.Keypoint;
import org.example.analyzers.feature.OctaveSlice;
import org.example.utils.DerivativeUtil;
import org.example.utils.MatrixUtil;
import org.example.utils.VectorUtil;

public class KeypointRefiner {
    private final DescriptorGenerator descriptorGenerator;

    /**
     * Offset magnitude threshold above which keypoint will be discarded.
     * Usually around 0.55
     */
    private final float offsetMagnitudeThreshold;

    /**
     * Contrast threshold below which keypoint will be discarded as noise.
     * Usually between 0.01 and 0.04
     */
    private final float keypointContrastThreshold;

    /**
     * Hessian eigenvalues ratio below which keypoint will be discarded as edge keypoint.
     * Usually between 5 and 20
     */
    private final float keypointEdgeResponseThreshold;

    public KeypointRefiner(float offsetMagnitudeThreshold, float keypointContrastThreshold, float keypointEdgeResponseRatio) {
        this.offsetMagnitudeThreshold = offsetMagnitudeThreshold;
        this.keypointContrastThreshold = keypointContrastThreshold;
        this.keypointEdgeResponseThreshold = ((keypointEdgeResponseRatio+1)*(keypointEdgeResponseRatio+1)) / keypointEdgeResponseRatio;
        this.descriptorGenerator = new DescriptorGenerator();
    }

    public Keypoint refineKeypointCandidate(OctaveSlice octaveSlice, PixelPoint candidate, int neighboursWindowSize) {
        int pixelX = candidate.getX();
        int pixelY = candidate.getY();
        int octaveIndex = octaveSlice.getOctaveIndex();

        float[][] hessianMatrix = calculateKeypointHessian(
                octaveSlice,
                pixelX,
                pixelY );
        if ( !isCandidateValid(hessianMatrix) ) return null;

        float[] gradientVector = DerivativeUtil.approximateGradientVector(
                octaveSlice.getPreviousScale(),
                octaveSlice.getCurrentScale(),
                octaveSlice.getNextScale(),
                pixelX, pixelY);

        float[] offsets = calculatePixelPositionsOffsets(hessianMatrix, gradientVector);
        float subPixelX = pixelX + offsets[0];
        float subPixelY = pixelY + offsets[1];
        if (verifySubpixelMagnitudeAndContrast(offsets) ) return null;

        float[][][] localGradients = computeKeypointLocalGradients(
                octaveSlice.getCurrentScale(),
                pixelX,
                pixelY,
                neighboursWindowSize );

        float[][] localMagnitudes = computeKeypointLocalMagnitudes(localGradients);
        float keypointOrientation = findKeypointDominantOrientation(localGradients, localMagnitudes);
        float[][] localOrientations = computeKeypointOrientations(localGradients, keypointOrientation);

        float[] keypointDescriptor = descriptorGenerator.constructDescriptor(localMagnitudes, localOrientations);

        return new Keypoint(octaveIndex, subPixelX, subPixelY, keypointDescriptor);
    }

    /**
     * Generates 3x3 approximated Hessian matrix for {x,y,x} dimensions
     * @param octaveSlice three consecutive scales from within single octave
     * @param pixelX candidate's width coordinate
     * @param pixelY candidate's height coordinate
     *
     * @return float[][] Hessian matrix
     */
    private float[][] calculateKeypointHessian(OctaveSlice octaveSlice, int pixelX, int pixelY) {
        float[] spaceDerivatives = DerivativeUtil.approximateSpaceDerivatives(
                octaveSlice.getCurrentScale(),
                pixelX,
                pixelY );

        float[] scaleDerivatives = DerivativeUtil.approximateScaleDerivatives(
                octaveSlice.getPreviousScale(),
                octaveSlice.getCurrentScale(),
                octaveSlice.getNextScale(),
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

        if ( (eigenvalues[0] * eigenvalues[1]) < keypointContrastThreshold ) return false;

        float r = (trace*trace) / determinant;
        float edgeThreshold = ((keypointEdgeResponseThreshold+1)*(keypointEdgeResponseThreshold+1)) / keypointEdgeResponseThreshold;

        return r <= keypointEdgeResponseThreshold;
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
        if (offsetMagnitude > offsetMagnitudeThreshold) {
            return false;
        }

        float contrast = VectorUtil.getVectorDotProduct(offsets);

        return !(Math.abs(contrast) >= keypointContrastThreshold);
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
                localGradients[i+radius][j+radius] = DerivativeUtil.approximateGradientVector(imageData, x+i, y+j);
            }
        }
        return localGradients;
    }

    /**
     * Computes magnitudes matrix for all entries in the local gradients matrix
     *
     * @param localGradients matrix containing {dx, dy} entries for each pixel
     *
     * @return matrix of gradient magnitudes
     */
    private float[][] computeKeypointLocalMagnitudes(float[][][] localGradients) {
        float[][] magnitudes = new float[localGradients.length][localGradients[0].length];

        for (int x=0; x<localGradients.length; x++) {
            for (int y=0; y<localGradients[0].length; y++) {
                magnitudes[x][y] = VectorUtil.getVectorNorm(localGradients[x][y]);
            }
        }

        return magnitudes;
    }

    /**
     * Iterates through gradients matrix, calculates magnitude of each gradient and returns orientation of the largest magnitude
     *
     * @param localGradients matrix of {dx, dy} gradients
     * @param localMagnitudes matrix of gradients magnitudes computed from localGradients
     *
     * @return dominant orientation in degrees
     */
    private float findKeypointDominantOrientation(float[][][] localGradients, float[][] localMagnitudes) {
        float maxMagnitude = 0;
        int maxX=0, maxY=0;
        for (int x=0; x<localMagnitudes.length; x++) {
            for (int y=0; y<localMagnitudes[0].length; y++) {
                float magnitude = localMagnitudes[x][y];
                if (magnitude > maxMagnitude) {
                    maxMagnitude = magnitude;
                    maxX = x;
                    maxY = y;
                }
            }
        }
        return VectorUtil.getVectorDegreesOrientation2D( localGradients[maxX][maxY] );
    }


    /**
     * Computes orientations of the entire gradients matrix and subtracts keypoint's dominant orientation from each value.
     *
     * @param localGradients matrix containing {dx,dy} values
     * @param keypointOrientation keypoint dominant orientation based on highest gradient magnitude
     *
     * @return new matrix containing orientations in degrees
     */
    private float[][] computeKeypointOrientations(float[][][] localGradients, float keypointOrientation) {
        float[][] orientations = new float[localGradients.length][localGradients[0].length];

        for (int x=0; x<localGradients.length; x++) {
            for (int y = 0; y < localGradients[0].length; y++) {
                float localOrientation = VectorUtil.getVectorDegreesOrientation2D( localGradients[x][y] );
                float orientation = localOrientation - keypointOrientation;
                if (orientation < 0) orientation += 360;
                orientations[x][y] = orientation;
            }
        }
        return orientations;
    }
}
