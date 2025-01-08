package org.example.comparators;

import org.example.analyzers.hash.PHashAnalyzer;
import org.example.config.HashComparatorConfig;
import org.example.utils.HashUtil;
import org.example.utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.BitSet;


public class HashComparator {
    private final HashComparatorConfig config;

    int imageTargetSize;

    public HashComparator(HashComparatorConfig config) {
        this.config = config;

        this.imageTargetSize = config.getImageTargetSize();
    }

    public HashComparator() {
        this.config = new HashComparatorConfig();
    }

    private void compare(BufferedImage actual, BufferedImage checked) {
        BufferedImage actualImage;
        BufferedImage checkedImage;

        if (imageTargetSize != 0) {
            actualImage = ImageUtil.resize(actual, imageTargetSize, imageTargetSize);
            checkedImage = ImageUtil.resize(checked, imageTargetSize, imageTargetSize);
        } else {
            actualImage = actual;
            checkedImage = checked;
        }

        
    }


    public double comparePHash(BufferedImage actual, BufferedImage checked) {
        PHashAnalyzer analyzer = new PHashAnalyzer(config);

        BitSet actualHash = analyzer.pHash(actual);
        BitSet checkedHash = analyzer.pHash(checked);
        int hammingDistance = HashUtil.calculateHammingDistance(actualHash, checkedHash);

        return HashUtil.calculateSimilarity(hammingDistance, actualHash.size());
    }
}
