package org.example.comparators;

import org.example.analyzers.ssim.SSIMAnalyzer;
import org.example.utils.ImageUtil;

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
        BufferedImage imageToCompare = comparedImage;

        boolean areImagesSameSize = checkImageSizes(baseImage,comparedImage);

        if(!areImagesSameSize) {
            if(enforceImageSize)
                throw new IllegalArgumentException("Compared image should have the same size");

            if(assureImageSize)
                imageToCompare = ImageUtil.resizeBilinear(comparedImage, baseImage.getWidth(), baseImage.getHeight());
        }

        return analyzer.calculateImagesSSIM(baseImage, imageToCompare);
    }

}
