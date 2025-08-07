package org.example.comparators;

public class HashComparatorConfig extends BaseComparatorConfig {

    /**
     * How much of the final wHash will be used for comparison.
     * The closer to 1, the finer details are being taken into account.
     * Value of 0.5 leaves only most significant values.
     */
    private double hashSizeCoefficient = 0.5;

    public double getHashSizeCoefficient() {
        return hashSizeCoefficient;
    }

    public HashComparatorConfig hashSizeCoefficient(double hashSizeCoefficient) {
        if (hashSizeCoefficient <= 0 || hashSizeCoefficient > 1)
            throw new IllegalArgumentException("Hash size coefficient must be larger than 0 and less or equal to 1");

        this.hashSizeCoefficient = hashSizeCoefficient;
        return this;
    }
}
