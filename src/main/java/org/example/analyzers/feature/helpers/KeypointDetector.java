package org.example.analyzers.feature.helpers;

import org.example.analyzers.common.PixelPoint;
import org.example.analyzers.feature.Keypoint;

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
     * How large should the window of neighbours around keypoint be. Will be further scaled by each octave
     */
    int baseNeighboursWindowSize = 16;


    public KeypointDetector() {
        this.refiner = new KeypointRefiner(offsetMagnitudeThreshold, keypointContrastThreshold, keypointEdgeResponseRatio);
    }

    public ArrayList<Keypoint> detectKeypoints(int octaveIndex, float[][] previousScaleData, float[][] currentScaleData, float[][] nextScaleData) {
        ArrayList<Keypoint> imageKeypoints = new ArrayList<>();

        ScalesTriplet scalesTriplet = new ScalesTriplet(
                octaveIndex,
                previousScaleData,
                currentScaleData,
                nextScaleData
        );

        ArrayList<PixelPoint> potentialCandidates = findLocalExtremes(scalesTriplet);
        if (potentialCandidates.isEmpty()) return imageKeypoints;

        for (PixelPoint candidate: potentialCandidates) {
            Keypoint keypoint = refiner.refineKeypointCandidate(scalesTriplet, candidate, baseNeighboursWindowSize);
            if (keypoint != null) imageKeypoints.add(keypoint);
        }

        return imageKeypoints;
    }

    public ArrayList<Keypoint> detectKeypoints(float[][][][] dogPyramid) {
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
                    Keypoint keypoint = refiner.refineKeypointCandidate(scalesTriplet, candidate, baseNeighboursWindowSize);
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

                    float neighbourValue = currentImage[currRow][currCol];
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;

                    neighbourValue = previousImage[currRow][currCol];
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;

                    neighbourValue = nextImage[currRow][currCol];
                    if (currentPixel >= neighbourValue) isMinimum = false;
                    if (currentPixel <= neighbourValue) isMaximum = false;

                    if (!isMinimum && !isMaximum) break;
                }

                if (isMaximum || isMinimum) {
                    keypointCandidates.add(new PixelPoint(row, col));
                }
            }
        }

        return keypointCandidates;
    }

}
