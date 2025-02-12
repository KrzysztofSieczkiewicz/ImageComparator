package org.example.analyzers.feature;

import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageDataUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MatrixSIFTAnalyzer {
    private final GaussianProcessor gaussianProcessor;
    private final SIFTMatcher siftMatcher;

    /**
     * How many scales should be generated per one octave
     */
    int scalesAmount = 2;

    /**
     * Base sigma value determining initial image blur
     */
    double baseSigma = 1.6;

    /**
     * Image size below which octaves won't be created
     */
    int minImageSizeThreshold = 32;

    /**
     * Downscaling factor by which the image is reduced between octaves
     */
    int downscalingFactor = 2;


    public MatrixSIFTAnalyzer() {
        this.gaussianProcessor = new GaussianProcessor(baseSigma, scalesAmount, downscalingFactor, minImageSizeThreshold);
        this.siftMatcher = new SIFTMatcher(150);
    }

    public ArrayList<Keypoint> computeImageKeypoints(BufferedImage image) {
        ImageAccessor accessor = ImageAccessor.create(image);
        int[][] imageData = accessor.getPixels();

        float[][] greyscaleImageData = ImageDataUtil.greyscaleToFloat(imageData);

        return gaussianProcessor.processImageKeypoints(greyscaleImageData);
    }

    public ArrayList<FeatureMatch> matchKeypoints(ArrayList<Keypoint> baseKeypoints, ArrayList<Keypoint> comparedKeypoints) {
        ArrayList<FeatureMatch> matches = siftMatcher.matchKeypoints(baseKeypoints, comparedKeypoints, 0.8f);


        return matches;
    }

    public void compareKeypoints(ArrayList<Keypoint> main, ArrayList<Keypoint> checked) {

    }
}