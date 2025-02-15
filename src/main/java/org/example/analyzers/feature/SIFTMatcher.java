package org.example.analyzers.feature;

import org.example.analyzers.feature.keypoints.FeatureMatch;
import org.example.analyzers.feature.keypoints.Keypoint;

import java.util.ArrayList;
import java.util.List;

public class SIFTMatcher {

    /**
     * Threshold above which keypoints won't be registered as matched
     */
    private final float distanceThreshold;

    /**
     * Lowe's ratio threshold for match filtering
     */
    private final float loweRatio;

    public SIFTMatcher(float distanceThreshold, float loweRatio) {
        this.loweRatio = loweRatio;
        this.distanceThreshold = distanceThreshold;
    }

    public ArrayList<FeatureMatch> matchKeypointsWithLimitedDistance(List<Keypoint> keypoints1, List<Keypoint> keypoints2) {
        return matchKeypointsGeneric(keypoints1, keypoints2, true);
    }

    public ArrayList<FeatureMatch> matchKeypoints(List<Keypoint> keypoints1, List<Keypoint> keypoints2) {
        return matchKeypointsGeneric(keypoints1, keypoints2, false);
    }

    private ArrayList<FeatureMatch> matchKeypointsGeneric(List<Keypoint> keypoints1, List<Keypoint> keypoints2, boolean useDistanceThreshold) {
        ArrayList<FeatureMatch> matches = new ArrayList<>();

        for (Keypoint keypoint1 : keypoints1) {
            Keypoint bestMatch = null;
            double bestDistance = Double.MAX_VALUE;
            double secondBestDistance = Double.MAX_VALUE;

            for (Keypoint keypoint2 : keypoints2) {
                double distance = calculateEuclideanDistance(
                        keypoint1.getDescriptor(),
                        keypoint2.getDescriptor());

                if (distance < bestDistance) {
                    secondBestDistance = bestDistance;
                    bestDistance = distance;
                    bestMatch = keypoint2;
                } else if (distance < secondBestDistance) {
                    secondBestDistance = distance;
                }
            }

            if (bestDistance < loweRatio * secondBestDistance &&
                    (!useDistanceThreshold || bestDistance < distanceThreshold)) {
                matches.add(new FeatureMatch(keypoint1, bestMatch, bestDistance));
            }
        }

        return matches;
    }

    private double calculateEuclideanDistance(float[] descriptor1, float[] descriptor2) {
        float sumSquaredDifferences = 0;
        for (int i = 0; i < descriptor1.length; i++) {
            float difference = descriptor1[i] - descriptor2[i];
            sumSquaredDifferences += difference * difference;
        }

        return Math.sqrt(sumSquaredDifferences);
    }
}
