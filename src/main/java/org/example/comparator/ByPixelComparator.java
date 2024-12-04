package org.example.comparator;

import java.awt.image.BufferedImage;

public interface ByPixelComparator {

    boolean[][] compare(BufferedImage actual, BufferedImage expected);

}
