package org.example.analyzers.feature;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;;

import java.util.ArrayList;
import java.util.List;

public class SIFTMatcher {
    /**
     * Distance above which keypoint is not considered matching
     */
    private final double distanceThreshold = 50.0;

    public Graph<Keypoint, Double> matchKeypoints(List<Keypoint> keypoints1, List<Keypoint> keypoints2) {
        List<FeatureMatch> matches = new ArrayList<>();

        Graph<Keypoint, Double> matchingGraph = new SparseMultigraph<>();

        for (Keypoint k1 : keypoints1) {
            for (Keypoint k2 : keypoints2) {
                double distance = calculateEuclideanDistance(k1.getDescriptor(), k2.getDescriptor());
                if (distance < distanceThreshold) {
                    matchingGraph.addVertex(k1);
                    matchingGraph.addVertex(k2);
                    matchingGraph.addEdge(distance, k1, k2); // Distance is the edge
                }
            }
        }
        return matchingGraph;
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
