package org.example;

import org.example.comparator.Mismatches;
import org.example.mismatchMarker.MismatchMarker;
import org.example.comparator.SimpleComparator;
import org.example.validator.Validator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        long globalStart = System.nanoTime();

        long start = System.nanoTime();
        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));
        Validator validator = new Validator();
        validator.enforceImagesSize(actualImage, checkedImage);
        long end = System.nanoTime();
        System.out.println("Time taken to read file from the disk: " + (end - start) + " ns");


        start = System.nanoTime();
        SimpleComparator comparator = new SimpleComparator();
        Mismatches mismatched = comparator.compare(actualImage, checkedImage);
        end = System.nanoTime();
        System.out.println("Time taken to compare: " + (end - start) + " ns");

        start = System.nanoTime();
        BufferedImage mismatchedImage = MismatchMarker.mark(mismatched, checkedImage);
        end = System.nanoTime();
        System.out.println("Time taken to mark mismatches: " + (end - start) + " ns");

        start = System.nanoTime();
        validator.isBelowMismatchThreshold(actualImage, mismatched);
        end = System.nanoTime();
        System.out.println("Time taken to check mismatches percentage: " + (end - start) + " ns");


        /*
        TODO:
        - HANDLE EXCLUDED AREAS - NOT EVERYTHING HAS TO BE COMPARED (PREFERABLY- FILL BOTH IMAGES WITH THE SAME COLOR IN THE EXCLUDED AREAS
        - ADD EXCLUDED AREAS MARKING IN THE FINAL IMAGE
        - ADD A NUMBER OF MISMATCHED PIXELS THRESHOLD TO OVERRIDE IF IMAGES ARE MISMATCHED OR NOT
         */


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

}

