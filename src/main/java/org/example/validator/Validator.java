package org.example.validator;

import org.example.comparator.Mismatches;

import java.awt.image.BufferedImage;

public class Validator {

    // TODO: Get from global config
    int mismatchesPercentageThreshold = 5;

    // TODO: Provide custom exception
    public void enforceImagesSize(BufferedImage actual, BufferedImage checked) {
        if ((actual.getWidth() != checked.getWidth()) ||
                (actual.getHeight() != checked.getHeight())) {

            throw new RuntimeException("Images size mismatch");
        }
    }

    // TODO: Might require either assertion or some kind of exception
    public boolean isBelowMismatchThreshold(BufferedImage actualImage, Mismatches mismatches) {
        int imageSize = actualImage.getWidth() * actualImage.getHeight();
        int mismatchesCount = mismatches.getMismatchesCount();

        return mismatchesPercentageThreshold > mismatchesCount*100/imageSize;
    }
}
