package org.example.comparators;

import org.example.analyzers.direct.*;
import org.example.analyzers.ExcludedAreas;
import org.example.analyzers.ImageValidator;
import org.example.utils.ImageUtil;

import java.awt.image.BufferedImage;


public class DirectComparator extends BaseComparator {
    private final DirectComparatorConfig config;

    private final boolean enforceImageSize;
    private final boolean assureImageSize;


    public DirectComparator(DirectComparatorConfig config) {
        this.config = config;

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
        ImageValidator imageValidator = new ImageValidator(config);

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

        boolean isMatching = imageValidator.isBelowMismatchThreshold(baseImage, mismatches);

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
        ImageValidator imageValidator = new ImageValidator(config);

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

        boolean isMatching = imageValidator.isBelowMismatchThreshold(baseImage, mismatches);

        return new DirectComparisonResult(
                resultsImage,
                isMatching
        );
    }

}
