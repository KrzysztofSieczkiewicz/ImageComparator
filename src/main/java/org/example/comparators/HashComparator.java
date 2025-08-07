package org.example.comparators;

import org.example.analyzers.hash.AHashAnalyzer;
import org.example.analyzers.hash.DHashAnalyzer;
import org.example.analyzers.hash.PHashAnalyzer;
import org.example.analyzers.hash.WHashAnalyzer;
import org.example.utils.HashUtil;

import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.function.Function;


public class HashComparator extends BaseComparator{
    private final HashComparatorConfig config;

    private final boolean enforceImageSize;
    private final boolean assureImageSize;

    public HashComparator(HashComparatorConfig config) {
        this.enforceImageSize = config.isEnforceImageSize();
        this.assureImageSize = config.isAssureImageSize();

        this.config = config;

    }

    public HashComparator() {
        this(new HashComparatorConfig());
    }

    private double compare(BufferedImage baseImage, BufferedImage comparedImage, Function<BufferedImage, BitSet> hash) {
        BufferedImage checkedComparedImage = handleInputComparedImage(
                baseImage,
                comparedImage,
                enforceImageSize,
                assureImageSize
        );

        BitSet actualHash = hash.apply(baseImage);
        BitSet checkedHash = hash.apply(checkedComparedImage);
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
