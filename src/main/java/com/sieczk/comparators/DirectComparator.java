package com.sieczk.comparators;

import com.sieczk.analyzers.direct.*;
import com.sieczk.utils.ImageUtil;
import org.sieczk.analyzers.direct.*;
import com.sieczk.analyzers.direct.ExcludedAreas;

import java.awt.image.BufferedImage;


public class DirectComparator extends BaseComparator {
    private final DirectComparatorConfig config;

    private final int mismatchesPercentageThreshold;
    private final boolean enforceImageSize;
    private final boolean assureImageSize;


    public DirectComparator(DirectComparatorConfig config) {
        this.config = config;

        this.mismatchesPercentageThreshold = config.getMismatchedPercentageThreshold();
        this.enforceImageSize = config.isEnforceImageSize();
        this.assureImageSize = config.isAssureImageSize();
    }

    public DirectComparator() {
        this(new DirectComparatorConfig());
    }


    public DirectComparisonResult compare(BufferedImage actualImage, BufferedImage checkedImage) {
        return compare(
                actualImage,
                checkedImage,
                new ExcludedAreas()
        );
    }

    public DirectComparisonResult compare(BufferedImage baseImage, BufferedImage comparedImage, ExcludedAreas excludedAreas) {
        DirectAnalyzer analyzer = new DirectAnalyzer(config);

        BufferedImage checkedComparedImage = handleInputComparedImage(
                baseImage,
                comparedImage,
                enforceImageSize,
                assureImageSize
        );

        Mismatches mismatches = analyzer.compare(baseImage, checkedComparedImage);
        mismatches.excludeResults(excludedAreas);

        BufferedImage resultsImage = null;

        if(config.isProduceOutputImage() ) {
            ImageMarker imageMarker = new ImageMarker(config);
            resultsImage = ImageUtil.deepCopy(comparedImage);
            resultsImage = imageMarker.mark(resultsImage, mismatches);
            resultsImage = imageMarker.mark(resultsImage, excludedAreas);
        }

        boolean isMatching = isBelowMismatchThreshold(baseImage, mismatches);

        return new DirectComparisonResult(
                resultsImage,
                isMatching
        );
    }

    public DirectComparisonResult fastCompare(BufferedImage actualImage, BufferedImage checkedImage) {
        return fastCompare(
                actualImage,
                checkedImage,
                new ExcludedAreas()
        );
    }

    public DirectComparisonResult fastCompare(BufferedImage baseImage, BufferedImage comparedImage, ExcludedAreas excludedAreas) {
        DirectAnalyzer analyzer = new DirectAnalyzer(config);

        BufferedImage checkedComparedImage = handleInputComparedImage(
                baseImage,
                comparedImage,
                enforceImageSize,
                assureImageSize
        );

        Mismatches mismatches = analyzer.compareEveryNth(baseImage, checkedComparedImage);
        mismatches.excludeResults(excludedAreas);

        BufferedImage resultsImage = null;

        if(config.isProduceOutputImage() ) {
            ImageMarker imageMarker = new ImageMarker(config);
            resultsImage = ImageUtil.deepCopy(comparedImage);
            resultsImage = imageMarker.mark(resultsImage, mismatches);
            resultsImage = imageMarker.mark(resultsImage, excludedAreas);
        }

        boolean isMatching = isBelowMismatchThreshold(baseImage, mismatches);

        return new DirectComparisonResult(
                resultsImage,
                isMatching
        );
    }

    private boolean isBelowMismatchThreshold(BufferedImage actualImage, Mismatches mismatches) {
        int imageSize = actualImage.getWidth() * actualImage.getHeight();
        int mismatchesCount = mismatches.getMismatchesCount();

        return mismatchesPercentageThreshold > mismatchesCount*100/imageSize;
    }
}
