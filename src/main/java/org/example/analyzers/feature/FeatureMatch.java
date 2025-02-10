package org.example.analyzers.feature;

public class FeatureMatch {

    private Keypoint keypoint1;
    private Keypoint keypoint2;
    public double distance;

    public FeatureMatch(Keypoint keypoint1, Keypoint keypoint2, double distance) {
        this.keypoint1 = keypoint1;
        this.keypoint2 = keypoint2;
        this.distance = distance;
    }
}
