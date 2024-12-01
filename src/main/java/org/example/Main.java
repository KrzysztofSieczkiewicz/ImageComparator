package org.example;

import org.example.accessor.ImageAccessor;
import org.example.analyzer.PixelGroup;
import org.example.comparator.PHashComparator;
import org.example.comparator.SimpleComparator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        long globalStart = System.nanoTime();

        String imagePath = "src/image2.png";

        long start = System.nanoTime();
        ImageAccessor imageAccessor = ImageAccessor.readAndCreate(imagePath);
        long end = System.nanoTime();
        System.out.println("Time taken to init: " + (end - start) + " ns");

        start = System.nanoTime();
        imageAccessor.getPixels();
        end = System.nanoTime();
        System.out.println("Time taken to read image: " + (end - start) + " ns");

        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            imageAccessor.getPixel(0);
        }
        end = System.nanoTime();
        System.out.println("Time taken to read: " + (end - start) + " ns");

//        start = System.nanoTime();
//        for (int i = 0; i < 100; i++) {
//            imageAccessor.setPixel(0, 255, 255, 255, 255);
//        }
//        end = System.nanoTime();
//        System.out.println("Time taken to write: " + (end - start) + " ns");

        BufferedImage actualImage = ImageIO.read(new File("src/image.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image2.png"));
        SimpleComparator comparator = new SimpleComparator();

        start = System.nanoTime();
        boolean[][] mismatched = comparator.compare(actualImage, checkedImage);
        for (int x=0; x<mismatched.length; x++) {
            for (int y=0; y<mismatched[0].length; y++) {
                if (mismatched[x][y]) System.out.println("Mismatched: " + x + " " + y);
            }
        }
        end = System.nanoTime();
        System.out.println("Time taken to compare: " + (end - start) + " ns");

        PixelGroup pixelGroup = new PixelGroup();
        start = System.nanoTime();
        pixelGroup.listConnectedMismatches(mismatched);
        end = System.nanoTime();
        System.out.println("Time taken to group mismatches: " + (end - start) + " ns");

        long globalEnd = System.nanoTime();
        System.out.println("Time taken in total: " + (globalEnd - globalStart) + " ns");
    }
}

