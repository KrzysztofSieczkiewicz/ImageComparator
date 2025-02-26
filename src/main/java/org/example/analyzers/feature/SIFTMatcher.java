package org.example.analyzers.feature;

import org.example.analyzers.feature.keypoints.FeatureMatch;
import org.example.analyzers.feature.keypoints.Keypoint;

import java.util.ArrayList;
import java.util.List;

public class SIFTMatcher {

    /**
     * Lowe's ratio threshold for match filtering
     */
    private final double loweRatio;

    public SIFTMatcher(double loweRatio) {
        this.loweRatio = loweRatio;
    }

    public ArrayList<FeatureMatch> matchKeypoints(List<Keypoint> keypoints1, List<Keypoint> keypoints2) {
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

            if (bestDistance < loweRatio * secondBestDistance) {
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
