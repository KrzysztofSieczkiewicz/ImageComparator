package org.example.analyzers.feature;

import org.example.analyzers.feature.helpers.KeypointDetector;
import org.example.utils.ImageDataUtil;

import java.util.ArrayList;

public class MatrixGaussianHelper {
    private final KeypointDetector keypointDetector;

    /**
     * Determines base sigma from which blurring in an octave will start
     */
    private final double baseSigma;

    /**
     * Number of DoG images for single octave
     */
    private final int numberDoGImages;

    public MatrixGaussianHelper(double sigma, int numberDoGImages) {
        keypointDetector = new KeypointDetector();

        this.baseSigma = sigma;
        this.numberDoGImages = numberDoGImages;
    }

    public ArrayList<Keypoint> processImageKeypoints(int[][] intImageData, int octavesNum, int downsamplingFactor) {
        float[][] imageData = ImageDataUtil.convertToFloatMatrix(intImageData);
        double sigmaInterval = calculateScaleIntervals();

        ArrayList<Keypoint> keypoints = new ArrayList<>();

        for (int octave = 0; octave < octavesNum; octave++) {
            ArrayList<Keypoint> octaveKeypoints = processOctave(imageData, octave, sigmaInterval);
            keypoints.addAll(octaveKeypoints);

            imageData = ImageDataUtil.resizeWithAveraging(
                    imageData,
                    imageData.length / downsamplingFactor,
                    imageData[0].length / downsamplingFactor);
        }

        return keypoints;
    }

    private ArrayList<Keypoint> processOctave(float[][] imageData, int octaveIndex, double sigmaInterval) {
        double gaussianSigma = baseSigma;
        float[][] currentImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);
        gaussianSigma *= sigmaInterval;
        float[][] nextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);
        gaussianSigma *= sigmaInterval;
        float[][] nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);

        float[][] previousDoGImage = ImageDataUtil.subtractImages(imageData, currentImage);
        float[][] currentDoGImage = ImageDataUtil.subtractImages(currentImage, nextImage);
        float[][] nextDoGImage = ImageDataUtil.subtractImages(nextImage, nextNextImage);

        ArrayList<Keypoint> octaveKeypoints = new ArrayList<>();

        for (int scale = 1; scale < numberDoGImages; scale++) {
            octaveKeypoints.addAll( keypointDetector.detectImageKeypoints(octaveIndex, previousDoGImage, currentDoGImage, nextDoGImage) );

            gaussianSigma *= sigmaInterval;
            nextImage = nextNextImage;
            nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);

            previousDoGImage = currentDoGImage;
            currentDoGImage = nextDoGImage;
            nextDoGImage = ImageDataUtil.subtractImages(nextImage, nextNextImage);
        }
        return octaveKeypoints;
    }

    /**
     * Calculates sigma multiplier which determines blurring progression within single octave
     * @return sigma multiplier
     */
    private double calculateScaleIntervals() {
        double p = 1d/numberDoGImages;
        return Math.pow(2, p);
    }
}
