package org.example.analyzers.feature;

import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageDataUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MatrixSIFTAnalyzer {
    private final GaussianProcessor gaussianProcessor;
    private final SIFTMatcher siftMatcher;

    /**
     * How many Gaussian images should be generated per one octave
     */
    int imagesPerOctave = 5;

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
        this.gaussianProcessor = new GaussianProcessor(baseSigma, imagesPerOctave, downscalingFactor, minImageSizeThreshold);
        this.siftMatcher = new SIFTMatcher(150, 0.8f);
    }

    public ArrayList<Keypoint> computeImageKeypoints(BufferedImage image) {
        ImageAccessor accessor = ImageAccessor.create(image);
        int[][] imageData = accessor.getPixels();

        float[][] greyscaleImageData = ImageDataUtil.greyscaleToFloat(imageData);

        return gaussianProcessor.processImageKeypoints(greyscaleImageData);
    }

    public ArrayList<FeatureMatch> matchKeypoints(ArrayList<Keypoint> base, ArrayList<Keypoint> checked) {
        ArrayList<FeatureMatch> matches = siftMatcher.matchKeypoints(base, checked);


        return matches;
    }
}