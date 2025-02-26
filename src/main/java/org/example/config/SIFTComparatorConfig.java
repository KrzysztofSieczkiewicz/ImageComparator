package org.example.config;

public class SIFTComparatorConfig {

    /**
     * Max distance between matched keypoints above which keypoints are no longer considered matched.
     * If set to 0 no limit will be applied.
     */
    private int matchDistanceThreshold = 150;

    /**
     * Threshold of the ratio between inliers from the keypoints matches and total number of matches.
     * If number of inliers divided by the number of matches is below this number,
     * the homography is considered unreliable and discarded
     */
    private double inliersNumberRatio = 0.5;

    /**
     * Threshold value for homography determinant below which homography is rejected as invalid
     */
    private double homographyMinDeterminantThreshold = 0.1;

    /**
     * Threshold value for homography determinant above which homography is rejected as invalid
     */
    private double homographyMaxDeterminantThreshold = 10;

    /**
     * Base sigma value determining initial image blur
     */
    private double gaussianSigma = 1.6;

    /**
     * How many Gaussian images should be generated per one octave
     */
    private int dogsPerOctave = 5;

    /**
     * Factor by which images will be downscaled between octaves in the gaussian pyramid
     */
    private int downscalingFactor = 2;

    /**
     * Smallest image dimension (in pixels) below which next octave won't be created
     */
    int minImageSize = 32;

    /**
     * Used in keypoint matching. Ratio between the best found distance and the second-best distance.
     * Reduces amount of random matches found in random parts of the image.
     */
    private double loweRatio = 0.8;



    /* KEYPOINT FINDER */
    /**
     * Contrast threshold below which keypoint will be discarded as noise.
     * Usually between 0.01 and 0.04
     */
    private final float contrastThreshold = 0.04f;

    /**
     * Offset magnitude threshold above which keypoint will be discarded.
     * Usually around 0.55
     */
    private final float offsetMagnitudeThreshold = 0.55f;

    /**
     * Hessian eigenvalues ratio below which keypoint will be discarded as edge keypoint.
     * Usually between 5 and 20
     */
    private final float edgeResponseRatio = 10;

    /**
     * How large should the window of neighbours around keypoint be. Will be scaled by each octave
     */
    private final int neighbourWindowSize = 16;

    /**
     * How large should the window for local extreme search be around each point.
     */
    private final int localExtremeSearchRadius = 1;


    public int getMatchDistanceThreshold() {
        return matchDistanceThreshold;
    }

    public double getInliersNumberRatio() {
        return inliersNumberRatio;
    }

    public double getHomographyMaxDeterminantThreshold() {
        return homographyMaxDeterminantThreshold;
    }

    public double getHomographyMinDeterminantThreshold() {
        return homographyMinDeterminantThreshold;
    }

    public double getGaussianSigma() {
        return gaussianSigma;
    }

    public int getDogsPerOctave() {
        return dogsPerOctave;
    }

    public int getDownscalingFactor() {
        return downscalingFactor;
    }

    public int getMinImageSize() {
        return minImageSize;
    }

    public double getLoweRatio() {
        return loweRatio;
    }
}
