package org.example;

import org.example.analyzers.ExcludedAreas;
import org.example.analyzers.Mismatches;
import org.example.config.DirectCompareConfig;
import org.example.mismatchMarker.MismatchMarker;
import org.example.analyzers.BasicAnalyzer;
import org.example.utils.ImageUtil;
import org.example.validator.Validator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    // TODO: Comparison should  be accessed via separate ComparatorObjects?
    // ORBComparator, HashComparator and DirectComparator, all should be able to accept Config and Images
    // Excluded areas and should be accepted by "compare()" method

    public static void main(String[] args) throws IOException {
        long globalStart = System.nanoTime();
        long start;
        long end;

        DirectCompareConfig directCompareConfig = DirectCompareConfig.defaultConfig();

        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));

        Validator validator = new Validator(directCompareConfig);
        validator.enforceImagesSize(actualImage, checkedImage);

        start = System.nanoTime();
        ExcludedAreas excludedAreas = new ExcludedAreas();
        excludedAreas.excludeArea(new Rectangle(0,0,50,1000));
        excludedAreas.includeArea(new Rectangle(5,5, 40,990));
        excludedAreas.excludeArea(new Rectangle(250,250,50,50));
        excludedAreas.excludeArea(new Rectangle(100,250,150,200));
        excludedAreas.excludeArea(new Rectangle(250,100,200,150));
        end = System.nanoTime();
        System.out.println("Time taken to exclude areas: " + (end-start) + " ns");

        start = System.nanoTime();
        BasicAnalyzer comparator = new BasicAnalyzer(directCompareConfig);
        Mismatches mismatched = comparator.compare(actualImage, checkedImage);
        end = System.nanoTime();
        System.out.println("Time taken to compare: " + (end - start) + " ns");

        start = System.nanoTime();
        mismatched.excludePixels(excludedAreas);
        end = System.nanoTime();
        System.out.println("Time taken to exclude areas from mismatches: " + (end - start) + " ns");

        start = System.nanoTime();
        BufferedImage mismatchedImage = ImageUtil.deepCopy(checkedImage);
        end = System.nanoTime();
        System.out.println("Time taken to perform image deep copy: " + (end - start) + " ns");

        start = System.nanoTime();
        mismatchedImage = MismatchMarker.markMismatches(mismatched, mismatchedImage, directCompareConfig);
        end = System.nanoTime();
        System.out.println("Time taken to mark mismatches: " + (end - start) + " ns");

        start = System.nanoTime();
        mismatchedImage = MismatchMarker.markExcluded(excludedAreas, mismatchedImage, directCompareConfig);
        end = System.nanoTime();
        System.out.println("Time taken to mark excluded areas: " + (end - start) + " ns");

        start = System.nanoTime();
        validator.isBelowMismatchThreshold(actualImage, mismatched);
        end = System.nanoTime();
        System.out.println("Time taken to check mismatches percentage: " + (end - start) + " ns");


        start = System.nanoTime();
        File outputFile = new File("rectangle_output.png");
        ImageIO.write(mismatchedImage, "png", outputFile);
        File actualFile = new File("actual_output.png");
        ImageIO.write(actualImage, "png", actualFile);
        File expectedFile = new File("checked_output.png");
        ImageIO.write(checkedImage, "png", expectedFile);
        end = System.nanoTime();
        System.out.println("Time taken to write the file: " + (end - start) + " ns");

        long globalEnd = System.nanoTime();
        System.out.println("Time taken in total: " + (globalEnd - globalStart) + " ns");
    }


    public BufferedImage directCompare(BufferedImage actualImage, BufferedImage checkedImage) {
        DirectCompareConfig directCompareConfig = DirectCompareConfig.defaultConfig();

        ExcludedAreas excludedAreas = new ExcludedAreas();
        BasicAnalyzer comparator = new BasicAnalyzer(directCompareConfig);
        Validator validator = new Validator(directCompareConfig);

        // VALIDATE IMAGE SIZES
        validator.enforceImagesSize(actualImage, checkedImage);

        // COPY ACTUAL IMAGE
        BufferedImage mismatchedImage = ImageUtil.deepCopy(checkedImage);

        // PERFORM COMPARISON
        Mismatches mismatches = comparator.compare(actualImage, checkedImage);

        // EXCLUDE FROM MISMATCHES
        mismatches.excludePixels(excludedAreas);

        // MARK MISMATCHES
        mismatchedImage = MismatchMarker.markMismatches(mismatches, mismatchedImage, directCompareConfig);

        // MARK EXCLUDED AREAS
        mismatchedImage = MismatchMarker.markExcluded(excludedAreas, mismatchedImage, directCompareConfig);

        // VALIDATE MISMATCH THRESHOLD
        validator.isBelowMismatchThreshold(actualImage, mismatches);

        return mismatchedImage;
    }
}