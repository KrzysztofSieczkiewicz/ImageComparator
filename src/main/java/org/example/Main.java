package org.example;

import org.example.analyzers.hash.PHashAnalyzer;
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
        HashComparatorConfig config = new HashComparatorConfig();
        new PHashAnalyzer(config).compare(actualImage, checkedImage);
        long end = System.nanoTime();

        System.out.println("Time taken to compare: " + (end-start) + "ns");

//        DirectComparatorConfig directComparatorConfig = DirectComparatorConfig.defaultConfig();
//
//        ExcludedAreas excludedAreas = new ExcludedAreas();
//        excludedAreas.excludeArea(new Rectangle(0,0,50,1000));
//        excludedAreas.includeArea(new Rectangle(5,5, 40,990));
//
//        DirectComparisonResult result = new DirectComparator(config).compare(actualImage, checkedImage, excludedAres);
//        File outputFile = new File("rectangle_output.png");
//        ImageIO.write(result.getResultImage(), "png", outputFile);
    }
}