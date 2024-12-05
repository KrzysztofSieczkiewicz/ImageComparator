package org.example.comparator;

import java.awt.image.BufferedImage;

public interface ByPixelComparator {

    Mismatches compare(BufferedImage actual, BufferedImage expected);

}
