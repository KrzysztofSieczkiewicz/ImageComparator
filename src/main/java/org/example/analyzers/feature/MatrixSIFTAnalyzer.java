package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageDataUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MatrixSIFTAnalyzer {
    // TODO - CURRENT: test if DoG is handling edge cases and if there are aliasing issues with image downscaling

    // TODO: can be memory optimized by merging buildGaussianPyramid with buildDoGPyramid
    //  that'd work by discarding each gaussian images after necessary dog is computed

    /**
     * When to stop creating octaves
     */
    int minImageSizeThreshold = 16;

    /**
     * How many scales should be generated per one octave
     */
    int scalesAmount = 3;

    /**
     * Base sigma value determining initial image blur
     */
    double baseSigma = 1.6;

    /**
     * Determines Gaussian blurring kernel dimension (multiplier * sigma)
     */
    int blurringSizeMultiplier = 6;

    /**
     * Downsampling factor by which the image is reduced between octaves
     */
    int downsamplingFactor = 2;

    /**
     * Contrast threshold below which keypoint will be discarded as noise
     */
    double keypointContrastThreshold = 0.03;

    /**
     * Hessian eigenvalues ratio below which keypoint will be discarded as edge keypoint
     */
    double keypointEdgeResponseRatio = 10;


    public void constructScaleSpace(int[][] imageData) {
        MatrixGaussianHelper helper = new MatrixGaussianHelper(baseSigma, blurringSizeMultiplier);

        // 0. Greyscale the image
        int[][] greyscaleImageData = ImageDataUtil.convertToGreyscale(imageData);

        // 1. Octaves
        int octavesAmount = calculateOctavesNum(greyscaleImageData, minImageSizeThreshold, downsamplingFactor);

        // 2. Build Gaussian Pyramid
        int[][][][] gaussianPyramid = helper.buildGaussianPyramid(greyscaleImageData, octavesAmount, scalesAmount, downsamplingFactor);

        // 3. Build DoG pyramid
        int[][][][] dogPyramid = helper.buildDoGPyramid(gaussianPyramid);

        // 4. Find keypoints in the DoG pyramid
        detectKeypoints(dogPyramid);

    }

    public int calculateOctavesNum(int[][] imageData, int minSizeThreshold, int downsamplingFactor) {
        int currWidth = imageData.length;
        int currHeight = imageData[0].length;

        int octaves = 0;
        while((currWidth/downsamplingFactor >= minSizeThreshold) && (currHeight/downsamplingFactor >= minSizeThreshold)) {
            octaves++;
            currWidth /= downsamplingFactor;
            currHeight /= downsamplingFactor;
        }

        return octaves;
    }

    private void detectKeypoints(int[][][][] dogPyramid) {
        int octavesNum = dogPyramid.length;
        int scalesNum = dogPyramid[0].length;

        for (int octaveIndex=0; octaveIndex<octavesNum; octaveIndex++) {
            int[][][] octave = dogPyramid[octaveIndex];

            for (int scaleIndex=1; scaleIndex<scalesNum-1; scaleIndex++) {

                int[][][] octaveSlice = {
                        octave[scaleIndex-1],
                        octave[scaleIndex],
                        octave[scaleIndex+1] };

                // 0. find potential keypoints
                ArrayList<PixelPoint> potentialCandidates = findPotentialKeypoints(octave[scaleIndex-1], octave[scaleIndex], octave[scaleIndex+1]);

                potentialCandidates.forEach(candidate -> {
                    System.out.println("X: " + candidate.getX() + ", Y: " + candidate.getY());
                });

                // 1. filter potential keypoints by checking contrast and edge response
                ArrayList<KeypointCandidate> keypointCandidates = potentialCandidates.stream()
                        .map(potentialCandidate -> new KeypointCandidate(octaveSlice, potentialCandidate))
                        .filter(candidate ->
                                !candidate.isLowContrast(keypointContrastThreshold) &&
                                !candidate.isEdgeResponse(keypointEdgeResponseRatio))
                        .collect(Collectors.toCollection(ArrayList::new));

                keypointCandidates.forEach(candidate -> {
                    System.out.println("X: " + candidate.getX() + ", Y: " + candidate.getY());
                });

                // 2. refine candidates into full keypoints
                ArrayList<Keypoint> keypoints = keypointCandidates.stream()
                        .map(KeypointCandidate::refineCandidate)
                        .collect(Collectors.toCollection(ArrayList::new));


                // 3. subpixel refinement
                keypoints = keypoints.stream()
                        .filter(Keypoint::subpixelRefinement)
                        .collect(Collectors.toCollection(ArrayList::new));

                // 4. at this point keypoints should be ready to make into full descriptor, but only after:
                //  a. calculate exact position of keypoint (subpixel coordinates)
                //  b. assigning orientation to each Keypoint (compute the Gradient Magnitude and Orientation) and create orientation histogram
                //      and finally decide on dominant orientation
                //
                // then, convert keypoints into normalized descriptors.

                // 5. Use descriptor distances and RANSAC to match keypoints across different images
                keypoints.forEach(keypoint -> {
                    System.out.println("X: " + keypoint.getSubPixelX() + ", Y: " + keypoint.getSubPixelY());
                });

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
    private ArrayList<PixelPoint> findPotentialKeypoints(int[][] previousImage, int[][] currentImage, int[][] nextImage) {
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        int rows = currentImage.length;
        int cols = currentImage[0].length;

        int[] dRow = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] dCol = {0, 0, -1, 1, -1, 1, -1, 1};

        for (int row=1; row<rows-1; row++) {
            for (int col=1; col<cols-1; col++) {
                int currentPixel = currentImage[row][col];
                boolean isMinimum = true;
                boolean isMaximum = true;

                for (int k=0; k<dRow.length; k++) {
                    int currRow = row + dRow[k];
                    int currCol = col + dCol[k];

                    // compare with current scale
                    int neighbourValue = currentImage[currRow][currCol];
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
    private ArrayList<PixelPoint> findKeypointCandidatesButSmarter(int[][] previousImage, int[][] currentImage, int[][] nextImage) {
        ArrayList<PixelPoint> keypointCandidates = new ArrayList<>();

        int rows = currentImage.length;
        int cols = currentImage[0].length;

        ArrayList<PixelPoint> baseCandidates = new ArrayList<>();

        {   // Find all keypoints in the base image
            int[] dRow = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] dCol = {-1, 0, 1, -1, 1, -1, 0, 1};

            for (int row=1; row<rows-1; row++) {
                for (int col = 1; col < cols - 1; col++) {
                    int currentPixel = currentImage[row][col];
                    boolean isMinimum = true;
                    boolean isMaximum = true;

                    for (int k=0; k<dRow.length; k++) {
                        int neighbourValue = currentImage
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
                int currentPixel = currentImage[row][col];

                boolean isMinimum = true;
                boolean isMaximum = true;

                for (int k=0; k<dRow.length; k++) {
                    int previousValue = previousImage
                            [row + dRow[k]]
                            [col + dCol[k]];
                    if (currentPixel >= previousValue) isMinimum = false;
                    if (currentPixel <= previousValue) isMaximum = false;

                    int nextValue = nextImage
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
