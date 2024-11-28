package org.example.comparator;

import org.example.utils.PHashUtil;

import java.awt.image.BufferedImage;

public class PHashComparator implements ImageComparator {

    @Override
    public void compare(BufferedImage actual, BufferedImage expected) {
        new PHashUtil().getImageHash(actual);
        new PHashUtil().getImageHash(expected);
    }
}
