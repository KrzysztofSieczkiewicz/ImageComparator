package org.example.analyzers.feature;

import org.example.analyzers.feature.homography.Homography;
import org.example.analyzers.feature.homography.HomographyEvaluator;
import org.example.analyzers.feature.keypoints.*;
import org.example.config.SIFTComparatorConfig;
import org.example.utils.MatrixUtil;
import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageDataUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SIFTAnalyzer {
    private final PyramidProcessor pyramidProcessor;
    private final KeypointFinder keypointFinder;
    private final SIFTMatcher siftMatcher;
    private final HomographyEvaluator homographyEvaluator;

    private final int matchDistanceThreshold;
    private final double inliersNumberRatio;
    private final double homographyMinDeterminantThreshold;
    private final double homographyMaxDeterminantThreshold;
    private final int dogsPerOctave;
    private final double downscalingFactor;

    private int scalesNum = 3;


    public SIFTAnalyzer() {
        this(new SIFTComparatorConfig());
    }

    public SIFTAnalyzer(SIFTComparatorConfig config) {
        this.inliersNumberRatio = config.getInliersNumberRatio();
        this.matchDistanceThreshold = config.getMatchDistanceThreshold();
        this.homographyMinDeterminantThreshold = config.getHomographyMinDeterminantThreshold();
        this.homographyMaxDeterminantThreshold = config.getHomographyMaxDeterminantThreshold();
        this.dogsPerOctave = config.getDogsPerOctave();

        this.downscalingFactor = config.getDownscalingFactor();

        double gaussianSigma = config.getGaussianSigma();
        int gaussianScalesPerOctave = config.getDogsPerOctave();
        int minImageSize = config.getMinImageSize();
        double loweRatio = config.getLoweRatio();

        this.pyramidProcessor = new PyramidProcessor(gaussianSigma, gaussianScalesPerOctave, downscalingFactor, minImageSize);

        this.keypointFinder = new KeypointFinder(
                config.getContrastThreshold(),
                config.getOffsetMagnitudeThreshold(),
                config.getEdgeResponseRatio(),
                config.getNeighbourWindowSize(),
                config.getLocalExtremeSearchRadius()
        );

        this.siftMatcher = new SIFTMatcher(loweRatio);
        this.homographyEvaluator = new HomographyEvaluator();
    }

    public List<Keypoint> findKeypoints(BufferedImage image) {
        List<Keypoint> keypoints = new ArrayList<>();

        // 1. Convert image to float matrix;
        ImageAccessor accessor = ImageAccessor.create(image);
        float[][] imageData = ImageDataUtil.greyscaleToFloat( accessor.getPixels() );

        saveImage(imageData, "Initial.png");

        // 2. Normalize the image to the [0-1] range
        for (int x=0; x<imageData.length; x++) {
            for (int y=0; y<imageData[0].length; y++) {
                imageData[x][y] /= 255;
            }
        }

        // 3. Check how many octaves and dog images will be created
        int octavesNum = pyramidProcessor.calculateNumberOfOctaves(imageData);

        for (int octave = 0; octave<octavesNum; octave++) {
            float[][][] gaussians = new float[2][imageData.length][imageData[0].length];
            float[][][] dogs = new float[3][imageData.length][imageData[0].length];

            gaussians[0] = imageData;
            gaussians[1] = pyramidProcessor.generateGaussian(imageData, 1);
            dogs[0] = ImageDataUtil.subtractImages(gaussians[1], gaussians[0]);

            saveImageWithNormalization(gaussians[0], "Gaussian1_o" + octave + "_s1" + ".png");
            saveImageWithNormalization(gaussians[1], "Gaussian2_o" + octave + "_s1" + ".png");

            gaussians[0] = gaussians[1];
            gaussians[1] = pyramidProcessor.generateGaussian(imageData, 2);
            dogs[1] = ImageDataUtil.subtractImages(gaussians[1], gaussians[0]);

            saveImageWithNormalization(gaussians[0], "Gaussian1_o" + octave + "_s2" + ".png");
            saveImageWithNormalization(gaussians[0], "Gaussian2_o" + octave + "_s2" + ".png");

            saveImageWithNormalization(dogs[0], "DoG_o" + octave + "_s1" + ".png");
            saveImageWithNormalization(dogs[1], "DoG_o" + octave + "_s2" + ".png");


            for (int scale = 3; scale< scalesNum + 3; scale++) {

                gaussians[0] = gaussians[1];
                gaussians[1] = pyramidProcessor.generateGaussian(imageData, scale);
                dogs[2] =  ImageDataUtil.subtractImages(gaussians[1], gaussians[0]);

                OctaveSlice octaveSlice = new OctaveSlice(
                        dogs,
                        octave,
                        downscalingFactor
                );

                saveImageWithNormalization(gaussians[0], "Gaussian1_o" + octave + "_s" + scale + ".png");
                saveImageWithNormalization(gaussians[1], "Gaussian2_o" + octave + "_s" + scale + ".png");

                saveImageWithNormalization(dogs[2], "DoG_o" + octave + "_s" + scale + ".png");


                keypoints.addAll( keypointFinder.findKeypoints(octaveSlice) );

                for (int k=0; k<2; k++) {
                    dogs[k] = dogs[k+1];
                }

            }

            saveImageWithNormalization(gaussians[1], "Gaussian_o" + octave + "_finalScale.png");

            imageData = ImageDataUtil.resizeWithAveraging(
                    gaussians[1],
                    (int)(imageData.length / downscalingFactor),
                    (int)(imageData[0].length / downscalingFactor));

            saveImageWithNormalization(imageData, "Gaussian_o" + octave + "_resized.png");
        }

        return keypoints;
    }

    // TODO : FIX AND FINISH
//    public List<Keypoint> findKeypoints(BufferedImage image) {
//        ImageAccessor accessor = ImageAccessor.create(image);
//        float[][] imageData = ImageDataUtil.greyscaleToFloat( accessor.getPixels() );
//
//        for (int x=0; x<imageData.length; x++) {
//            for (int y=0; y<imageData[0].length; y++) {
//                imageData[x][y] /= 255;
//            }
//        }
//
//        int octaves = pyramidProcessor.calculateNumberOfOctaves(imageData);
//
//        List<Keypoint> octaveKeypoints = new ArrayList<>();
//
//        float[][] currentImage = imageData;
//        for (int octave=0; octave<octaves; octave++) {
//
//            float[][][] gaussians = new float[2][currentImage.length][currentImage[0].length];
//            gaussians[0] = currentImage;
//            gaussians[1] = pyramidProcessor.generateGaussian(currentImage, 1);
//
//            float[][][] dogs = new float[dogsPerSlice][currentImage.length][currentImage[0].length];
//            for (int j=0; j<dogsPerSlice; j++) {
//                dogs[j] = pyramidProcessor.processSingleDoG(gaussians[0], gaussians[1] );
//                gaussians[0] = gaussians[1];
//                gaussians[1] = pyramidProcessor.generateGaussian(currentImage, j+1);
//            }
//
//            for (int scale=0; scale<dogsPerOctave; scale++) {
//
//                OctaveSlice octaveSlice = new OctaveSlice(
//                        dogs,
//                        octave,
//                        downscalingFactor
//                );
//
//                octaveKeypoints.addAll( keypointFinder.findKeypoints(octaveSlice) );
//
//                for (int k=0; k<dogsPerSlice-1; k++) {
//                    dogs[k] = dogs[k+1];
//                }
//                gaussians[0] = gaussians[1];
//                gaussians[1] = pyramidProcessor.generateGaussian(currentImage, (scale)+dogsPerSlice);
//                dogs[dogsPerSlice-1] = pyramidProcessor.processSingleDoG(gaussians[0], gaussians[1]);
//            }
//
//            currentImage = ImageDataUtil.resizeWithAveraging(
//                    currentImage,
//                    (int)(currentImage.length / downscalingFactor),
//                    (int)(currentImage[0].length / downscalingFactor));
//        }
//
//        return octaveKeypoints;
//    }




    /**
     * Iterates through base keypoint list and searches for matches in checked list.
     * @return ArrayList of matches
     */
    public ArrayList<FeatureMatch> matchKeypoints(List<Keypoint> base, List<Keypoint> checked) {
        ArrayList<FeatureMatch> matches = siftMatcher.matchKeypoints(base, checked);

        if (matchDistanceThreshold != 0) {
            matches.removeIf(match -> match.getDistance() >= matchDistanceThreshold);
        }
        return matches;
    }

    public Homography evaluateAndValidateHomography(ArrayList<FeatureMatch> matches) {
        Homography homography = homographyEvaluator.estimateHomography(matches);

        if ( homography.getMatrix()==null || !validateHomography(homography) ) {
            return null;
        }

        return homography;
    }

    /**
     * Checks if homography matrix determinant lies within thresholds and
     * checks if number of inliers is within acceptable ratio to the total matches number
     * @return true if homography is valid
     */
    private boolean validateHomography(Homography homography) {
        int inliersNumber = homography.getInlierMatches().size();
        int totalMatchesNumber = homography.getTotalMatchesNumber();
        double determinant = MatrixUtil.get3x3MatrixDeterminant( homography.getMatrix() );
        double determinantAbs = Math.abs(determinant);

        if ( inliersNumber < totalMatchesNumber * inliersNumberRatio) {
            return false;
        }

        if (determinantAbs > homographyMaxDeterminantThreshold ||
            determinantAbs < homographyMinDeterminantThreshold) {
            return false;
        }

        return true;
    }


    public static void saveImage(float[][] pixels, String outputPath) {
        int width = pixels.length;
        int height = pixels[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int gray = (int) pixels[x][y];
                gray = Math.max(0, Math.min(255, gray)); // Clamp between 0 and 255
                int rgb = (gray << 16) | (gray << 8) | gray; // Convert to grayscale RGB
                image.setRGB(x, y, rgb);
            }
        }

        File outputFile = new File(outputPath);
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveImageWithNormalization(float[][] pixels, String outputPath) {
        int width = pixels.length;
        int height = pixels[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float grayFloat = (pixels[x][y] + 1) * 128f; // Normalize if values are 0-1
                int gray = (int) grayFloat;
                gray = Math.max(0, Math.min(255, gray)); // Clamp between 0 and 255
                int rgb = (gray << 16) | (gray << 8) | gray; // Convert to grayscale RGB
                image.setRGB(x, y, rgb);
            }
        }

        File outputFile = new File(outputPath);
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}