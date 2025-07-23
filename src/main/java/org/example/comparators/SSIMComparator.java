package org.example.comparators;

import org.example.analyzers.ssim.SSIMAnalyzer;

import java.awt.image.BufferedImage;

public class SSIMComparator {
    private SSIMAnalyzer analyzer;

    public SSIMComparator(SSIMComparatorConfig config) {
        this.analyzer = new SSIMAnalyzer(config);
    }

    public SSIMComparator() {
        this(new SSIMComparatorConfig());
    }

    public double compare(BufferedImage actual, BufferedImage checked) {
        return analyzer.compareImages(actual, checked);
    }
}
