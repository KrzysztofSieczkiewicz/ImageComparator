package org.example.comparators;

public class SSIMComparatorConfig extends BaseComparatorConfig {

    /**
     * Empirical constant k1 used for c1,c2,c3 calculation
     */
    private double k1 = 0.01;
    /**
     * Empirical constant k2 used for c1,c2,c3 calculation
     */
    private double k2 = 0.03;

    /**
     * Gaussian kernel sigma value. Bigger values increase neighbouring pixels importance
     */
    private double sigma = 1.6;

    /**
     * Difference between max-min pixel value. 255 for 8bit images
     */
    private int dynamicRange = 255;

    /**
     * Luminosity exponent - affects how important component value is in the SSIM value
     */
    private double alpha = 1.0;
    /**
     * Contrast exponent - affects how important component value is in the SSIM value
     */
    private double beta = 1.0;
    /**
     * Structural exponent - affects how important component value is in the SSIM value
     */
    private double gamma = 1.0;

    /**
     * Size of window rectangle. How many pixels are taken into account during comparison.
     * For most use cases avoid larger than 11
     */
    private int windowSize = 5;


    public double getK1() {
        return k1;
    }

    public SSIMComparatorConfig k1(double k1) {
        this.k1 = k1;
        return this;
    }

    public double getK2() {
        return k2;
    }

    public SSIMComparatorConfig k2(double k2) {
        this.k2 = k2;
        return this;
    }

    public double getSigma() {
        return sigma;
    }

    public SSIMComparatorConfig sigma(double sigma) {
        this.sigma = sigma;
        return this;
    }

    public int getDynamicRange() {
        return dynamicRange;
    }

    public SSIMComparatorConfig dynamicRange(int dynamicRange) {
        this.dynamicRange = dynamicRange;
        return this;
    }

    public double getAlpha() {
        return alpha;
    }

    public SSIMComparatorConfig alpha(double alpha) {
        this.alpha = alpha;
        return this;
    }

    public double getBeta() {
        return beta;
    }

    public SSIMComparatorConfig beta(double beta) {
        this.beta = beta;
        return this;
    }

    public double getGamma() {
        return gamma;
    }

    public SSIMComparatorConfig gamma(double gamma) {
        this.gamma = gamma;
        return this;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public SSIMComparatorConfig windowSize(int windowSize) {
        this.windowSize = windowSize;
        return this;
    }
}
