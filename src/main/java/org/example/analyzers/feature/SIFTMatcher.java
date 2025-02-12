package org.example.analyzers.feature;

import java.util.ArrayList;
import java.util.List;

public class SIFTMatcher {

    /**
     * Threshold above which keypoints won't be matched
     */
    private final float distanceThreshold;

    public SIFTMatcher(float distanceThreshold) {
        this.distanceThreshold = distanceThreshold;
    }

    public ArrayList<FeatureMatch> matchKeypoints(List<Keypoint> keypoints1, List<Keypoint> keypoints2, float ratioThreshold) {
        ArrayList<FeatureMatch> matches = new ArrayList<>();

        for (Keypoint keypoint1: keypoints1) {
            Keypoint bestMatch = null;
            double bestDistance = Double.MAX_VALUE;
            double secondBestDistance = Double.MAX_VALUE;

            for (Keypoint keypoint2: keypoints2) {
                double distance = calculateEuclideanDistance(
                        keypoint1.getDescriptor(),
                        keypoint2.getDescriptor() );

                if (distance < bestDistance) {
                    secondBestDistance = bestDistance;
                    bestDistance = distance;
                    bestMatch = keypoint2;
                } else if (distance < secondBestDistance) {
                    secondBestDistance = distance;
                }
            }

            if (bestDistance < ratioThreshold * secondBestDistance &&
                bestDistance < distanceThreshold) {
                matches.add( new FeatureMatch(keypoint1, bestMatch, bestDistance) );
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
