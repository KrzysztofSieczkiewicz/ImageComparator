package org.example.comparators;

import org.example.analyzers.feature.SIFTAnalyzer;
import org.example.analyzers.feature.homography.Homography;
import org.example.analyzers.feature.keypoints.FeatureMatch;
import org.example.analyzers.feature.keypoints.Keypoint;
import org.example.config.SIFTComparatorConfig;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SIFTComparator {
    private final SIFTComparatorConfig config;
    private final SIFTAnalyzer siftAnalyzer;

    public SIFTComparator(SIFTComparatorConfig config) {
        this.config = config;

        this.siftAnalyzer = new SIFTAnalyzer();
    }

    public SIFTComparator() {
        this.config = new SIFTComparatorConfig();

        this.siftAnalyzer = new SIFTAnalyzer(config);
    }

    /**
     * Finds and matches feature keypoints between the images
     * @return ArrayList<FeatureMatch> of matches with distances below threshold (if specified in config)
     */
    public ArrayList<FeatureMatch> findMatches(BufferedImage base, BufferedImage checked) {
        List<Keypoint> baseKeypoints = siftAnalyzer.findKeypoints(base);
        List<Keypoint> checkedKeypoints = siftAnalyzer.findKeypoints(checked);

        return siftAnalyzer.matchKeypoints(baseKeypoints, checkedKeypoints);
    }

    /**
     * Finds and matches keypoints between the images, then estimates and validates homography using RANSAC.
     * @return valid Homography or null if the Homography is invalid or couldn't be found
     */
    public Homography findHomography(BufferedImage base, BufferedImage checked) {
        return siftAnalyzer.evaluateAndValidateHomography( findMatches(base, checked) );
    }
}
