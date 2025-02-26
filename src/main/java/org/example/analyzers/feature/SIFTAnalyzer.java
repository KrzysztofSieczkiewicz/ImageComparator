package org.example.analyzers.feature;

import org.example.analyzers.feature.homography.Homography;
import org.example.analyzers.feature.homography.HomographyEvaluator;
import org.example.analyzers.feature.keypoints.*;
import org.example.config.SIFTComparatorConfig;
import org.example.utils.MatrixUtil;
import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageDataUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SIFTAnalyzer {
    private final GaussianPyramidProcessor pyramidProcessor;
    private final KeypointFinder keypointFinder;
    private final SIFTMatcher siftMatcher;
    private final HomographyEvaluator homographyEvaluator;

    private final int matchDistanceThreshold;
    private final double inliersNumberRatio;
    private final double homographyMinDeterminantThreshold;
    private final double homographyMaxDeterminantThreshold;
    private final int dogsPerOctave;
    private final double downscalingFactor;


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

        this.pyramidProcessor = new GaussianPyramidProcessor(gaussianSigma, gaussianScalesPerOctave, downscalingFactor, minImageSize);
        this.keypointFinder = new KeypointFinder(contrastThreshold, offsetMagnitudeThreshold, edgeResponseRatio, neighbourWindowSize, localExtremeSearchRadius);
        this.siftMatcher = new SIFTMatcher(loweRatio);
        this.homographyEvaluator = new HomographyEvaluator();
    }

    // TODO: merge keypoint finder and keypoint refiner

    public List<Keypoint> findAllKeypoints(BufferedImage image) {
        ImageAccessor accessor = ImageAccessor.create(image);
        float[][] imageData = ImageDataUtil.greyscaleToFloat( accessor.getPixels() );

        int octaves = pyramidProcessor.calculateNumberOfOctaves(imageData);
        int scales = dogsPerOctave;

        List<Keypoint> keypoints = new ArrayList<>();

        float[][] currentImage = imageData;
        for (int octave=0; octave<octaves; octave++) {

            for (int scale=0; scale<scales; scale++) {
                float[][][] gaussians = pyramidProcessor.generateConsecutiveGaussians(currentImage, scale, 2);
                OctaveSlice octaveSlice = pyramidProcessor.processSingleDoGSlice(gaussians, octave);

                keypoints.addAll( keypointFinder.findKeypoints(octaveSlice) );
            }

            currentImage = ImageDataUtil.resizeWithAveraging(
                    currentImage,
                    (int)(currentImage.length / downscalingFactor),
                    (int)(currentImage[0].length / downscalingFactor));
        }

        return keypoints;
    }


    /**
     * Computes keypoint candidates and refines them into SIFT keypoints.
     * @return ArrayList<Keypoint> of found and refined keypoints
     */
    public ArrayList<Keypoint> findKeypoints(BufferedImage image) {
        ImageAccessor accessor = ImageAccessor.create(image);
        int[][] imageData = accessor.getPixels();

        float[][] greyscaleImageData = ImageDataUtil.greyscaleToFloat(imageData);

        return pyramidProcessor.findKeypoints(greyscaleImageData);
    }

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
}