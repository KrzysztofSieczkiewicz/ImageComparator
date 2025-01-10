package org.example;

import org.example.comparators.HashComparator;
import org.example.config.HashComparatorConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    // TODO: Comparison should  be accessed via separate ComparatorObjects
    //  ORBComparator, HashComparator and DirectComparator, all should be able to accept Config and Images
    //  Excluded areas and should be accepted by "compare()" method

    public static void main(String[] args) throws IOException {
        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));

        long start = System.nanoTime();

        new HashComparator().comparePHash(actualImage, checkedImage);

        long end = System.nanoTime();
        System.out.println("Time taken to compare: " + (end-start) + "ns");
    }
}