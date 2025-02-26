package org.example.analyzers.feature.keypoints;

public class FeatureMatch {

    private Keypoint keypoint1;
    private Keypoint keypoint2;
    private double distance;

    public FeatureMatch(Keypoint keypoint1, Keypoint keypoint2, double distance) {
        this.keypoint1 = keypoint1;
        this.keypoint2 = keypoint2;
        this.distance = distance;
    }

    public Keypoint getKeypoint1() {
        return keypoint1;
    }

    public Keypoint getKeypoint2() {
        return keypoint2;
    }

    public double getDistance() {
        return distance;
    }
}
