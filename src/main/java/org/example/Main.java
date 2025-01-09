package org.example;

import org.example.analyzers.hash.AHashAnalyzer;
import org.example.analyzers.hash.DHashAnalyzer;
import org.example.analyzers.hash.PHashAnalyzer;
import org.example.analyzers.hash.WHashAnalyzer;
import org.example.config.HashComparatorConfig;
import org.example.utils.HashUtil;
import org.example.utils.ImageUtil;

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

        long end = System.nanoTime();
        System.out.println("Time taken to compare: " + (end-start) + "ns");
    }
}