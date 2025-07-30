package org.example.comparators;

import java.awt.image.BufferedImage;

public abstract class BaseComparator {

    boolean checkImageSizes(BufferedImage baseImage, BufferedImage comparedImage) {
        return baseImage.getHeight() == comparedImage.getHeight() &&
                baseImage.getWidth() == comparedImage.getWidth();
    }

}
