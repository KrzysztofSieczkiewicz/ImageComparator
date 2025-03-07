package org.example.analyzers.feature.keypoints;

import org.example.analyzers.common.PixelPoint;
import org.example.analyzers.feature.OctaveSlice;
import org.example.config.SobelKernelSize;
import org.example.utils.DerivativeUtil;
import org.example.utils.MatrixUtil;
import org.example.utils.VectorUtil;

import java.util.ArrayList;
import java.util.List;

public class KeypointFinder {
    private final DescriptorGenerator descriptorGenerator;

    private final int[][] relativeNeighboursCoordinates;


    /**
     * Contrast threshold below which keypoint will be discarded as noise.
     * Usually between 0.01 and 0.04
     */
    private final float contrastThreshold;

    /**
     * Offset magnitude threshold above which keypoint will be discarded.
     * Usually around 0.55
     */
    private final float offsetMagnitudeThreshold;

    /**
     * Hessian eigenvalues ratio below which keypoint will be discarded as edge keypoint.
     * Usually between 5 and 20
     */
    private final float edgeResponseRatio;

    /**
     * How large should the window of neighbours around keypoint be. Will be scaled by each octave
     */
    private final int neighbourWindowSize;

    /**
     * How large should the window of neighbours around keypoint be for local extremes search
     */
    private final int localExtremesSearchRadius;


    /**
     * Size of the Sobel kernel used for 2nd order derivatives approximation
     */
    SobelKernelSize sobelKernelSize = SobelKernelSize.SOBEL7x7;

    public KeypointFinder(float contrastThreshold, float offsetMagnitudeThreshold, float edgeResponseRatio, int neighbourWindowSize, int localExtremeSearchRadius) {
        this.contrastThreshold = contrastThreshold;
        this.offsetMagnitudeThreshold = offsetMagnitudeThreshold;
        this.edgeResponseRatio = edgeResponseRatio;
        this.neighbourWindowSize = neighbourWindowSize;
        this.localExtremesSearchRadius = localExtremeSearchRadius;

        this.relativeNeighboursCoordinates = generateWindowRelativeCoordinates(localExtremeSearchRadius);
        this.descriptorGenerator = new DescriptorGenerator();
    }

    public ArrayList<Keypoint> findKeypoints(OctaveSlice octaveSlice) {
        ArrayList<Keypoint> keypoints = new ArrayList<>();

        ArrayList<PixelPoint> potentialCandidates = findKeypointCandidates(octaveSlice);
        if (potentialCandidates.isEmpty()) return keypoints;

        for (PixelPoint candidate: potentialCandidates) {
            Keypoint keypoint = refineKeypointCandidate(octaveSlice, candidate);
            if (keypoint != null) keypoints.add(keypoint);
        }

        return keypoints;
    }

    /**
     * Searches for local extremes in provided octave slice. Uses all provided scales for scale dimension and neighbour window size for xy dimension.
     *
     * @param octaveSlice containing images that contain the extreme.
     * @return ArrayList containing pixel coordinates of potential keypoint candidates
     */
    public ArrayList<PixelPoint> findKeypointCandidates(OctaveSlice octaveSlice) {
        float[][] centralImage = octaveSlice.getMainImage();
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        int rows = centralImage.length;
        int cols = centralImage[0].length;

        int edgeOffset = localExtremesSearchRadius;
        int[] dRow = relativeNeighboursCoordinates[0];
        int[] dCol = relativeNeighboursCoordinates[1];

        for (int row=edgeOffset; row<rows-edgeOffset; row++) {
            for (int col=edgeOffset; col<cols-edgeOffset; col++) {
                float currentPixel = centralImage[row][col];
                boolean isMinimum = true;
                boolean isMaximum = true;

                // check main image
                for ( int k=0; k<dRow.length; k++) {
                    int currRow = row + dRow[k];
                    int currCol = col + dCol[k];
                    if (currRow == row && currCol == col) continue;

                    float neighbourValue = centralImage[currRow][currCol];
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;
                    if (!isMinimum && !isMaximum) break;
                }
                if (!isMinimum && !isMaximum) continue;

                // check neighbouring images
                for ( float[][] image: octaveSlice.getPeripheralImages() ) {
                    for (int k=0; k<dRow.length; k++) {
                        int currRow = row + dRow[k];
                        int currCol = col + dCol[k];

                        float neighbourValue = image[currRow][currCol];

                        if (currentPixel >= neighbourValue) isMinimum = false;
                        if (currentPixel <= neighbourValue) isMaximum = false;
                        if (!isMinimum && !isMaximum) break;
                    }
                    if (!isMinimum && !isMaximum) break;
                }

                if (isMaximum || isMinimum) {
                    keypointCandidates.add(new PixelPoint(row, col));
                }
            }
        }

        return keypointCandidates;
    }

