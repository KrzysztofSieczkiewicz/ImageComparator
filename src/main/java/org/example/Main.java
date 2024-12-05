package org.example;

import org.example.analyzer.BitSetMismatchManager;
import org.example.analyzer.MismatchManager;
import org.example.analyzer.RectangleDraw;
import org.example.comparator.SimpleComparator;
import org.example.config.ColorSpace;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;

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


        start = System.nanoTime();
        MismatchManager mismatchManager = new MismatchManager(6, 0);
        List<Rectangle> groups = mismatchManager.groupMismatches(mismatched);
        end = System.nanoTime();
        System.out.println("Time taken to group mismatches: " + (end - start) + " ns");

//        BitSet mismatchedSet = convertToBitSet(mismatched);
//        start = System.nanoTime();
//        BitSetMismatchManager mismatchManager = new BitSetMismatchManager(6);
//        List<Rectangle> groups = mismatchManager.groupMismatches(mismatchedSet, actualImage.getWidth(), actualImage.getHeight());
//        end = System.nanoTime();
//        System.out.println("Time taken to group mismatches: " + (end - start) + " ns");

        start = System.nanoTime();
        RectangleDraw rectangleDraw = new RectangleDraw();
        BufferedImage mismatchedImage = rectangleDraw.draw(groups, checkedImage);
        File outputFile = new File("rectangle_output.png");
        ImageIO.write(mismatchedImage, "png", outputFile);
        end = System.nanoTime();
        System.out.println("Time taken to draw rectangles: " + (end - start) + " ns");

        long globalEnd = System.nanoTime();
        System.out.println("Time taken in total: " + (globalEnd - globalStart) + " ns");
    }



    private static BitSet convertToBitSet(boolean[][] booleanArray) {
        int rows = booleanArray.length;
        int cols = booleanArray[0].length;
        BitSet bitSet = new BitSet(rows * cols);

        // Convert the 2D boolean array to a 1D BitSet
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int index = i * cols + j;  // Map (i, j) to a 1D index
                if (booleanArray[i][j]) {
                    bitSet.set(index);  // Set the bit if the value is true
                }
            }
        }

        return bitSet;
    }
}

