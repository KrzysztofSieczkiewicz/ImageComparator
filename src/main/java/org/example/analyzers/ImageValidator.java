package org.example.analyzers;

import org.example.analyzers.direct.DirectComparatorConfig;
import org.example.analyzers.direct.Mismatches;

import java.awt.image.BufferedImage;

// TODO
//  decide later - as for now this class seems to have potential to be shared across different comparators/analyzers
//  and that might be unwanted connection - if not - convert the class to util
public class ImageValidator {
    final int mismatchesPercentageThreshold;

    public ImageValidator(DirectComparatorConfig config) {
        this.mismatchesPercentageThreshold = config.getMismatchedPercentageThreshold();
    }
    public void enforceImagesSize(BufferedImage actual, BufferedImage checked) {
        if ((actual.getWidth() != checked.getWidth()) ||
                (actual.getHeight() != checked.getHeight())) {

            throw new RuntimeException("Images size mismatch");
        }
    }

    // TODO: move this to the DirectAnalyzer as private method?
    public boolean isBelowMismatchThreshold(BufferedImage actualImage, Mismatches mismatches) {
        int imageSize = actualImage.getWidth() * actualImage.getHeight();
        int mismatchesCount = mismatches.getMismatchesCount();

        return mismatchesPercentageThreshold > mismatchesCount*100/imageSize;
    }
}
