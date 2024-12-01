package org.example.comparator;

import java.awt.image.BufferedImage;

public interface ImageComparator {

    boolean[][] compare(BufferedImage actual, BufferedImage expected);

}
