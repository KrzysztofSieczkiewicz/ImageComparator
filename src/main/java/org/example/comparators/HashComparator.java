package org.example.comparators;

import org.example.analyzers.hash.AHashAnalyzer;
import org.example.analyzers.hash.DHashAnalyzer;
import org.example.analyzers.hash.PHashAnalyzer;
import org.example.analyzers.hash.WHashAnalyzer;
import org.example.analyzers.hash.HashComparatorConfig;
import org.example.utils.HashUtil;
import org.example.utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.function.Function;


public class HashComparator {
    private final HashComparatorConfig config;
    private final int imageTargetSize;

    public HashComparator(HashComparatorConfig config) {
        this.config = config;
        this.imageTargetSize = config.getImageTargetSize();
    }

    public HashComparator() {
        this(new HashComparatorConfig());
    }

    private double compare(
            BufferedImage actual,
            BufferedImage checked,
            Function<BufferedImage, BitSet> hash
    ) {
        BufferedImage actualImage;
        BufferedImage checkedImage;

        if (imageTargetSize != 0) {
            actualImage = ImageUtil.resize(actual, imageTargetSize, imageTargetSize);
            checkedImage = ImageUtil.resize(checked, imageTargetSize, imageTargetSize);
        } else {
            actualImage = actual;
            checkedImage = checked;
        }

        BitSet actualHash = hash.apply(actualImage);
        BitSet checkedHash = hash.apply(checkedImage);
        int hammingDistance = HashUtil.calculateHammingDistance(actualHash, checkedHash);

        return HashUtil.calculateSimilarity(hammingDistance, actualHash.size());
    }


    public double comparePHash(BufferedImage actual, BufferedImage checked) {
        PHashAnalyzer analyzer = new PHashAnalyzer();

        return compare(
                actual,
                checked,
                analyzer::pHash
        );
    }

    public double compareWHash(BufferedImage actual, BufferedImage checked) {
        WHashAnalyzer analyzer = new WHashAnalyzer(config.getHashSizeCoefficient());

        return compare(
                actual,
                checked,
                analyzer::wHash
        );
    }

    public double compareAHash(BufferedImage actual, BufferedImage checked) {
        AHashAnalyzer analyzer = new AHashAnalyzer();

        return compare(
                actual,
                checked,
                analyzer::aHash
        );
    }

    public double compareDHash(BufferedImage actual, BufferedImage checked) {
        DHashAnalyzer analyzer = new DHashAnalyzer();

        return compare(
                actual,
                checked,
                analyzer::dHash
        );
    }
}
