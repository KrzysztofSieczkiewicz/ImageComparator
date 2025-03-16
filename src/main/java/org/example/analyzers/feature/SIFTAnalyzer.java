package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.analyzers.feature.homography.Homography;
import org.example.analyzers.feature.homography.HomographyEvaluator;
import org.example.analyzers.feature.keypoints.*;
import org.example.config.SIFTComparatorConfig;
import org.example.utils.MatrixUtil;
import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageDataUtil;

import javax.imageio.ImageIO;
import java.awt.*;
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
    private final int scalesNum;
    private final double downscalingFactor;

    private final int minImageSize;
    private final double baseSigma;
    private final double sigmaInterval;

    public SIFTAnalyzer() {
        this(new SIFTComparatorConfig());
    }

    public SIFTAnalyzer(SIFTComparatorConfig config) {
        this.inliersNumberRatio = config.getInliersNumberRatio();
        this.matchDistanceThreshold = config.getMatchDistanceThreshold();
        this.homographyMinDeterminantThreshold = config.getHomographyMinDeterminantThreshold();
        this.homographyMaxDeterminantThreshold = config.getHomographyMaxDeterminantThreshold();
        this.scalesNum = config.getNumberOfScales();
        this.minImageSize = config.getMinImageSize();

        this.downscalingFactor = config.getDownscalingFactor();
        this.baseSigma = config.getGaussianSigma();
        this.sigmaInterval = calculateSigmaIntervals();

        double loweRatio = config.getLoweRatio();

        this.pyramidProcessor = new PyramidProcessor(baseSigma, scalesNum, downscalingFactor, minImageSize);

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
        List<PixelPoint> candidates = new ArrayList<>();

        List<Keypoint> emptyList = new ArrayList<>();

        ImageAccessor accessor = ImageAccessor.create(image);
        float[][] imageData = ImageDataUtil.greyscaleToFloat( accessor.getPixels() );

        int octavesNum = pyramidProcessor.calculateNumberOfOctaves(imageData);
        for (int octave = 0; octave<octavesNum; octave++) {
            float[][][] blurred = new float[2][imageData.length][imageData[0].length];
            float[][][] dogs = new float[3][imageData.length][imageData[0].length];

            blurred[0] = ImageDataUtil.gaussianBlurGreyscaled(imageData, calculateSigma(octave,0));
            blurred[1] = ImageDataUtil.gaussianBlurGreyscaled(imageData, calculateSigma(octave,1));
            dogs[0] = ImageDataUtil.subtractImages(blurred[1], blurred[0]);

            if (octave == 0) {
                drawKeypoints(
                        dogs[0],
                        "FirstDog",
                        keypoints
                );
            }

            drawKeypoints(
                    blurred[0],
                    "Gaussian_o" +octave+ "_s0",
                    emptyList
            );
            drawKeypoints(
                    blurred[1],
                    "Gaussian_o" +octave+ "_s1",
                    emptyList
            );

            blurred[0] = blurred[1];
            blurred[1] = ImageDataUtil.gaussianBlurGreyscaled(imageData, calculateSigma(octave,2));
            dogs[1] = ImageDataUtil.subtractImages(blurred[1], blurred[0]);

            drawKeypoints(
                    blurred[1],
                    "Gaussian_o" +octave+ "_s2",
                    emptyList
            );

            List<PixelPoint> localPoints = new ArrayList<>();
            List<Keypoint> localKeypoints = new ArrayList<>();

            for (int scale=0; scale<scalesNum; scale++) {

                blurred[0] = blurred[1];
                blurred[1] = ImageDataUtil.gaussianBlurGreyscaled(imageData, calculateSigma(octave,scale+3));
                dogs[2] =  ImageDataUtil.subtractImages(blurred[1], blurred[0]);

                drawKeypoints(
                        blurred[1],
                        "Gaussian_o" +octave+ "_s" +(scale+3),
                        emptyList
                );

                OctaveSlice octaveSlice = new OctaveSlice(dogs,octave, scale,downscalingFactor);

                localPoints.addAll( keypointFinder.findKeypointCandidates(octaveSlice) );

                localKeypoints.addAll( keypointFinder.findKeypoints(octaveSlice) );

                keypoints.addAll( localKeypoints );
                candidates.addAll( localPoints );

                for (int k=0; k<2; k++) {
                    dogs[k] = dogs[k+1];
                }
            }
            System.out.println("Points in octave: " + octave + ": " + localPoints.size());
            System.out.println("Keypoints in octave: " + octave + ": " + localKeypoints.size());

            if (octave == 1) {
                int index = 0;
                for (PixelPoint point : localPoints) {
                    System.out.println(index + "- X:" + point.getX() + ", Y:" + point.getY());
                    index++;
                }
            }

            drawCandidates(
                    dogs[1],
                    "Candidates_o" + octave,
                    octave,
                    candidates
            );

            drawKeypoints(
                    dogs[1],
                    "Keypoints_o" + octave,
                    keypoints
            );

            imageData = ImageDataUtil.bicubicInterpolation(
                    imageData,
                    (int)(imageData.length / downscalingFactor),
                    (int)(imageData[0].length / downscalingFactor));
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

        if ( homography.getMatrix()==null || !ifHomographyValid(homography) ) {
            return null;
        }

        return homography;
    }

    /**
     * Checks if homography matrix determinant lies within thresholds and
     * checks if number of inliers is within acceptable ratio to the total matches number
     * @return true if homography is valid
     */
    private boolean ifHomographyValid(Homography homography) {
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

    /**
     * Calculates sigma multiplier which determines blurring progression within single octave
     * @return sigma multiplier
     */
    private double calculateSigmaIntervals() {
        double p = 1d / scalesNum;
        return Math.pow(2, p);
    }

    /**
     * Checks how many times image can be downsized with provided downscalingFactor and minimal image size
     * @return number of octaves that can be created
     */
    private int calculateNumberOfOctaves(float[][] imageData) {
        int currWidth = imageData.length;
        int currHeight = imageData[0].length;

        int octaves = 0;
        while( (currWidth/downscalingFactor >= minImageSize) &&
                (currHeight/downscalingFactor >= minImageSize) ) {
            octaves++;
            currWidth = (int)(currWidth / downscalingFactor);
            currHeight = (int)(currHeight / downscalingFactor);
        }

        return octaves;
    }

    private double calculateSigma(int octave, int scale) {
        double octaveFactor = Math.pow(Math.sqrt(downscalingFactor), octave);
        double scaleFactor = Math.pow(sigmaInterval, scale);
        return baseSigma * octaveFactor * scaleFactor;
    }



    public void drawCandidates(float[][] img, String outputName, int octaveIndex, List<PixelPoint> points) {
        BufferedImage output = new BufferedImage(img.length, img[0].length, BufferedImage.TYPE_INT_RGB);
        ImageAccessor accessor = ImageAccessor.create(output);
        Graphics2D g = output.createGraphics();

        float min = 0;
        float max = 0;
        for (int x=0; x<img.length; x++) {
            for (int y=0; y<img[0].length; y++) {
                if (img[x][y] > max) max = img[x][y];
                if (img[x][y] < min) min = img[x][y];
            }
        }

        for(int x=0; x<output.getWidth(); x++) {
            for(int y=0; y<output.getHeight(); y++) {
                float grayValue = (img[x][y] - min) * (255f / (max - min));
                int gray = Math.round( grayValue );
                accessor.setPixel(x, y, 255, gray, gray, gray);
            }
        }

        for (PixelPoint pp : points) {
            int x = pp.getX() ;//* (int) Math.pow(2, octaveIndex );
            int y = pp.getY() ;//* (int) Math.pow(2, octaveIndex );
            int radius = (int) (2 * octaveIndex); // Increase radius based on octave

            g.setColor(Color.RED);
            //g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
            g.fillOval(x - 1, y - 1, 2, 2); // Draw center dot
        }

        g.dispose();

        File file = new File("src/"+ outputName +".png");
        try {
            ImageIO.write(output, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void drawKeypoints(float[][] img, String outputName,  List<Keypoint> keypoints) {
        BufferedImage output = new BufferedImage(img.length, img[0].length, BufferedImage.TYPE_INT_RGB);
        ImageAccessor accessor = ImageAccessor.create(output);
        Graphics2D g = output.createGraphics();

        float min = 0;
        float max = 0;
        for (int x=0; x<img.length; x++) {
            for (int y=0; y<img[0].length; y++) {
                if (img[x][y] > max) max = img[x][y];
                if (img[x][y] < min) min = img[x][y];
            }
        }

        for(int x=0; x<output.getWidth(); x++) {
            for(int y=0; y<output.getHeight(); y++) {
                float grayValue = (img[x][y] - min) * (255f / (max - min));
                int gray = Math.round( grayValue );
                accessor.setPixel(x, y, 255, gray, gray, gray);
            }
        }

        for (Keypoint kp : keypoints) {
            int x = (int) kp.getSubPixelX() / (int) Math.pow(2, kp.getOctaveIndex());
            int y = (int) kp.getSubPixelY() / (int) Math.pow(2, kp.getOctaveIndex());
            int octave = kp.getOctaveIndex();
            int radius = (int) (8 * Math.pow(2, octave)); // Increase radius based on octave

            g.setColor(Color.RED);
            g.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
            g.fillOval(x - 2, y - 2, 4, 4); // Draw center dot
        }

        g.dispose();

        File file = new File("src/"+ outputName +".png");
        try {
            ImageIO.write(output, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}