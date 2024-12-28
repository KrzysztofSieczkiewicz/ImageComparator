package org.example.comparators;

import org.example.analyzers.BasicAnalyzer;
import org.example.analyzers.ExcludedAreas;
import org.example.analyzers.Mismatches;
import org.example.analyzers.ImageValidator;
import org.example.config.DirectCompareConfig;
import org.example.mismatchMarker.ImageMarker;
import org.example.utils.ImageUtil;

import java.awt.image.BufferedImage;


public class DirectComparator {
    private final DirectCompareConfig config;

    public DirectComparator(DirectCompareConfig config) {
        this.config = config;
    }

    public DirectComparator() {
        this.config = DirectCompareConfig.defaultConfig();
    }

    // TODO: MAKE a DirectComparisonResult class to return comparison result, resulting image and "statistics"
    public BufferedImage compare(BufferedImage actualImage, BufferedImage checkedImage, ExcludedAreas excludedAreas) {
        BasicAnalyzer analyzer = new BasicAnalyzer(config);
        ImageValidator imageValidator = new ImageValidator(config);
        ImageMarker imageMarker = new ImageMarker(config);

        // CREATE RESULT IMAGE
        BufferedImage resultsImage = ImageUtil.deepCopy(checkedImage);

        // VALIDATE IMAGE SIZES
        imageValidator.enforceImagesSize(actualImage, checkedImage);

        // COMPARE
        Mismatches mismatches = analyzer.compare(actualImage, checkedImage);

        // EXCLUDE FROM MISMATCHES
        mismatches.excludeResults(excludedAreas);

        // MARK MISMATCHES
        resultsImage = imageMarker.mark(resultsImage, mismatches);

        // MARK EXCLUDED AREAS
        resultsImage = imageMarker.mark(resultsImage, excludedAreas);

        // VALIDATE MISMATCH THRESHOLD
        imageValidator.isBelowMismatchThreshold(actualImage, mismatches);

        return resultsImage;
    }
}
