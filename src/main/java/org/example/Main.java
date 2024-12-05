package org.example;

import org.example.mismatchMarker.MismatchMarker;
import org.example.comparator.SimpleComparator;
import org.example.config.ColorSpace;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        long globalStart = System.nanoTime();


        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));

        long start = System.nanoTime();
        SimpleComparator comparator = new SimpleComparator(ColorSpace.RGB, 5f);
        boolean[][] mismatched = comparator.compare(actualImage, checkedImage);
        long end = System.nanoTime();
        System.out.println("Time taken to compare: " + (end - start) + " ns");

        start = System.nanoTime();
        BufferedImage mismatchedImage = MismatchMarker.mark(mismatched, checkedImage);
        end = System.nanoTime();
        System.out.println("Time taken to mark mismatches: " + (end - start) + " ns");

        start = System.nanoTime();
        File outputFile = new File("rectangle_output.png");
        ImageIO.write(mismatchedImage, "png", outputFile);
        end = System.nanoTime();
        System.out.println("Time taken to write the file: " + (end - start) + " ns");

        
        long globalEnd = System.nanoTime();
        System.out.println("Time taken in total: " + (globalEnd - globalStart) + " ns");
    }

}

