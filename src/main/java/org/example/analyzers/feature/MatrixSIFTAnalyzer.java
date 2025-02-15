package org.example.analyzers.feature;

import org.example.analyzers.feature.homography.Homography;
import org.example.analyzers.feature.homography.HomographyEvaluator;
import org.example.utils.MatrixUtil;
import org.example.utils.accessor.ImageAccessor;
import org.example.utils.ImageDataUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MatrixSIFTAnalyzer {
    private final GaussianProcessor gaussianProcessor;
    private final SIFTMatcher siftMatcher;
    private final HomographyEvaluator homographyEvaluator;

    /**
     * Threshold value for homography determinant below which homography is rejected as invalid
     */
    private double homographyMinDeterminantThreshold = 0.1;

    /**
     * Threshold value for homography determinant above which homography is rejected as invalid
     */
    private double homographyMaxDeterminanThreshold = 10;

    /**
     * Ratio of inliers from the keypoints matches. If inliers to total matches ratio is below this number,
     * the homography is marked as invalid
     */
    private double inliersNumberRatio = 0.5;


    /**
     * How many Gaussian images should be generated per one octave
     */
    int imagesPerOctave = 5;

    /**
     * Base sigma value determining initial image blur
     */
    double baseSigma = 1.6;

    /**
     * Image size below which octaves won't be created
     */
    int minImageSizeThreshold = 32;

    /**
     * Downscaling factor by which the image is reduced between octaves
     */
    int downscalingFactor = 2;


    public MatrixSIFTAnalyzer() {
        this.gaussianProcessor = new GaussianProcessor(baseSigma, imagesPerOctave, downscalingFactor, minImageSizeThreshold);
        this.siftMatcher = new SIFTMatcher(150, 0.8f);
        this.homographyEvaluator = new HomographyEvaluator();
    }

    /**
     * Computes keypoints candidates and refines them into feature keypoints.
     * @return ArrayList of the Keypoints found
     */
    public ArrayList<Keypoint> findImageKeypoints(BufferedImage image) {
        ImageAccessor accessor = ImageAccessor.create(image);
        int[][] imageData = accessor.getPixels();

        float[][] greyscaleImageData = ImageDataUtil.greyscaleToFloat(imageData);

        return gaussianProcessor.processImageKeypoints(greyscaleImageData);
    }

    /**
     * Iterates through base keypoint list and searches for matching keypoints in checked list.
     * @return ArrayList of matches
     */
    public ArrayList<FeatureMatch> matchKeypoints(ArrayList<Keypoint> base, ArrayList<Keypoint> checked) {
        return siftMatcher.matchKeypoints(base, checked);
    }

    /**
     * Finds and matches keypoints from the images, then estimates and validates homography.
     * @return valid Homography or null if the Homography is invalid
     */
    public Homography matchImages(BufferedImage base, BufferedImage checked) {
        ArrayList<Keypoint> baseKeypoints = findImageKeypoints(base);
        ArrayList<Keypoint> checkedKeypoints = findImageKeypoints(checked);
        ArrayList<FeatureMatch> matches = matchKeypoints(baseKeypoints, checkedKeypoints);

        Homography homography = homographyEvaluator.estimateHomography(matches);

        if ( homography==null || validateHomography(homography) ) {
            return null;
        }

        return homography;
    }

    /**
     * Checks if homography matrix determinant lies within thresholds and
     * checks if number of inliers is within acceptable ratio to the total matches number
     * @return true if homography is valid
     */
    public boolean validateHomography(Homography homography) {
        int inliersNumber = homography.getInlierMatches().size();
        int totalMatchesNumber = homography.getTotalMatchesNumber();
        double determinant = MatrixUtil.get3x3MatrixDeterminant( homography.getMatrix() );
        double determinantAbs = Math.abs(determinant);

        if ( inliersNumber < totalMatchesNumber * inliersNumberRatio) {
            return false;
        }

        if (determinantAbs > homographyMaxDeterminanThreshold ||
            determinantAbs < homographyMinDeterminantThreshold) {
            return false;
        }

        return true;
    }
}