package org.example.config;

public class HashComparatorConfig {

    /**
     * To what largest dimension will the image be resized to before performing hashing.
     * Heavily impacts the performance
     */
    private int imageTargetSize = 64;

    /**
     * How much of the final hash will be used for comparison. Affects only wHash (value of 0.5 leaves ony most significant values).
     * The closer to 1, the more details are being taken into account.
     */
    private double hashSizeCoefficient = 0.5;

    public int getImageTargetSize() {
        return imageTargetSize;
    }

    public double getHashSizeCoefficient() {
        return hashSizeCoefficient;
    }

    public void setImageTargetSize(int imageTargetSize) {
        if (imageTargetSize <= 0) {
            throw new IllegalArgumentException("Image target size should be larger than 0. Cannot scale image to less than one pixel");
        }
        this.imageTargetSize = imageTargetSize;
    }

    public void setHashSizeCoefficient(double hashSizeCoefficient) {
        if (hashSizeCoefficient <= 0 || hashSizeCoefficient > 1) {
            throw new IllegalArgumentException("Hash size coefficient must be larger than 0 and less or equal to 1");
        }
        this.hashSizeCoefficient = hashSizeCoefficient;
    }
}
