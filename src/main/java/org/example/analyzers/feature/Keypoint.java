package org.example.analyzers.feature;

public class Keypoint {
    private float x,y;
    private double[] gradientsVector;
    private double[][] hessianMatrix;

    public Keypoint(float x, float y, double[] gradientsVector, double[][] hessianMatrix) {
        this.x = x;
        this.y = y;
        this.gradientsVector = gradientsVector;
        this.hessianMatrix = hessianMatrix;
    }

    public Keypoint(KeypointCandidate candidate) {
        this.x = candidate.getX();
        this.y = candidate.getY();


    }

}
