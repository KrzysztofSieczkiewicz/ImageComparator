package org.example;

import org.example.comparator.ExcludedAreas;
import org.example.comparator.Mismatches;
import org.example.mismatchMarker.MismatchMarker;
import org.example.comparator.SimpleComparator;
import org.example.utils.ImageUtil;
import org.example.validator.Validator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        long globalStart = System.nanoTime();
        long start;
        long end;


        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));
        Validator validator = new Validator();
        validator.enforceImagesSize(actualImage, checkedImage);

        // TODO: Dlaczego "Time taken to mark mismatches" jest wysoki niezależnie od liczby mismatchy (nawet 0)?

        start = System.nanoTime();
        ExcludedAreas excludedAreas = new ExcludedAreas();
        excludedAreas.excludeArea(new Rectangle(0,0,50,1000));
        //excludedAreas.includeArea(new Rectangle(5,5, 40,990));
        excludedAreas.excludeArea(new Rectangle(250,250,50,50));
        excludedAreas.excludeArea(new Rectangle(100,250,150,200));
        excludedAreas.excludeArea(new Rectangle(250,100,200,150));
        end = System.nanoTime();
        System.out.println("Time taken to exclude areas: " + (end-start) + " ns");

        start = System.nanoTime();
        SimpleComparator comparator = new SimpleComparator();
        Mismatches mismatched = comparator.compare(actualImage, checkedImage);
        end = System.nanoTime();
        System.out.println("Time taken to compare: " + (end - start) + " ns");

        start = System.nanoTime();
        BufferedImage mismatchedImage = ImageUtil.deepCopy(checkedImage);
        end = System.nanoTime();
        //System.out.println("Time taken to perform image deep copy: " + (end - start) + " ns");

        start = System.nanoTime();
        mismatched.excludeResults(excludedAreas.getPixels());
        end = System.nanoTime();
        System.out.println("Time taken to exclude areas from comparison results: " + (end - start) + " ns");

        start = System.nanoTime();
        mismatchedImage = MismatchMarker.markMismatches(mismatched, mismatchedImage);
        end = System.nanoTime();
        System.out.println("Time taken to mark mismatches: " + (end - start) + " ns");

        start = System.nanoTime();
        mismatchedImage = MismatchMarker.markExcluded(excludedAreas, mismatchedImage);
        end = System.nanoTime();
        System.out.println("Time taken to mark excluded areas: " + (end - start) + " ns");

        start = System.nanoTime();
        validator.isBelowMismatchThreshold(actualImage, mismatched);
        end = System.nanoTime();
        //System.out.println("Time taken to check mismatches percentage: " + (end - start) + " ns");


        start = System.nanoTime();
        File outputFile = new File("rectangle_output.png");
        ImageIO.write(mismatchedImage, "png", outputFile);
        File actualFile = new File("actual_output.png");
        ImageIO.write(actualImage, "png", actualFile);
        File expectedFile = new File("checked_output.png");
        ImageIO.write(checkedImage, "png", expectedFile);
        end = System.nanoTime();
        //System.out.println("Time taken to write the file: " + (end - start) + " ns");

        long globalEnd = System.nanoTime();
        System.out.println("Time taken in total: " + (globalEnd - globalStart) + " ns");
    }

}

