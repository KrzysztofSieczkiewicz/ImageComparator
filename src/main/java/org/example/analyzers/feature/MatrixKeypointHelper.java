package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.DerivativeUtil;
import org.example.utils.MatrixUtil;
import org.example.utils.VectorUtil;

import java.util.ArrayList;
import java.util.Arrays;

// TODO: rename to KeyppointFinder
public class MatrixKeypointHelper {

    /**
     * Contrast threshold below which keypoint will be discarded as noise
     * usually between 0.01 and 0.04
     */
    float keypointContrastThreshold = 0.04f;

    /**
     * Hessian eigenvalues ratio below which keypoint will be discarded as edge keypoint
     * usually between 5 and 20
     */
    float keypointEdgeResponseRatio = 10;

    /**
     * How large should the window of neighbours around keypoint be. Will be further scaled by each octave
     */
    int baseNeighboursWindowSize = 16;


    public void detectKeypoints(float[][][][] dogPyramid) {
        int octavesNum = dogPyramid.length;
        int scalesNum = dogPyramid[0].length;
        
        for (int octaveIndex=0; octaveIndex<octavesNum; octaveIndex++) {
            float[][][] octave = dogPyramid[octaveIndex];

            for (int scaleIndex=1; scaleIndex<scalesNum-1; scaleIndex++) {

                OctaveSlice octaveSlice = new OctaveSlice(
                        scaleIndex,
                        octave[scaleIndex-1],
                        octave[scaleIndex],
                        octave[scaleIndex+1]
                );

                ArrayList<PixelPoint> finalCandidates = new ArrayList<>();
                ArrayList<PixelPoint> potentialCandidates = findLocalExtremes(octaveSlice);
                System.out.println("Potential candidates: " + potentialCandidates.size());

                for (PixelPoint candidate: potentialCandidates) {
                    int pixelX = candidate.getX();
                    int pixelY = candidate.getY();

                    // Calculate basic image data
                    float[][] hessianMatrix = calculateKeypointHessian(
                            octaveSlice,
                            pixelX,
                            pixelY );

                    float hessianTrace = MatrixUtil.getMatrixTrace(hessianMatrix, 2);
                    float hessianDeterminant = MatrixUtil.get2x2MatrixDeterminant(hessianMatrix);
                    float hessianDiscriminant = MatrixUtil.get2x2MatrixDiscriminant(hessianTrace, hessianDeterminant);
                    float[] eigenvalues = MatrixUtil.get2x2MatrixEigenvalues(hessianTrace, hessianDiscriminant);

                    // Skip for low contrast and edge response candidates
                    if (checkIsLowContrast(eigenvalues, keypointContrastThreshold)) continue;
                    if (checkIsEdgeResponse(hessianTrace, hessianDiscriminant, keypointEdgeResponseRatio)) continue;

                    // Refine candidate
                    float[] gradientVector = DerivativeUtil.approximateGradientVector(
                            octaveSlice.getPreviousScale(),
                            octaveSlice.getCurrentScale(),
                            octaveSlice.getNextScale(),
                            pixelX, pixelY);

                    // Subpixel refinement
                    float[] offsets = calculatePixelPositionsOffsets(hessianMatrix, gradientVector);
                    float subPixelX = pixelX + offsets[0];
                    float subPixelY = pixelY + offsets[1];

                    if (subpixelRefinement(offsets, keypointContrastThreshold)) continue;

                    int orientationBin = computeKeypointOrientation(gradientVector);

                    // Retrieve pixels 16x16 slice around requested point
                    float[][] localGradientDistributions = calculateNeighboursGradientOrientations(
                            octaveSlice.getCurrentScale(),
                            pixelX,
                            pixelY,
                            baseNeighboursWindowSize * (1 << octaveIndex) );

                    finalCandidates.add(candidate);
                }

                System.out.println("Final keypoints: " + finalCandidates.size());
            }
        }
    }


