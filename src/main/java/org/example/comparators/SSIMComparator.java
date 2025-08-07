package org.example.comparators;

import org.example.analyzers.ssim.SSIMAnalyzer;

import java.awt.image.BufferedImage;

public class SSIMComparator extends BaseComparator {
    private final SSIMAnalyzer analyzer;

    private final boolean enforceImageSize;
    private final boolean assureImageSize;

    public SSIMComparator(SSIMComparatorConfig config) {
        this.enforceImageSize = config.isEnforceImageSize();
        this.assureImageSize = config.isAssureImageSize();

        this.analyzer = new SSIMAnalyzer(config);
    }

    public SSIMComparator() {
        this(new SSIMComparatorConfig());
    }

    public double compare(BufferedImage baseImage, BufferedImage comparedImage) {
        BufferedImage checkedComparedImage = handleInputComparedImage(
                baseImage,
                comparedImage,
                enforceImageSize,
                assureImageSize
        );

        return analyzer.calculateImagesSSIM(baseImage, checkedComparedImage);
    }

}
