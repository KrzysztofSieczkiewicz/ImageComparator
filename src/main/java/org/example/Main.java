package org.example;


import org.example.mismatchMarker.MismatchMarker;
import org.example.comparator.SimpleComparator;
import org.example.config.ColorSpace;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;

public class Main {
    public static void main(String[] args) throws IOException {
        long globalStart = System.nanoTime();

//        String imagePath = "src/image2.png";
//        long start = System.nanoTime();
//        ImageAccessor imageAccessor = ImageAccessor.readAndCreate(imagePath);
//        long end = System.nanoTime();
//        System.out.println("Time taken to read image: " + (end - start) + " ns");
//
//        start = System.nanoTime();
//        imageAccessor.getPixels();
//        end = System.nanoTime();
//        System.out.println("Time taken to read all pixels: " + (end - start) + " ns");

        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));
        SimpleComparator comparator = new SimpleComparator(ColorSpace.RGB, 5f);

        long start = System.nanoTime();
        boolean[][] mismatched = comparator.compare(actualImage, checkedImage);
        long end = System.nanoTime();
        System.out.println("Time taken to compare: " + (end - start) + " ns");

//        start = System.nanoTime();
//        MismatchManager mismatchManager = new MismatchManager(6);
//        List<Rectangle> groups = mismatchManager.groupMismatches(mismatched);
//        end = System.nanoTime();
//        System.out.println("Time taken to group mismatches: " + (end - start) + " ns");

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

