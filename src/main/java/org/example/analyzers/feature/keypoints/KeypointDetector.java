package org.example.analyzers.feature.keypoints;

import org.example.analyzers.common.PixelPoint;
import org.example.analyzers.feature.helpers.ScalesTriplet;
import org.example.config.SobelKernelSize;
import org.example.utils.MatrixUtil;

import java.util.ArrayList;

public class KeypointDetector {
    private final KeypointRefiner refiner;

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
     * How large should the window of neighbours around keypoint be. Will be scaled by each octave
     */
    int baseNeighboursWindowSize = 16;

    /**
     * How large should the window for local extreme search be around each point.
     */
    int localExtremeRadius = 1;

    /**
     * Size of the Sobel kernel used for 2nd order derivatives approximation
     */
    SobelKernelSize sobelKernelSize = SobelKernelSize.SOBEL5x5;


    private final int[][] relativeNeighboursCoordinates;


    public KeypointDetector() {
        this.relativeNeighboursCoordinates = generateWindowRelativeCoordinates(localExtremeRadius);

        this.refiner = new KeypointRefiner(offsetMagnitudeThreshold, keypointContrastThreshold, keypointEdgeResponseRatio, sobelKernelSize, baseNeighboursWindowSize);
    }

    // TODO: EITHER REMOVE ScalesTriplet OR INITIALIZE IT IN THE GAUSSIAN PROCESSOR TO PASS IT AS AN ARG
    public ArrayList<Keypoint> detectImageKeypoints(ScalesTriplet scalesTriplet) {
        ArrayList<Keypoint> imageKeypoints = new ArrayList<>();

        ArrayList<PixelPoint> potentialCandidates = findLocalExtremes(scalesTriplet);
        if (potentialCandidates.isEmpty()) return imageKeypoints;

        for (PixelPoint candidate: potentialCandidates) {
            Keypoint keypoint = refiner.refineKeypointCandidate(scalesTriplet, candidate);
            if (keypoint != null) imageKeypoints.add(keypoint);
        }

        return imageKeypoints;
    }

    public ArrayList<Keypoint> detectPyramidKeypoints(float[][][][] dogPyramid) {
        int octavesNum = dogPyramid.length;
        int scalesNum = dogPyramid[0].length;

        ArrayList<Keypoint> imageKeypoints = new ArrayList<>();

        for (int octaveIndex=0; octaveIndex<octavesNum; octaveIndex++) {
            float[][][] octave = dogPyramid[octaveIndex];

            for (int scaleIndex = 1; scaleIndex < scalesNum - 1; scaleIndex++) {
                ScalesTriplet scalesTriplet = new ScalesTriplet(
                        octaveIndex,
                        octave[scaleIndex-1],
                        octave[scaleIndex],
                        octave[scaleIndex+1]
                );

                ArrayList<PixelPoint> potentialCandidates = findLocalExtremes(scalesTriplet);
                if (potentialCandidates.isEmpty()) continue;

                for (PixelPoint candidate: potentialCandidates) {
                    Keypoint keypoint = refiner.refineKeypointCandidate(scalesTriplet, candidate);
                    if (keypoint != null) imageKeypoints.add(keypoint);
                }

            }
        }

        return imageKeypoints;
    }

    /**
     * Searches through provided octave slice (three consecutive scales within single octave) for potential
     * keypoint candidates. Requires that the images are the same size
     *
     * @param scalesTriplet images within the same octave to find extremes in
     * @return ArrayList containing pixel coordinates of potential keypoint candidates
     */
    private ArrayList<PixelPoint> findLocalExtremes(ScalesTriplet scalesTriplet) {
        float[][] previousImage = scalesTriplet.getPreviousScale();
        float[][] currentImage = scalesTriplet.getCurrentScale();
        float[][] nextImage = scalesTriplet.getNextScale();
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        int rows = currentImage.length;
        int cols = currentImage[0].length;

        int[] dRow = relativeNeighboursCoordinates[0];
        int[] dCol = relativeNeighboursCoordinates[1];

        for (int row=0; row<rows; row++) {
            for (int col=0; col<cols; col++) {
                float currentPixel = currentImage[row][col];
                boolean isMinimum = true;
                boolean isMaximum = true;

                for (int k=0; k<dRow.length; k++) {
                    int currRow = MatrixUtil.safeReflectCoordinate( row + dRow[k], rows );
                    int currCol = MatrixUtil.safeReflectCoordinate( col + dCol[k], cols );

                    float neighbourValue = currentImage[currRow][currCol];
                    if (currentPixel > neighbourValue) isMinimum = false;
                    if (currentPixel < neighbourValue) isMaximum = false;

                    neighbourValue = previousImage[currRow][currCol];
                    if (currentPixel > neighbourValue) isMinimum = false;
                    if (currentPixel < neighbourValue) isMaximum = false;

                    neighbourValue = nextImage[currRow][currCol];
                    if (currentPixel > neighbourValue) isMinimum = false;
                    if (currentPixel < neighbourValue) isMaximum = false;

                    if (!isMinimum && !isMaximum) break;
                }

                if (isMaximum || isMinimum) {
                    keypointCandidates.add(new PixelPoint(row, col));
                }
            }
        }

        return keypointCandidates;
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
}
