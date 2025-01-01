package org.example.comparators;

import org.example.config.HashComparatorConfig;

import java.awt.image.BufferedImage;

// TODO: hashing requires creating greyscale
//  when You're using accessor getPixels - check if they read proper greyscale intensity
public class HashComparator {
    private final HashComparatorConfig config;

    public HashComparator(HashComparatorConfig config) {
        this.config = config;
    }

    public HashComparator() {
        this.config = new HashComparatorConfig();
    }


    public void comparePHash(BufferedImage actual, BufferedImage checked) {

    }
}
