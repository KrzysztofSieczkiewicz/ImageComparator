package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.DerivativeUtil;
import org.example.utils.MatrixUtil;
import org.example.utils.VectorUtil;

import java.util.ArrayList;
// TODO: rename to KeyppointFinder
public class MatrixKeypointHelper {

    /**
     * Contrast threshold below which keypoint will be discarded as noise.
     * Usually between 0.01 and 0.04
     */
    float keypointContrastThreshold = 0.04f;

    /**
     * Offset magnitude threshold above which keypoint will be discarded.
     * Usually around 0.55
     */
    float offsetMagnitudeThreshold = 0.55f;

    /**
     * Hessian eigenvalues ratio below which keypoint will be discarded as edge keypoint.
     * Usually between 5 and 20
     */
    float keypointEdgeResponseRatio = 10;

    /**
     * How large should the window of neighbours around keypoint be. Will be further scaled by each octave
     */
    int baseNeighboursWindowSize = 16;


    // TODO: CLEAN THIS CODE BEFORE MOVING FURTHER - THIS CLASS SHOULD BE COMPLETE SO CLEANUP!!!!
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

                    if (subpixelRefinement(offsets, offsetMagnitudeThreshold, keypointContrastThreshold)) continue;

                    // Retrieve pixels 16x16 slice around requested point

                    // To do it properly:
                    //  1. I need a matrix of local orientations of ~16x16 pixel window
                    //  2. For each of these orientations I need its magnitude to find dominant orientation
                    //  3. I need to adjust all local orientations by subtracting dominant orientation
                    //  4. I need to create 4 histograms of orientations and join them into longer descriptor
                    float[][][] localGradients = computeKeypointLocalGradients(
                            octaveSlice.getCurrentScale(),
                            pixelX,
                            pixelY,
                            baseNeighboursWindowSize * (1 << octaveIndex) );

                    float keypointOrientation = findKeypointDominantOrientation(localGradients);
                    float[][] localOrientations = computeKeypointOrientations(localGradients, keypointOrientation);


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

    /**
     * Calculates subpixel position offset of the keypoint.
     *
     * @param hessianMatrix 3x3 hessian matrix of the keypoint
     * @param gradientsVector {dx, dy, ds} vector with gradients in each dimension
     *
     * @return offsets {x,y} that, added to initial pixel coordinates, give precise keypoint location
     */
    public float[] calculatePixelPositionsOffsets(float[][] hessianMatrix, float[] gradientsVector) {
        float[][] regularizedHessianMatrix = MatrixUtil.diagonalRegularization(hessianMatrix);
        return MatrixUtil.getMatrixSolution(regularizedHessianMatrix, VectorUtil.multiplyVector(gradientsVector, -1.0f) );
    }


    /**
     * Verifies if selected pixel is significant enough by offset magnitude and contrast checks
     *
     * @param offsets array[2] of subpixel position offsets {x,y}
     * @param offsetMagnitudeThreshold threshold for offset magnitude above which keypoint is discarded
     * @param contrastThreshold threshold for contrast check below which keypoint is discarded
     * @return
     */
    public boolean subpixelRefinement(float[] offsets, float offsetMagnitudeThreshold, float contrastThreshold) {

        float offsetMagnitude = VectorUtil.getVectorNorm(offsets);
        if (offsetMagnitude > offsetMagnitudeThreshold) {
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
    public float[][][] computeKeypointLocalGradients(float[][] imageData, int x, int y, int windowSize) {
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
    public float[][] computeKeypointMagnitudes(float[][][] localGradients) {
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
    public float findKeypointDominantOrientation(float[][][] localGradients, float[][] localMagnitudes) {
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
    public float[][] computeKeypointOrientations(float[][][] localGradients, float keypointOrientation) {
        float[][] orientations = new float[localGradients.length][localGradients[0].length];

        for (int x=0; x<localGradients.length; x++) {
            for (int y = 0; y < localGradients[0].length; y++) {
                float localOrientation = VectorUtil.getVectorDegreesOrientation2D( localGradients[x][y] );
                orientations[x][y] = localOrientation - keypointOrientation;
            }
        }
        return orientations;
    }

    public void constructDescriptor(float[][] gradientMagnitudes, float[][] relativeOrientations) {
        int numBins = 8;
        int descriptorLength = 128; // 16 cells x 8 bins
        float[] descriptor = new float[descriptorLength];

        int index = 0;
        for (int cellX=0; cellX<4; cellX++) {
            for (int cellY=0; cellY<4; cellY++) {
                float[] localHistogram = new float[numBins];

                for (int pixelX=0; pixelX<4; pixelX++) {
                    for (int pixelY=0; pixelY<4; pixelY++) {
                        int x = cellX + 4 * pixelX;
                        int y = cellY + 4 * pixelY;

                        float magnitude = gradientMagnitudes[x][y];
                        float orientation = relativeOrientations[x][y];

                        int bin = (int) Math.floor(orientation / (float)(360/numBins) ) % numBins;
                        localHistogram[bin] += magnitude;
                    }
                }

                System.arraycopy(localHistogram, 0, descriptor, index, numBins);
                index += numBins;
            }
        }

        // normalize descriptor here. Add proper method to Vector utils
    }


}