    public PixelPoint refineCandidate(OctaveSlice slice, PixelPoint candidate) {
        int pixelX = candidate.getX();
        int pixelY = candidate.getY();

        float[][] hessianMatrix = approxKeypointHessian(
                slice,
                pixelX,
                pixelY );
        if ( !ifContrastAndEdgeResponseValid(hessianMatrix) ) return null;

        return candidate;
    }

    public Keypoint refineKeypointCandidate(OctaveSlice octaveSlice, PixelPoint candidate) {
        int pixelX = candidate.getX();
        int pixelY = candidate.getY();
        int octaveIndex = octaveSlice.getOctaveIndex();
        double keypointPositionRatio = octaveSlice.getDownscalingRatio();

        float[][] hessianMatrix = approxKeypointHessian(
                octaveSlice,
                pixelX,
                pixelY );
        if ( !ifContrastAndEdgeResponseValid(hessianMatrix) ) return null;

        float[] gradientVector;
        gradientVector = DerivativeUtil.approximateGradientVector(
                octaveSlice.getImages(),
                pixelX, pixelY);

        float[] offsets = calculatePixelPositionsOffsets(hessianMatrix, gradientVector);
        if ( !ifSubpixelOffsetWithinBounds(offsets) ) return null;

        float subPixelX = (float) (pixelX*keypointPositionRatio) + offsets[0];
        float subPixelY = (float) (pixelY*keypointPositionRatio) + offsets[1];

        float[][][] localGradients = computeKeypointLocalGradients(octaveSlice.getMainImage(), pixelX, pixelY );
        float[] keypointDescriptor = descriptorGenerator.constructDescriptor(localGradients);

        return new Keypoint(octaveIndex, subPixelX, subPixelY, keypointDescriptor);
    }

    // TODO: can be merged with refineKeypointCandidate later on
    public List<Keypoint> refineKeypointCandidates(OctaveSlice octaveSlice, List<PixelPoint> candidates) {
        List<Keypoint> keypoints = new ArrayList<>();

        for (PixelPoint candidate : candidates) {
            Keypoint keypoint = refineKeypointCandidate(octaveSlice, candidate);
            if (keypoint != null) keypoints.add(keypoint);
        }

        return keypoints;
    }

    /**
     * Generates relative coordinates for an n-neighbours radius (including central pixel)
     * @param radius how many neighbours should be added into a window
     * @return int[2][(2*radius+1) * (2*radius+1)] {dRows, dCols}
     */
    private int[][] generateWindowRelativeCoordinates(int radius) {
        int size = (2 * radius + 1) * (2 * radius + 1);
        int[] dRows = new int[size];
        int[] dCols = new int[size];

        int index = 0;
        for (int x=-radius; x<=radius; x++) {
            for (int y=-radius; y<=radius; y++) {
                dRows[index] = x;
                dCols[index] = y;
                index++;
            }
        }

        return new int[][]{dRows, dCols};
    }

