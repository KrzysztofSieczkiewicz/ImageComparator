package org.example.analyzers.feature;

public class Keypoint {
    private float x,y;

    public Keypoint(float x, float y, double[][] hessianMatrix) {
        this.x = x;
        this.y = y;
    }

    public Keypoint(KeypointCandidate candidate) {
        this.x = candidate.getX();
        this.y = candidate.getY();


    }

}
