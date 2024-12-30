package org.example.analyzers;

import org.example.config.DirectComparatorConfig;
import org.example.mismatchMarker.Mismatches;

import java.awt.image.BufferedImage;

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

    public boolean isBelowMismatchThreshold(BufferedImage actualImage, Mismatches mismatches) {
        int imageSize = actualImage.getWidth() * actualImage.getHeight();
        int mismatchesCount = mismatches.getMismatchesCount();

        return mismatchesPercentageThreshold > mismatchesCount*100/imageSize;
    }
}
