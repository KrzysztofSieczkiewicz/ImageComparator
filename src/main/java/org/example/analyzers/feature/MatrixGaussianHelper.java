package org.example.analyzers.feature;

import org.example.utils.accessor.ImageDataUtil;

public class MatrixGaussianHelper {

    /**
     * Determines base sigma from which blurring in an octave will start
     */
    double baseSigma;

    /**
     * Determines Gaussian blurring kernel dimension (multiplier * sigma)
     */
    int blurringSizeMultiplier;

    public MatrixGaussianHelper(double sigma, int blurringSizeMultiplier) {
        this.baseSigma = sigma;
        this.blurringSizeMultiplier = blurringSizeMultiplier;
    }

    public int[][][][] buildGaussianPyramid(int[][] imageData, int octavesNum, int scalesNum, int downsamplingFactor) {
        int[][][][] pyramid = new int[octavesNum][scalesNum][][];

        double sigmaInterval = calculateScaleIntervals(scalesNum);

        for (int octave=0; octave<octavesNum; octave++) {
            pyramid[octave] = generateGaussianScales(imageData, scalesNum, baseSigma, sigmaInterval);

            imageData = ImageDataUtil.resizeWithAveraging(
                    imageData,
                    imageData.length/downsamplingFactor,
                    imageData[0].length/downsamplingFactor
            );
        }

        return pyramid;
    }

    public int[][][][] buildDoGPyramid(int[][][][] gaussianPyramid) {
        int octavesNum = gaussianPyramid.length;
        int scalesNum = gaussianPyramid[0].length-1;
        int[][][][] pyramid = new int[octavesNum][scalesNum][][];

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
    private int[][] calculateDifferences(int[][] firstImage, int[][] secondImage) {
        int width = firstImage.length;
        int height = firstImage[0].length;
        int[][] result = new int[width][height];

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
    private int[][][] generateGaussianScales(int[][] imageData, int scalesNum, double baseSigma, double scaleInterval) {
        int numberOfScales = scalesNum + 3;
        int[][][] gaussianImages = new int[numberOfScales][imageData.length][imageData[0].length];
        double baseScale = baseSigma;

        for (int i = 0; i < numberOfScales; i++) {
            gaussianImages[i] = ImageDataUtil.gaussianBlur(imageData, baseScale, blurringSizeMultiplier);
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
