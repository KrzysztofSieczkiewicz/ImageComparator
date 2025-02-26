package org.example.analyzers.feature.keypoints;

import org.example.analyzers.common.PixelPoint;
import org.example.analyzers.feature.OctaveSlice;
import org.example.config.SobelKernelSize;
import org.example.utils.MatrixUtil;

import java.util.ArrayList;

public class KeypointFinder {
    private final KeypointRefiner refiner;

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
     * How large should the window for local extreme search be around each point.
     */
    private final int localExtremeSearchRadius;

    /**
     * Size of the Sobel kernel used for 2nd order derivatives approximation
     */
    SobelKernelSize sobelKernelSize = SobelKernelSize.SOBEL5x5;

    public KeypointFinder(float contrastThreshold, float offsetMagnitudeThreshold, float edgeResponseRatio, int neighbourWindowSize, int localExtremeSearchRadius) {
        this.contrastThreshold = contrastThreshold;
        this.offsetMagnitudeThreshold = offsetMagnitudeThreshold;
        this.edgeResponseRatio = edgeResponseRatio;
        this.neighbourWindowSize = neighbourWindowSize;
        this.localExtremeSearchRadius = localExtremeSearchRadius;

        this.relativeNeighboursCoordinates = generateWindowRelativeCoordinates(this.localExtremeSearchRadius);
        this.refiner = new KeypointRefiner(offsetMagnitudeThreshold, contrastThreshold, edgeResponseRatio, sobelKernelSize, neighbourWindowSize);
    }

    public ArrayList<Keypoint> findKeypoints(OctaveSlice octaveSlice) {
        ArrayList<Keypoint> keypoints = new ArrayList<>();

        ArrayList<PixelPoint> potentialCandidates = findLocalExtremes(octaveSlice);
        if (potentialCandidates.isEmpty()) return keypoints;

        for (PixelPoint candidate: potentialCandidates) {
            Keypoint keypoint = refiner.refineKeypointCandidate(octaveSlice, candidate);
            if (keypoint != null) keypoints.add(keypoint);
        }

        return keypoints;
    }

    /**
     * Searches through provided octave slice (three consecutive scales within single octave) for potential
     * keypoint candidates. Requires that the images are the same size.
     *
     * @param octaveSlice containing images that contain the extreme.
     * @return ArrayList containing pixel coordinates of potential keypoint candidates
     */
    private ArrayList<PixelPoint> findLocalExtremes(OctaveSlice octaveSlice) {
        float[][] centralImage = octaveSlice.getMainImage();
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        int rows = centralImage.length;
        int cols = centralImage[0].length;

        int[] dRow = relativeNeighboursCoordinates[0];
        int[] dCol = relativeNeighboursCoordinates[1];

        for (int row=0; row<rows; row++) {
            for (int col=0; col<cols; col++) {
                float currentPixel = centralImage[row][col];
                boolean isMinimum = true;
                boolean isMaximum = true;

                for ( float[][] image: octaveSlice.getImages() ) {
                    for (int k=0; k<dRow.length; k++) {
                        int currRow = MatrixUtil.safeReflectCoordinate( row + dRow[k], rows );
                        int currCol = MatrixUtil.safeReflectCoordinate( col + dCol[k], cols );

                        float neighbourValue = image[currRow][currCol];
                        if (currentPixel > neighbourValue) isMinimum = false;
                        if (currentPixel < neighbourValue) isMaximum = false;

                        if (!isMinimum && !isMaximum) break;
                    }
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
