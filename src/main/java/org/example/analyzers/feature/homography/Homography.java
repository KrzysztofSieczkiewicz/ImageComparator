package org.example.analyzers.feature.homography;

import org.example.analyzers.feature.FeatureMatch;

import java.util.List;

public class Homography {

    private final double[][] matrix;
    private final List<FeatureMatch> inlierMatches;
    private final int totalMatchesNumber;

    public Homography(double[][] matrix, List<FeatureMatch> inlierMatches, int totalMatchesNumber) {
        this.matrix = matrix;
        this.inlierMatches = inlierMatches;
        this.totalMatchesNumber = totalMatchesNumber;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public List<FeatureMatch> getInlierMatches() {
        return inlierMatches;
    }

    public int getTotalMatchesNumber() {
        return totalMatchesNumber;
    }
}
