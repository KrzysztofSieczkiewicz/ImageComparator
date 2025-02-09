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
    public ArrayList<Keypoint> buildDoG(int[][] intImageData, int octavesNum, int dogScalesNum, int downsamplingFactor) {
        float[][] imageData = ImageDataUtil.convertToFloatMatrix(intImageData);
        double sigmaInterval = calculateScaleIntervals(dogScalesNum);

        ArrayList<Keypoint> keypoints = new ArrayList<>();

        double gaussianSigma;
        for (int octave=0; octave<octavesNum; octave++) {
            gaussianSigma = baseSigma;
            float[][] currentImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);
            gaussianSigma *= sigmaInterval;
            float[][] nextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);
            gaussianSigma *= sigmaInterval;
            float[][] nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);

            float[][] previousDoGScale = calculateImageDifferences(imageData, currentImage);
            float[][] currentDoGScale = calculateImageDifferences(currentImage, nextImage);
            float[][] nextDoGScale = calculateImageDifferences(nextImage, nextNextImage);

            for (int scale = 1; scale < dogScalesNum; scale++) {
                // Find features in the dogImage -> detector must be modified not to work with pyramid but single image instead;
                keypoints.addAll(keypointDetector.detectKeypoints(octave, previousDoGScale, currentDoGScale, nextDoGScale));

                // Move the gaussian images by one step
                gaussianSigma *= sigmaInterval;
                nextImage = nextNextImage;
                nextNextImage = ImageDataUtil.gaussianBlurGreyscaled(imageData, gaussianSigma);

                // Move the DoG by one step
                previousDoGScale = currentDoGScale;
                currentDoGScale = nextDoGScale;
                nextDoGScale = calculateImageDifferences(nextImage, nextNextImage);
            }

            // Downsize and only slightly blur between octaves
            imageData = ImageDataUtil.resizeWithAveraging(
                    imageData,
                    imageData.length/downsamplingFactor,
                    imageData[0].length/downsamplingFactor );
        }

        return keypoints;
    }

    /**
     * Internal method. Calculates difference between two greyscaled images.
     */
    private float[][] calculateImageDifferences(float[][] firstImage, float[][] secondImage) {
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
     * Calculates sigma multiplier which determines blurring progression within single octave
     * @param scalesAmount number of scales in an octave
     * @return sigma multiplier
     */
    private double calculateScaleIntervals(int scalesAmount) {
        double p = 1d/scalesAmount;
        return Math.pow(2, p);
    }
}
