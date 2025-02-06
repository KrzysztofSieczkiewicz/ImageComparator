package org.example.analyzers.feature;

import org.example.utils.accessor.ImageDataUtil;

public class MatrixSIFTAnalyzer {
    // TODO - CURRENT: test if DoG is handling edge cases and if there are aliasing issues with image downscaling

    // TODO: can be memory optimized by merging buildGaussianPyramid with buildDoGPyramid
    //  that'd work by discarding each gaussian images after necessary dog is computed

    /**
     * When to stop creating octaves
     */
    int minImageSizeThreshold = 32;

    /**
     * How many scales should be generated per one octave
     */
    int scalesAmount = 2;

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

    // TODO: CURRENT -> time to debug -
    //  go through each dog pyramid step and save all created images.
    //  then go through all keypoint steps and mark them on the image

    public void constructScaleSpace(int[][] imageData) {
        MatrixGaussianHelper gaussianHelper = new MatrixGaussianHelper(baseSigma, blurringSizeMultiplier);
        MatrixKeypointHelper keypointHelper = new MatrixKeypointHelper();

        // 0. Greyscale the image
        int[][] greyscaleImageData = ImageDataUtil.greyscale(imageData);

        // 1. Octaves
        int octavesAmount = calculateOctavesNum(greyscaleImageData, minImageSizeThreshold, downsamplingFactor);

        // 2. Build Gaussian Pyramid
        float[][][][] gaussianPyramid = gaussianHelper.buildGaussianPyramid(greyscaleImageData, octavesAmount, scalesAmount, downsamplingFactor);

        // 3. Build DoG pyramid
        float[][][][] dogPyramid = gaussianHelper.buildDoGPyramid(gaussianPyramid);

        // 4. Find keypoints in the DoG pyramid
        keypointHelper.detectKeypoints(dogPyramid);

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
}