    /**
     * Generates approximated Hessian matrix for {x,y,x} dimensions using Sobel kernels
     * @param octaveSlice for heesians to be found in
     * @param pixelX candidate's width coordinate
     * @param pixelY candidate's height coordinate
     *
     * @return float[][] Hessian matrix
     */
    private float[][] approxKeypointHessian(OctaveSlice octaveSlice, int pixelX, int pixelY) {
        int lastImageIndex = octaveSlice.getImages().length - 1;

        float[] spaceDerivatives = DerivativeUtil.approxSpaceDerivatives(
                octaveSlice.getMainImage(),
                pixelX,
                pixelY,
                (float)(1.6*Math.pow(1.41f, octaveSlice.getScaleIndex())) );
//        if (sobelKernelSize.equals(SobelKernelSize.SOBEL3x3)) {
//            spaceDerivatives = DerivativeUtil.approximateSpaceDerivatives3x3(
//                    octaveSlice.getMainImage(),
//                    pixelX,
//                    pixelY );
//        } else if (sobelKernelSize.equals(SobelKernelSize.SOBEL5x5)){
//            spaceDerivatives = DerivativeUtil.approximateSpaceDerivatives5x5(
//                    octaveSlice.getMainImage(),
//                    pixelX,
//                    pixelY );
//        } else {
//            spaceDerivatives = DerivativeUtil.approximateSpaceDerivatives7x7(
//                    octaveSlice.getMainImage(),
//                    pixelX,
//                    pixelY );
//        }

        float[] scaleDerivatives = DerivativeUtil.approximateScaleDerivatives(
                octaveSlice.getImages()[0],
                octaveSlice.getMainImage(),
                octaveSlice.getImages()[lastImageIndex],
                pixelX,
                pixelY );

        return new float[][] {
                { spaceDerivatives[0], spaceDerivatives[1],  scaleDerivatives[1]},
                { spaceDerivatives[1], spaceDerivatives[2],  scaleDerivatives[2]},
                { scaleDerivatives[1], scaleDerivatives[2],  scaleDerivatives[0]} };
    }

    /**
     * Validates keypoint candidate by checking for edge responses and low contrasts
     * @return true if candidate's contrast is valid
     */
    private boolean ifContrastAndEdgeResponseValid(float[][] hessianMatrix) {
        float trace = MatrixUtil.getMatrixTrace(hessianMatrix, 2);
        float determinant = MatrixUtil.get2x2MatrixDeterminant(hessianMatrix);
        float discriminant = MatrixUtil.get2x2MatrixDiscriminant(trace, determinant);
        double[] eigenvalues = MatrixUtil.get2x2MatrixEigenvalues(trace, discriminant);

        if (Math.abs(eigenvalues[0] * eigenvalues[1]) < contrastThreshold) return false;

        float r = Math.abs( trace * trace / determinant );

        return r <= edgeResponseRatio;
    }

    /**
     * Calculates subpixel position offset of the keypoint.
     *
     * @param hessianMatrix 3x3 hessian matrix of the keypoint
     * @param gradientsVector {dx, dy, ds} vector with gradients in each dimension
     *
     * @return offsets float[2] array containing {x,y}. Adding these values to initial pixel coordinates, gives precise keypoint location
     */
    private float[] calculatePixelPositionsOffsets(float[][] hessianMatrix, float[] gradientsVector) {
        float[][] regularizedHessianMatrix = MatrixUtil.diagonalRegularization(hessianMatrix);
        return MatrixUtil.solveMatrix(regularizedHessianMatrix, VectorUtil.multiplyVector(gradientsVector, -1.0f) );
    }

    /**
     * Validates keypoint by checking if the subpixel offset is significant enough.
     * Achieved by comparing keypoint magnitude and associated contrast against thresholds.
     *
     * @param offsets array[2] of subpixel position offsets {x, y}
     * @return true if the subpixel offset passes both checks
     */
    private boolean ifSubpixelOffsetWithinBounds(float[] offsets) {
        float offsetMagnitude = VectorUtil.getVectorNorm(offsets);

        if (offsetMagnitude > offsetMagnitudeThreshold) {
            return false;
        }

        if (offsetMagnitude > 1f) {
            //
        }
        return true;

//        float contrast = VectorUtil.getVectorDotProduct(offsets);
//
//        return !(Math.abs(contrast) >= contrastThreshold);
    }

    /**
     * Calculates local gradients of pixels inside window around given x,y coordinate.
     * Handles out of bound by edge reflection.
     *
     * @param imageData float matrix containing image pixel values
     * @param x central point x coordinate
     * @param y central point y coordinate
     *
     * @return matrix of {dx, dy} gradients
     */
    private float[][][] computeKeypointLocalGradients(float[][] imageData, int x, int y) {
        float[][][] localGradients = new float[neighbourWindowSize][neighbourWindowSize][2];

        int radius = neighbourWindowSize / 2;
        for (int i=-radius; i<radius; i++) {
            for (int j = -radius; j < radius; j++) {
                localGradients[i + radius][j + radius] = DerivativeUtil.approximateGradientVector(imageData, x + i, y + j);
            }
        }
        return localGradients;
    }

}