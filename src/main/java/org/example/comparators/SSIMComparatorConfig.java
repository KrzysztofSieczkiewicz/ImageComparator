package org.example.comparators;

public class SSIMComparatorConfig {

    // Empirical constants used for c1, c2 and c3 calculation
    private double k1 = 0.01;
    private double k2 = 0.03;

    // Gaussian kernel sigma (higher values increase weight with which window values are taken into account)
    private double sigma = 1.6;

    // Highest value that can be held by the pixel (by default 255 for 8bit images)
    private int dynamicRange = 255;

    // Exponents for each component (luminosity, contrast, structural respectively)
    private double alpha = 1.0;
    private double beta = 1.0;
    private double gamma = 1.0;

    // Width and height of the sliding window (how large group of pixels is to be compared at a single time)
    private int windowSize = 5;


    public double getK1() {
        return k1;
    }

    public void setK1(double k1) {
        this.k1 = k1;
    }

    public double getK2() {
        return k2;
    }

    public void setK2(double k2) {
        this.k2 = k2;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public int getDynamicRange() {
        return dynamicRange;
    }

    public void setDynamicRange(int dynamicRange) {
        this.dynamicRange = dynamicRange;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }
}
