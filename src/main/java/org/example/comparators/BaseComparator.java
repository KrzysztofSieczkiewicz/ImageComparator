package org.example.comparators;

import org.example.utils.ImageUtil;

import java.awt.image.BufferedImage;

public abstract class BaseComparator {

    boolean checkImageSizes(BufferedImage baseImage, BufferedImage comparedImage) {
        return baseImage.getHeight() == comparedImage.getHeight() &&
                baseImage.getWidth() == comparedImage.getWidth();
    }

    BufferedImage handleInputComparedImage(
            BufferedImage baseImage,
            BufferedImage comparedImage,
            boolean isShouldEnforceSize,
            boolean isShouldAssureSize) {

        boolean areImagesSameSize = checkImageSizes(baseImage, comparedImage);

        if(!areImagesSameSize) {
            if(isShouldEnforceSize)
                throw new IllegalArgumentException("Compared image should have the same size");
            if(isShouldAssureSize)
                return ImageUtil.resizeBilinear(comparedImage, baseImage.getWidth(), baseImage.getHeight());
        }

        return comparedImage;
    }

}
