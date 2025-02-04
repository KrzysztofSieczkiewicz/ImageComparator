package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class BackupMatrixKeypointHelper {

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


    public void detectKeypoints(float[][][][] dogPyramid) {
        int octavesNum = dogPyramid.length;
        int scalesNum = dogPyramid[0].length;

        // TODO: drop separate Pixelpoints, KeypointCandidates and Candidates lists. Just find candidates and one by one process them (or not) into Keypoints

        for (int octaveIndex=0; octaveIndex<octavesNum; octaveIndex++) {
            float[][][] octave = dogPyramid[octaveIndex];

            for (int scaleIndex=1; scaleIndex<scalesNum-1; scaleIndex++) {

                OctaveSlice octaveSlice = new OctaveSlice(
                        scaleIndex,
                        octave[scaleIndex-1],
                        octave[scaleIndex],
                        octave[scaleIndex+1]
                );

                // 0. find potential keypoints
                ArrayList<PixelPoint> potentialCandidates = findKeypointCandidatesButSmarter(octave[scaleIndex-1], octave[scaleIndex], octave[scaleIndex+1]);

                System.out.println("Potential candidates: " + potentialCandidates.size());

                // 1. filter potential keypoints by checking contrast and edge response
                ArrayList<KeypointCandidate> keypointCandidates = potentialCandidates.stream()
                        .map(potentialCandidate -> new KeypointCandidate(octaveSlice, potentialCandidate))
                        .filter(candidate ->
                                candidate.checkIsNotLowContrast(keypointContrastThreshold) &&
                                candidate.checkIsNotEdgeResponse(keypointEdgeResponseRatio))
                        .collect(Collectors.toCollection(ArrayList::new));

                System.out.println("Keypoint candidates: " + keypointCandidates.size());

                // 2. refine candidates into full keypoints
                ArrayList<Keypoint> keypoints = keypointCandidates.stream()
                        .map(KeypointCandidate::refineCandidate)
                        .collect(Collectors.toCollection(ArrayList::new));


                // 3. subpixel refinement
                keypoints = keypoints.stream()
                        .filter(keypoint -> {
                            keypoint.subpixelRefinement(keypointContrastThreshold);
                            double orientation = keypoint.computeOrientation();
                            //System.out.println("Orientation: " + orientation);
                            return true;
                        })
                        .collect(Collectors.toCollection(ArrayList::new));


                // 4. at this point keypoints should be ready to make into full descriptor, but only after:
                //  a. calculate exact position of keypoint (subpixel coordinates)
                //  b. assigning orientation to each Keypoint (compute the Gradient Magnitude and Orientation) and create orientation histogram
                //      and finally decide on dominant orientation
                //
                // then, convert keypoints into normalized descriptors.


                // 5. Use descriptor distances and RANSAC to match keypoints across different images

                System.out.println("Keypoints: " + keypoints.size());

            }
        }
    }

    /**
     * Searches through provided octave slice (three consecutive scales within single octave) for potential
     * keypoint candidates. Requires that the images are the same size
     *
     * @param previousImage image from previous scale within the same octave
     * @param currentImage central scale image that will be searched for candidates
     * @param nextImage image from next scale within the same octave
     * @return ArrayList containing pixel coordinates of potential keypoint candidates
     */
    public ArrayList<PixelPoint> findPotentialKeypoints(float[][] previousImage, float[][] currentImage, float[][] nextImage) {
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

    /**
     * Searches through provided octave slice (three consecutive scales within single octave) for potential
     * keypoint candidates. Requires that the images are the same size
     *
     * @param previousImage image from previous scale within the same octave
     * @param currentImage central scale image that will be searched for candidates
     * @param nextImage image from next scale within the same octave
     * @return ArrayList containing pixel coordinates of potential keypoint candidates
     */
    private ArrayList<PixelPoint> findKeypointCandidatesButSmarter(float[][] previousImage, float[][] currentImage, float[][] nextImage) {
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        int rows = currentImage.length;
        int cols = currentImage[0].length;

        ArrayList<PixelPoint> baseCandidates = new ArrayList<>();

        {   // Find all keypoints in the base image
            int[] dRow = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};

            for (int row=1; row<rows-1; row++) {
                for (int col = 1; col < cols - 1; col++) {
                    float currentPixel = currentImage[row][col];
                    boolean isMinimum = true;
                    boolean isMaximum = true;

                    for (int k=0; k<dRow.length; k++) {
                        float neighbourValue = currentImage
                                [row + dRow[k]]
                                [col + dCol[k]];

                        if (currentPixel >= neighbourValue) isMinimum = false;
                        if (currentPixel <= neighbourValue) isMaximum = false;

                        if (!isMinimum && !isMaximum) break;
                    }

                    if (isMaximum || isMinimum) {
                        baseCandidates.add(new PixelPoint(row, col));
                    }
                }

            }
        }
        {   // Filter baseCandidates through previous and next images
            int[] dRow = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
            int[] dCol = {-1, 0, 1, -1, 0, 1, -1, 0, 1};

            for( PixelPoint candidate : baseCandidates) {
                int row = candidate.getX();
                int col = candidate.getY();
                float currentPixel = currentImage[row][col];

                boolean isMinimum = true;
                boolean isMaximum = true;

                for (int k=0; k<dRow.length; k++) {
                    float previousValue = previousImage
                            [row + dRow[k]]
                            [col + dCol[k]];
                    if (currentPixel >= previousValue) isMinimum = false;
                    if (currentPixel <= previousValue) isMaximum = false;

                    float nextValue = nextImage
                            [row + dRow[k]]
                            [col + dCol[k]];
                    if (currentPixel >= nextValue) isMinimum = false;
                    if (currentPixel <= nextValue) isMaximum = false;

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
