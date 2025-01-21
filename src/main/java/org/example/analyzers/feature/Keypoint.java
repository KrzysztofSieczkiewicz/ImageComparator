package org.example.analyzers.feature;

public class Keypoint {
    private double xBase, yBase;
    private int x,y;
    private int octave, scale;
    private double sigma;

    public Keypoint(int x, int y, int octave, int scale, int scalingFactor, double sigma) {
        this.x = x;
        this.y = y;
        this.octave = octave;
        this.scale = scale;
        this.sigma = sigma;

        int offsetFactor = (int) Math.pow(scalingFactor, octave);
        this.xBase = x * offsetFactor;
        this.yBase = y * offsetFactor;
    }

}
