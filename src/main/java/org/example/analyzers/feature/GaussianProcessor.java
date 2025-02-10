package org.example.analyzers.feature;

import org.example.analyzers.feature.helpers.KeypointDetector;
import org.example.utils.ImageDataUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GaussianProcessor {
    private final KeypointDetector keypointDetector;

    /**
     * Image size below which octaves won't be created
     */
    int minImageSizeThreshold;

    /**
     * Downscaling factor by which the image is reduced between octaves
     */
    int downscalingFactor;

    /**
     * Determines base sigma from which blurring in an octave will start
     */
    private final double baseSigma;

    /**
     * Number of DoG images for single octave
     */
    private final int numberDoGImages;

    /**
     * Value by which gaussian sima is multiplied for consecutive scales within octave
     */
    private final double sigmaInterval;

    public GaussianProcessor(double sigma, int numberDoGImages, int downscalingFactor, int minImageSizeThreshold) {
        keypointDetector = new KeypointDetector();

        this.baseSigma = sigma;
        this.numberDoGImages = numberDoGImages;
        this.downscalingFactor = downscalingFactor;
        this.minImageSizeThreshold = minImageSizeThreshold;

        this.sigmaInterval = calculateScaleIntervals();
    }

    public ArrayList<Keypoint> processImageKeypoints(float[][] imageData) {
        int octavesNum = calculateNumberOfOctaves(imageData);

        ArrayList<Keypoint> keypoints = new ArrayList<>();
        for (int octave = 0; octave < octavesNum; octave++) {
            ArrayList<Keypoint> octaveKeypoints = processOctave(imageData, octave);
            keypoints.addAll(octaveKeypoints);

            imageData = ImageDataUtil.resizeWithAveraging(
                    imageData,
                    imageData.length / downscalingFactor,
                    imageData[0].length / downscalingFactor);
        }

        return keypoints;
    }

    /**
     * Generates number of scales to satisfy config requirements for DoG comparisons. Generates Gaussian images and DoGs on the go,
     * compares them and finds keypoints for all scales within octave.

     * @return ArrayList with all keypoints found in all scales within specified octave
     */
    private ArrayList<Keypoint> processOctave(float[][] imageData, int octaveIndex) {

        //imageData = ImageDataUtil.gaussianBlurGreyscaled(imageData, baseSigma);

        double gaussianSigma = baseSigma;
        float[][] currentImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);
        gaussianSigma *= sigmaInterval;
        float[][] nextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);
        gaussianSigma *= sigmaInterval;
        float[][] nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);

        saveImageFloat(imageData, "ComputedGreyscaleImage_prev_"+ octaveIndex +".png");
        saveImageFloat(currentImage, "ComputedGreyscaleImage_curr_"+ octaveIndex +".png");
        saveImageFloat(nextImage, "ComputedGreyscaleImage_next_"+ octaveIndex +".png");
        saveImageFloat(nextNextImage, "ComputedGreyscaleImage_nextNext_"+ octaveIndex +".png");

        float[][] previousDoGImage = ImageDataUtil.subtractImages(currentImage, imageData);
        previousDoGImage = ImageDataUtil.normalizationZScore(previousDoGImage);
        float[][] currentDoGImage = ImageDataUtil.subtractImages(nextImage, currentImage);
        currentDoGImage = ImageDataUtil.normalizationZScore(currentDoGImage);
        float[][] nextDoGImage = ImageDataUtil.subtractImages(nextNextImage, nextImage);
        nextDoGImage = ImageDataUtil.normalizationZScore(nextDoGImage);

        saveImageFloat(previousDoGImage, "ComputedDoGImage_prev_"+ octaveIndex +".png");
        saveImageFloat(currentDoGImage, "ComputedDoGImage_curr_"+ octaveIndex +".png");
        saveImageFloat(nextDoGImage, "ComputedDoGImage_next_"+ octaveIndex +".png");

        ArrayList<Keypoint> octaveKeypoints = new ArrayList<>();

        for (int scale = 1; scale < numberDoGImages; scale++) {
            octaveKeypoints.addAll( keypointDetector.detectImageKeypoints(octaveIndex, previousDoGImage, currentDoGImage, nextDoGImage) );

            gaussianSigma *= sigmaInterval;
            nextImage = nextNextImage;
            nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);

            saveImageFloat(nextNextImage, "ComputedGreyscaleImage_nextNext_"+ octaveIndex+scale +".png");

            previousDoGImage = currentDoGImage;
            currentDoGImage = nextDoGImage;
            nextDoGImage = ImageDataUtil.subtractImages(nextNextImage, nextImage);
            nextDoGImage = ImageDataUtil.normalizationZScore(nextDoGImage);

            saveImageFloat(nextDoGImage, "ComputedDoGImage_nextNextImage_"+ octaveIndex+scale +".png");
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

    /**
     * Checks how many times image can be downsized with provided downscalingFactor and minimal image size (config)
     * @return number of octaves that can be created
     */
    private int calculateNumberOfOctaves(float[][] imageData) {
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


    public static void saveImageFloat(float[][] imageData, String filePath) {
        if (imageData == null || imageData.length == 0 || imageData[0].length == 0) {
            throw new IllegalArgumentException("Invalid image data");
        }

        int width = imageData.length;
        int height = imageData[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelValue = Math.round(imageData[x][y]);
                //pixelValue = (pixelValue + 1) * 128;
                int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue;
                image.setRGB(x, y, rgb);
            }
        }

        try {
            File outputFile = new File(filePath);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}