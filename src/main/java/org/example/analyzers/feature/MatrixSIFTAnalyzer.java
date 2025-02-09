package org.example.analyzers.feature;

import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageDataUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MatrixSIFTAnalyzer {
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
    int downscalingFactor = 2;


    public MatrixSIFTAnalyzer() {
        this.gaussianHelper = new MatrixGaussianHelper(baseSigma, scalesAmount);
    }


    public void computeImageKeypoints(BufferedImage image) {
        ImageAccessor accessor = ImageAccessor.create(image);
        int[][] imageData = accessor.getPixels();

        int[][] greyscaleImageData = ImageDataUtil.greyscale(imageData);

        int octavesAmount = calculateOctavesNum(greyscaleImageData);

        ArrayList<Keypoint> keypoints = gaussianHelper.processImageKeypoints(greyscaleImageData, octavesAmount, downscalingFactor);
    }

    public int calculateOctavesNum(int[][] imageData) {
        int currWidth = imageData.length;
        int currHeight = imageData[0].length;

        int octaves = 0;
        while((currWidth/downscalingFactor >= minImageSizeThreshold) && (currHeight/downscalingFactor >= minImageSizeThreshold)) {
            octaves++;
            currWidth /= downscalingFactor;
            currHeight /= downscalingFactor;
        }

        return octaves;
    }
}