    /**
     * Searches through provided octave slice (three consecutive scales within single octave) for potential
     * keypoint candidates. Requires that the images are the same size
     *
     * @param octaveSlice images within the same octave to find extremes in
     * @return ArrayList containing pixel coordinates of potential keypoint candidates
     */
    public ArrayList<PixelPoint> findLocalExtremes(OctaveSlice octaveSlice) {
        float[][] previousImage = octaveSlice.getPreviousScale();
        float[][] currentImage = octaveSlice.getCurrentScale();
        float[][] nextImage = octaveSlice.getNextScale();
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        int rows = currentImage.length;
        int cols = currentImage[0].length;

        int[] dRow = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dCol = {0, 0, -1, 1, -1, 1, -1, 1};

        for (int row=1; row<rows-1; row++) {
            for (int col=1; col<cols-1; col++) {
                float currentPixel = currentImage[row][col];
                boolean isMinimum = true;
                boolean isMaximum = true;

                for (int k=0; k<dRow.length; k++) {
                    int currRow = row + dRow[k];
                    int currCol = col + dCol[k];

                    // compare with current scale
                    float neighbourValue = currentImage[currRow][currCol];
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;

                    // compare with previous scale
                    neighbourValue = previousImage[currRow][currCol];
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;

                    // compare with next scale
                    neighbourValue = nextImage[currRow][currCol];
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;

                    // early exit
                    if (!isMinimum && !isMaximum) break;
                }

                if (isMaximum || isMinimum) {
                    keypointCandidates.add(new PixelPoint(row, col));
                }
            }
        }

        return keypointCandidates;
    }


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
     * Verification method for low contrast keypoint candidates
     * @return true if candidate's contrast is above threshold
     */
    public boolean checkIsLowContrast(float[] eigenvalues, float contrastThreshold) {
        return (eigenvalues[0] * eigenvalues[1]) < contrastThreshold;
    }


    /**
     * Verification method if keypoint candidate is an edge response.
     * @return true if candidate is not an edge response
     */
    public boolean checkIsEdgeResponse(float trace, float determinant, float ratioThreshold) {
        float r = (trace*trace) / determinant;
        float edgeThreshold = ((ratioThreshold+1)*(ratioThreshold+1)) / ratioThreshold;

        return r > edgeThreshold;
    }


    public float[] calculatePixelPositionsOffsets(float[][] hessianMatrix, float[] gradientsVector) {
        float[][] regularizedHessianMatrix = MatrixUtil.diagonalRegularization(hessianMatrix);
        return MatrixUtil.getMatrixSolution(regularizedHessianMatrix, VectorUtil.multiplyVector(gradientsVector, -1.0f) );
    }


    public boolean subpixelRefinement(float[] offsets, float contrastThreshold) {

        float offsetMagnitude = VectorUtil.getVectorNorm(offsets);
        if (offsetMagnitude > 0.55) {
            return false;
        }

        float contrast = VectorUtil.getVectorDotProduct(offsets);

        if (Math.abs(contrast) >= contrastThreshold) {
            return false;
        }

        return true;
    }


    public int computeKeypointOrientation(float[] gradientsVector) {
        double[] histogram = new double[36];

        double magnitude = Math.sqrt( gradientsVector[0]*gradientsVector[0] + gradientsVector[1]*gradientsVector[1]);

        double direction = Math.toDegrees( Math.atan2(gradientsVector[0], gradientsVector[1]) );
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


    public static float[][] calculateNeighboursGradientOrientations(float[][] imageData, int x, int y, int windowSize) {
        int radius = windowSize / 2;

        float[][] orientations = new float[windowSize][windowSize];
        for (int i=-radius; i<radius; i++) {
            for (int j=-radius; j<radius; j++) {
                float[] gradients = DerivativeUtil.approximateGradientVector(imageData, x+i, y+j);
                float orientation = (float) Math.toDegrees( Math.atan2(gradients[0], gradients[1]) );
                if (orientation < 0) orientation += 360;

                orientations[i + radius][j + radius] = orientation;
            }
        }

        return orientations;
    }
}
