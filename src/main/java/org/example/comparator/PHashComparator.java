package org.example.comparator;

import org.example.utils.PHashUtil;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class PHashComparator implements ImageComparator {

    @Override
    public void compare(BufferedImage actual, BufferedImage expected) {
        BitSet hash1 = new PHashUtil().getImageHash(actual);
        BitSet hash2 = new PHashUtil().getImageHash(expected);

        int hammingDistance = PHashUtil.calculateHammingDistance(hash1, hash2);
        double similarity = PHashUtil.calculateSimilarity(hammingDistance);
//        System.out.println(
//                "Distance: " + hammingDistance
//        );
//
//        System.out.println(
//                "Similarity: " + similarity
//        );
    }
}
