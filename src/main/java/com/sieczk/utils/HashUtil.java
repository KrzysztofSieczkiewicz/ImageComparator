package com.sieczk.utils;

import java.util.BitSet;

public class HashUtil {


    /**
     * Calculates Hamming Distance - number of differing bits between two Hashes (XOR)
     *
     * @param hash1 hash representing first image
     * @param hash2 hash representing second image
     * @return number of differing bits
     */
    public static int calculateHammingDistance(BitSet hash1, BitSet hash2) {
        BitSet xorResult = (BitSet) hash1.clone();
        xorResult.xor(hash2);
        return xorResult.cardinality();
    }

    /**
     * Calculates similarity between images based on hammingDistance and compared bits amount
     *
     * @param hammingDistance hamming distance for compared hashes
     * @return normalized difference
     */
    public static double calculateSimilarity(int hammingDistance, int reducedImageSize) {
        return 1.0 - ((double) hammingDistance / (reducedImageSize*reducedImageSize));
    }

}