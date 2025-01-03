package org.example;

import org.example.analyzers.ExcludedAreas;
import org.example.comparators.DirectComparator;
import org.example.comparators.DirectComparisonResult;
import org.example.mismatchMarker.Mismatches;
import org.example.config.DirectComparatorConfig;
import org.example.mismatchMarker.ImageMarker;
import org.example.analyzers.DirectAnalyzer;
import org.example.utils.ImageUtil;
import org.example.analyzers.ImageValidator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    // TODO: Comparison should  be accessed via separate ComparatorObjects
    //  ORBComparator, HashComparator and DirectComparator, all should be able to accept Config and Images
    //  Excluded areas and should be accepted by "compare()" method

    public static void main(String[] args) throws IOException {

        long globalStart = System.nanoTime();
        long start;
        long end;

        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));

        long localStart = System.nanoTime();

        DirectComparatorConfig directComparatorConfig = DirectComparatorConfig.defaultConfig();

        ExcludedAreas excludedAreas = new ExcludedAreas();
        excludedAreas.excludeArea(new Rectangle(0,0,50,1000));
        excludedAreas.includeArea(new Rectangle(5,5, 40,990));

        DirectComparisonResult result = new DirectComparator(directComparatorConfig).compare(actualImage, checkedImage, excludedAreas);

//        start = System.nanoTime();
//        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
//        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));
//        end = System.nanoTime();
//        System.out.println("Time taken to load images: " + (end-start) + " ns");
//
//        ImageMarker imageMarker = new ImageMarker(directComparatorConfig);
//        ImageValidator imageValidator = new ImageValidator(directComparatorConfig);
//        imageValidator.enforceImagesSize(actualImage, checkedImage);
//
//        start = System.nanoTime();
//        ExcludedAreas excludedAreas = new ExcludedAreas();
//        excludedAreas.excludeArea(new Rectangle(0,0,50,1000));
//        excludedAreas.includeArea(new Rectangle(5,5, 40,990));
//        excludedAreas.excludeArea(new Rectangle(250,250,50,50));
//        excludedAreas.excludeArea(new Rectangle(100,250,150,200));
//        excludedAreas.excludeArea(new Rectangle(250,100,200,150));
//        end = System.nanoTime();
//        System.out.println("Time taken to exclude areas: " + (end-start) + " ns");
//
//        start = System.nanoTime();
//        DirectAnalyzer comparator = new DirectAnalyzer(directComparatorConfig);
//        Mismatches mismatched = comparator.compare(actualImage, checkedImage);
//        end = System.nanoTime();
//        System.out.println("Time taken to compare: " + (end - start) + " ns");
//
//        start = System.nanoTime();
//        mismatched.excludeResults(excludedAreas);
//        end = System.nanoTime();
//        System.out.println("Time taken to exclude areas from mismatches: " + (end - start) + " ns");
//
//        start = System.nanoTime();
//        BufferedImage mismatchedImage = ImageUtil.deepCopy(checkedImage);
//        end = System.nanoTime();
//        System.out.println("Time taken to perform image deep copy: " + (end - start) + " ns");
//
//        start = System.nanoTime();
//        mismatchedImage = imageMarker.mark(mismatchedImage, mismatched);
//        end = System.nanoTime();
//        System.out.println("Time taken to mark mismatches: " + (end - start) + " ns");
//
//        start = System.nanoTime();
//        mismatchedImage = imageMarker.mark(mismatchedImage, excludedAreas);
//        end = System.nanoTime();
//        System.out.println("Time taken to mark excluded areas: " + (end - start) + " ns");
//
//        start = System.nanoTime();
//        imageValidator.isBelowMismatchThreshold(actualImage, mismatched);
//        end = System.nanoTime();
//        System.out.println("Time taken to check mismatches percentage: " + (end - start) + " ns");
//
//
//        start = System.nanoTime();
//        File outputFile = new File("rectangle_output.png");
//        ImageIO.write(mismatchedImage, "png", outputFile);
//        File actualFile = new File("actual_output.png");
//        ImageIO.write(actualImage, "png", actualFile);
//        File expectedFile = new File("checked_output.png");
//        ImageIO.write(checkedImage, "png", expectedFile);
//        end = System.nanoTime();
//        System.out.println("Time taken to write the file: " + (end - start) + " ns");
//
//        long globalEnd = System.nanoTime();
//        System.out.println("Time taken in total: " + (globalEnd - globalStart) + " ns");

        long localEnd = System.nanoTime();
        System.out.println("Time taken in total: " + (localEnd - localStart) + " ns");

        File outputFile = new File("rectangle_output.png");
        ImageIO.write(result.getResultImage(), "png", outputFile);
    }
}