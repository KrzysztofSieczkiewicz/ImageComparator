package org.example.analyzers.feature.keypoints;

import org.example.analyzers.feature.OctaveSlice;
import org.example.utils.ImageDataUtil;

import java.util.ArrayList;

public class GaussianPyramidProcessor {
    private final KeypointFinder keypointDetector;

    /**
     * Image size below which octaves won't be created
     */
    int minImageSizeThreshold;

    /**
     * Downscaling factor by which the image is reduced between octaves
     */
    double downscalingFactor;

    /**
     * Determines base sigma from which blurring in an octave will start
     */
    private final double baseSigma;

    /**
     * Number of Gaussian images for a single octave
     */
    private final int imagesPerOctave;

    /**
     * Value by which gaussian sima is multiplied for consecutive scales within octave
     */
    private final double sigmaInterval;

    public GaussianPyramidProcessor(double sigma, int imagesPerOctave, double downscalingFactor, int minImageSizeThreshold) {
        this.keypointDetector = new KeypointFinder(); //contrastThreshold, offsetMagnitudeThreshold, edgeResponseRatio, neighbourWindowSize, localExtremeSearchRadius

        this.baseSigma = sigma;
        this.imagesPerOctave = imagesPerOctave;
        this.downscalingFactor = downscalingFactor;
        this.minImageSizeThreshold = minImageSizeThreshold;

        this.sigmaInterval = calculateScaleIntervals();
    }

    public float[][][] generateConsecutiveGaussians(float[][] image, int scale, int numberOfGaussians) {
        double sigma = baseSigma * Math.pow(sigmaInterval, scale);

        float[][][] gaussians = new float
                [numberOfGaussians]
                [image.length]
                [image[0].length];

        gaussians[0] = image;
        for (int i=1; i<numberOfGaussians; i++) {
            gaussians[i] = ImageDataUtil.gaussianBlurGreyscaled(image, sigma);
            sigma *= sigmaInterval;
        }

        return gaussians;
    }

    public OctaveSlice processSingleDoGSlice(float[][][] gaussians, int octave) {
        int numberOfGaussians = gaussians.length - 1;

        float[][][] DoGs = new float
                [numberOfGaussians-1]
                [gaussians[0].length]
                [gaussians[0][0].length];

        for (int s=0; s<numberOfGaussians; s++) {
            DoGs[s] = ImageDataUtil.subtractImages(gaussians[s], gaussians[s+1]);
        }

        return new OctaveSlice(
                DoGs,
                octave,
                downscalingFactor
        );
    }


    public ArrayList<Keypoint> findKeypoints(float[][] imageData) {
        int octavesNum = calculateNumberOfOctaves(imageData);

        ArrayList<Keypoint> keypoints = new ArrayList<>();
        for (int octave=0; octave<octavesNum; octave++) {
            ArrayList<Keypoint> octaveKeypoints = processOctave(imageData, octave);
            keypoints.addAll(octaveKeypoints);

            imageData = ImageDataUtil.resizeWithAveraging(
                    imageData,
                    (int)(imageData.length / downscalingFactor),
                    (int)(imageData[0].length / downscalingFactor));
        }

        return keypoints;
    }

    /**
     * Generates number of scales to satisfy config requirements for DoG comparisons. Generates Gaussian images and DoGs on the go,
     * compares them and finds keypoints for all scales within octave.

     * @return ArrayList with all keypoints found in all scales within specified octave
     */
    private ArrayList<Keypoint> processOctave(float[][] imageData, int octaveIndex) {
        double gaussianSigma = baseSigma;
        float[][] currentImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);
        gaussianSigma *= sigmaInterval;
        float[][] nextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);
        gaussianSigma *= sigmaInterval;
        float[][] nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);

        float[][] previousDoGImage = ImageDataUtil.subtractImages(currentImage, imageData);
        previousDoGImage = ImageDataUtil.normalizationZScore(previousDoGImage);
        float[][] currentDoGImage = ImageDataUtil.subtractImages(nextImage, currentImage);
        currentDoGImage = ImageDataUtil.normalizationZScore(currentDoGImage);
        float[][] nextDoGImage = ImageDataUtil.subtractImages(nextNextImage, nextImage);
        nextDoGImage = ImageDataUtil.normalizationZScore(nextDoGImage);

        ArrayList<Keypoint> octaveKeypoints = new ArrayList<>();

        for (int scale = 0; scale < imagesPerOctave-2; scale++) {

            OctaveSlice octaveSlice = new OctaveSlice(
                    new float[][][] { previousDoGImage, currentDoGImage, nextDoGImage },
                    octaveIndex,
                    downscalingFactor
            );

            octaveKeypoints.addAll( keypointDetector.findKeypoints(octaveSlice) );

            gaussianSigma *= sigmaInterval;
            nextImage = nextNextImage;
            nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);

            previousDoGImage = currentDoGImage;
            currentDoGImage = nextDoGImage;
            nextDoGImage = ImageDataUtil.subtractImages(nextNextImage, nextImage);
            nextDoGImage = ImageDataUtil.normalizationZScore(nextDoGImage);
        }

        return octaveKeypoints;
    }

    /**
     * Calculates sigma multiplier which determines blurring progression within single octave
     * @return sigma multiplier
     */
    private double calculateScaleIntervals() {
        double p = 1d/ imagesPerOctave;
        return Math.pow(2, p);
    }

    /**
     * Checks how many times image can be downsized with provided downscalingFactor and minimal image size
     * @return number of octaves that can be created
     */
    public int calculateNumberOfOctaves(float[][] imageData) {
        int currWidth = imageData.length;
        int currHeight = imageData[0].length;

        int octaves = 0;
        while( (currWidth/downscalingFactor >= minImageSizeThreshold) &&
               (currHeight/downscalingFactor >= minImageSizeThreshold) ) {
            octaves++;
            currWidth = (int)(currWidth / downscalingFactor);
            currHeight = (int)(currHeight / downscalingFactor);
        }

        return octaves;
    }
}