package org.example.analyzers.feature;

import org.example.analyzers.feature.helpers.KeypointDetector;
import org.example.utils.accessor.ImageDataUtil;

import java.util.ArrayList;

public class MatrixGaussianHelper {
    private final KeypointDetector keypointDetector;

    /**
     * Determines base sigma from which blurring in an octave will start
     */
    double baseSigma;

    public MatrixGaussianHelper(double sigma) {
        keypointDetector = new KeypointDetector();
        this.baseSigma = sigma;
    }

    // TODO: move dogScalesNum to config file
    public void buildDoG(float[][] imageData, int octavesNum, int dogScalesNum, int downsamplingFactor) {
        double sigmaInterval = calculateScaleIntervals(dogScalesNum);

        int numberOfScales = dogScalesNum + 3;
        float[][][] gaussianImages = new float[numberOfScales][imageData.length][imageData[0].length];
        double baseScale = baseSigma;

        for (int i = 0; i < numberOfScales; i++) {
            gaussianImages[i] = ImageDataUtil.gaussianBlurGreyscaled(imageData, baseScale);
            baseScale *= sigmaInterval;
        }

        // TODO: FINISH HERE:
        //  update scaling to use progressing sigmas instead of blurring multiple times
        for (int octave=0; octave<octavesNum; octave++) {
            float[][] previousImage = imageData;
            float[][] currentImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, baseScale);
            float[][] nextImage = ImageDataUtil.gaussianBlurGreyscaled(currentImage, baseScale);
            float[][] nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(nextImage, baseScale);

            float[][] previousDoGScale = calculateDifferences(previousImage, currentImage);
            float[][] currentDoGScale = calculateDifferences(currentImage, nextImage);
            float[][] nextDoGScale = calculateDifferences(nextImage, nextNextImage);

            ArrayList<Keypoint> scaleKeypoints = new ArrayList<>();

            for (int scale = 1; scale < dogScalesNum; scale++) {
                // Find features in the dogImage -> detector must be modified not to work with pyramid but single image instead;
                scaleKeypoints.addAll(keypointDetector.detectKeypoints(octave, previousDoGScale, currentDoGScale, nextDoGScale));

                // Move the gaussian images by one step
                nextImage = nextNextImage;
                nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(nextNextImage, baseScale);

                // Move the DoG by one step
                previousDoGScale = currentDoGScale;
                currentDoGScale = nextDoGScale;
                nextDoGScale = calculateDifferences(nextImage, nextNextImage);
            }
        }
    }

    private void prepareDoGScalesTriplet() {

    }



    public float[][][][] buildGaussianPyramid(int[][] imageData, int octavesNum, int scalesNum, int downsamplingFactor) {
        float[][][][] pyramid = new float[octavesNum][scalesNum+3][][];

        double sigmaInterval = calculateScaleIntervals(scalesNum);

        for (int octave=0; octave<octavesNum; octave++) {
            pyramid[octave] = generateGaussianScales(imageData, scalesNum, baseSigma, sigmaInterval);

            imageData = ImageDataUtil.resizeWithAveraging(
                    imageData,
                    imageData.length/downsamplingFactor,
                    imageData[0].length/downsamplingFactor );
            imageData = ImageDataUtil.gaussianBlur(imageData, 1.6);
        }

        return pyramid;
    }

    public float[][][][] buildDoGPyramid(float[][][][] gaussianPyramid) {
        int octavesNum = gaussianPyramid.length;
        int scalesNum = gaussianPyramid[0].length-1;
        float[][][][] pyramid = new float[octavesNum][scalesNum][][];

        for (int octave=0; octave<octavesNum; octave++) {
            for (int scale=0; scale<scalesNum; scale++) {
                pyramid[octave][scale] = calculateDifferences(
                        gaussianPyramid[octave][scale+1],
                        gaussianPyramid[octave][scale] );
            }
        }

        return pyramid;
    }

    /**
     * Internal method. Calculates difference between two greyscaled images.
     */
    private float[][] calculateDifferences(float[][] firstImage, float[][] secondImage) {
        int width = firstImage.length;
        int height = firstImage[0].length;
        float[][] result = new float[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                result[x][y] = firstImage[x][y] - secondImage[x][y];
            }
        }

        return result;
    }

    /**
     * Generates an entire octave for a gaussian pyramid
     * @param imageData matrix containing image raster
     * @param scalesNum number of scales to generate
     * @param baseSigma base sigma for gaussian blurring
     * @param scaleInterval value by which sigma will be multiplied between blurring
     *
     * @return octave (array of progressively blurred images)
     */
    private float[][][] generateGaussianScales(int[][] imageData, int scalesNum, double baseSigma, double scaleInterval) {
        int numberOfScales = scalesNum + 3;
        float[][][] gaussianImages = new float[numberOfScales][imageData.length][imageData[0].length];
        double baseScale = baseSigma;

        for (int i = 0; i < numberOfScales; i++) {
            gaussianImages[i] = ImageDataUtil.gaussianBlurGreyscaled(imageData, baseScale);
            baseScale *= scaleInterval;
        }

        return gaussianImages;
    }

    /**
     * Calculates sigma multiplier which determines blurring progression within single octave
     * @param scalesAmount number of scales in an octave
     * @return sigma multiplier
     */
    private double calculateScaleIntervals(int scalesAmount) {
        double p = 1d/scalesAmount;
        return Math.pow(2, p);
    }
}
