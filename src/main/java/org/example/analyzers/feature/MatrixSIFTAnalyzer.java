package org.example.analyzers.feature;

import org.example.utils.accessor.ImageAccessor;
import org.example.utils.accessor.ImageDataUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MatrixSIFTAnalyzer {
    // TODO - CURRENT: test if DoG is handling edge cases and if there are aliasing issues with image downscaling

    // TODO: can be memory optimized by merging buildGaussianPyramid with buildDoGPyramid
    //  that'd work by discarding each gaussian images after necessary dog is computed

    /**
     * When to stop creating octaves
     */
    int minImageSizeThreshold = 128;

    /**
     * How many scales should be generated per one octave
     */
    int scalesAmount = 2;

    /**
     * Base sigma value determining initial image blur
     */
    double baseSigma = 1.6;

    /**
     * Determines Gaussian blurring kernel dimension (multiplier * sigma)
     */
    int blurringSizeMultiplier = 6;

    /**
     * Downsampling factor by which the image is reduced between octaves
     */
    int downsamplingFactor = 2;

    // TODO: CURRENT -> time to debug -
    //  go through each dog pyramid step and save all created images.
    //  then go through all keypoint steps and mark them on the image

    public void constructScaleSpace(int[][] imageData) {
        MatrixGaussianHelper gaussianHelper = new MatrixGaussianHelper(baseSigma, blurringSizeMultiplier);
        MatrixKeypointHelper keypointHelper = new MatrixKeypointHelper();

        // 0. Greyscale the image
        int[][] greyscaleImageData = ImageDataUtil.greyscale(imageData);


        {// [DEBUG]
            BufferedImage greyscaleImage = new BufferedImage(imageData.length, imageData[0].length, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < imageData[0].length; y++) {
                for (int x = 0; x < imageData.length; x++) {
                    // Get the grayscale value and set the pixel in the BufferedImage
                    int pixelValue = greyscaleImageData[x][y];
                    int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue; // Grayscale to RGB format
                    greyscaleImage.setRGB(x, y, rgb);
                }
            }
            File file = new File("src/1_1_ScaleSpace_Greyscale.png");
            try {
                ImageIO.write(greyscaleImage, "PNG", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }// [DEBUG]


        // 1. Octaves
        int octavesAmount = calculateOctavesNum(greyscaleImageData, minImageSizeThreshold, downsamplingFactor);

        // 2. Build Gaussian Pyramid
        float[][][][] gaussianPyramid = gaussianHelper.buildGaussianPyramid(greyscaleImageData, octavesAmount, scalesAmount, downsamplingFactor);


        {// [DEBUG]
            int octaveIndex = 1;
            for (float[][][] octave : gaussianPyramid) {
                int scaleIndex = 1;
                for (float[][] scale : octave) {
                    BufferedImage gaussianImage = new BufferedImage(scale.length, scale[0].length, BufferedImage.TYPE_INT_RGB);
                    for (int y = 0; y < scale[0].length; y++) {
                        for (int x = 0; x < scale.length; x++) {
                            // Get the grayscale value and set the pixel in the BufferedImage
                            int pixelValue = (int) scale[x][y];
                            int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue; // Grayscale to RGB format
                            gaussianImage.setRGB(x, y, rgb);
                        }
                    }
                    File file = new File("src/1_2_Gaussian_" + octaveIndex + "_" + scaleIndex + ".png");
                    try {
                        ImageIO.write(gaussianImage, "PNG", file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    scaleIndex++;
                }
                octaveIndex++;
            }
        }// [DEBUG]


        // 3. Build DoG pyramid
        float[][][][] dogPyramid = gaussianHelper.buildDoGPyramid(gaussianPyramid);


        {// [DEBUG]
            int octaveDoGIndex = 1;
            for (float[][][] octave : dogPyramid) {
                int scaleDoGIndex = 1;
                for (float[][] scale : octave) {
                    BufferedImage dogImage = new BufferedImage(scale.length, scale[0].length, BufferedImage.TYPE_INT_RGB);
                    for (int y = 0; y < scale[0].length; y++) {
                        for (int x = 0; x < scale.length; x++) {
                            // Get the grayscale value and set the pixel in the BufferedImage
                            int pixelValue = (int) scale[x][y];
                            int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue; // Grayscale to RGB format
                            dogImage.setRGB(x, y, rgb);
                        }
                    }
                    File file = new File("src/1_3_DoG_" + octaveDoGIndex + "_" + scaleDoGIndex + ".png");
                    try {
                        ImageIO.write(dogImage, "PNG", file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    scaleDoGIndex++;
                }
                octaveDoGIndex++;
            }
        }// [DEBUG]


        // 4. Find keypoints in the DoG pyramid
        keypointHelper.detectKeypoints(dogPyramid);

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
