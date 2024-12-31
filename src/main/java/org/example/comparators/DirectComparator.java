package org.example.comparators;

import org.example.analyzers.direct.DirectAnalyzer;
import org.example.analyzers.ExcludedAreas;
import org.example.analyzers.direct.Mismatches;
import org.example.analyzers.ImageValidator;
import org.example.config.DirectComparatorConfig;
import org.example.analyzers.direct.ImageMarker;
import org.example.utils.ImageUtil;

import java.awt.image.BufferedImage;


public class DirectComparator {
    private final DirectComparatorConfig config;


    public DirectComparator(DirectComparatorConfig config) {
        this.config = config;
    }

    public DirectComparator() {
        this.config = DirectComparatorConfig.defaultConfig();
    }


    public DirectComparisonResult compare(BufferedImage actualImage, BufferedImage checkedImage) {
        return compare(
                actualImage,
                checkedImage,
                new ExcludedAreas()
        );
    }

    public DirectComparisonResult compare(BufferedImage actualImage, BufferedImage checkedImage, ExcludedAreas excludedAreas) {
        DirectAnalyzer analyzer = new DirectAnalyzer(config);
        ImageValidator imageValidator = new ImageValidator(config);
        ImageMarker imageMarker = new ImageMarker(config);

        // VALIDATE IMAGE SIZES
        imageValidator.enforceImagesSize(actualImage, checkedImage);

        // COMPARE
        Mismatches mismatches = analyzer.compare(actualImage, checkedImage);
        mismatches.excludeResults(excludedAreas);

        BufferedImage resultsImage = null;

        if(config.isProduceOutputImage() ) {
            resultsImage = ImageUtil.deepCopy(checkedImage);
            resultsImage = imageMarker.mark(resultsImage, mismatches);
            resultsImage = imageMarker.mark(resultsImage, excludedAreas);
        }

        // VALIDATE MISMATCH THRESHOLD
        boolean isMatching = imageValidator.isBelowMismatchThreshold(actualImage, mismatches);

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

    public DirectComparisonResult fastCompare(BufferedImage actualImage, BufferedImage checkedImage, ExcludedAreas excludedAreas) {
        DirectAnalyzer analyzer = new DirectAnalyzer(config);
        ImageValidator imageValidator = new ImageValidator(config);
        ImageMarker imageMarker = new ImageMarker(config);

        // VALIDATE IMAGE SIZES
        imageValidator.enforceImagesSize(actualImage, checkedImage);

        // COMPARE
        Mismatches mismatches = analyzer.compareEveryNth(actualImage, checkedImage);
        mismatches.excludeResults(excludedAreas);

        BufferedImage resultsImage = null;

        if(config.isProduceOutputImage() ) {
            resultsImage = ImageUtil.deepCopy(checkedImage);
            resultsImage = imageMarker.mark(resultsImage, mismatches);
            resultsImage = imageMarker.mark(resultsImage, excludedAreas);
        }

        // VALIDATE MISMATCH THRESHOLD
        boolean isMatching = imageValidator.isBelowMismatchThreshold(actualImage, mismatches);

        return new DirectComparisonResult(
                resultsImage,
                isMatching
        );
    }
}
