package org.example.analyzers.feature;

import org.example.analyzers.feature.helpers.KeypointDetector;
import org.example.utils.accessor.ImageDataUtil;

import java.util.ArrayList;

public class MatrixSIFTAnalyzer {
    private final KeypointDetector keypointDetector;
    private final MatrixGaussianHelper gaussianHelper;

    /**
     * Image size below which octaves won't be created
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
     * Downsampling factor by which the image is reduced between octaves
     */
    int downsamplingFactor = 2;


    public MatrixSIFTAnalyzer() {
        this.keypointDetector = new KeypointDetector();
        this.gaussianHelper = new MatrixGaussianHelper(baseSigma);
    }


    public void constructScaleSpace(int[][] imageData) {

        // 0. Greyscale the image
        int[][] greyscaleImageData = ImageDataUtil.greyscale(imageData);

        // 1. Octaves
        int octavesAmount = calculateOctavesNum(greyscaleImageData, minImageSizeThreshold, downsamplingFactor);

        // 2. Build Gaussian Pyramid
        float[][][][] gaussianPyramid = gaussianHelper.buildGaussianPyramid(greyscaleImageData, octavesAmount, scalesAmount, downsamplingFactor);

        // 3. Build DoG pyramid
        float[][][][] dogPyramid = gaussianHelper.buildDoGPyramid(gaussianPyramid);

        // 4. Find keypoints in the DoG pyramid
        ArrayList<Keypoint> keypoints = keypointDetector.detectKeypoints(dogPyramid);

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
