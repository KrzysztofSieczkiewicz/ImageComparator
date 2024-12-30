package org.example.comparators;

import org.example.analyzers.DirectAnalyzer;
import org.example.analyzers.ExcludedAreas;
import org.example.mismatchMarker.Mismatches;
import org.example.analyzers.ImageValidator;
import org.example.config.DirectComparatorConfig;
import org.example.mismatchMarker.ImageMarker;
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


    // TODO: ADD "should i write output file" flag to the config and make a condition to perform deep copy and mark results

    public DirectComparisonResult compare(BufferedImage actualImage, BufferedImage checkedImage, ExcludedAreas excludedAreas) {
        DirectAnalyzer analyzer = new DirectAnalyzer(config);
        ImageValidator imageValidator = new ImageValidator(config);
        ImageMarker imageMarker = new ImageMarker(config);

        // VALIDATE IMAGE SIZES
        imageValidator.enforceImagesSize(actualImage, checkedImage);

        // COMPARE
        Mismatches mismatches = analyzer.compare(actualImage, checkedImage);

        // EXCLUDE FROM MISMATCHES
        mismatches.excludeResults(excludedAreas);

        BufferedImage resultsImage = null;

        if(config.isProduceOutputImage() ) {
            // CREATE RESULT IMAGE
            resultsImage = ImageUtil.deepCopy(checkedImage);

            // MARK MISMATCHES ON THE RESULT IMAGE
            resultsImage = imageMarker.mark(resultsImage, mismatches);

            // MARK EXCLUDED AREAS ON THE RESULT IMAGE
            resultsImage = imageMarker.mark(resultsImage, excludedAreas);
        }

        // VALIDATE MISMATCH THRESHOLD
        boolean isMatching = imageValidator.isBelowMismatchThreshold(actualImage, mismatches);

        return new DirectComparisonResult(
                resultsImage,
                isMatching
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

        // EXCLUDE FROM MISMATCHES
        mismatches.excludeResults(excludedAreas);

        // CREATE RESULT IMAGE
        BufferedImage resultsImage = ImageUtil.deepCopy(checkedImage);

        // MARK MISMATCHES ON THE RESULT IMAGE
        resultsImage = imageMarker.mark(resultsImage, mismatches);

        // MARK EXCLUDED AREAS ON THE RESULT IMAGE
        resultsImage = imageMarker.mark(resultsImage, excludedAreas);

        // VALIDATE MISMATCH THRESHOLD
        boolean isMatching = imageValidator.isBelowMismatchThreshold(actualImage, mismatches);

        return new DirectComparisonResult(
                resultsImage,
                isMatching
        );
    }
}
