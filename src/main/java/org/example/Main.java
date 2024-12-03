package org.example;

import org.example.accessor.ImageAccessor;
import org.example.analyzer.PixelGroup;
import org.example.analyzer.RectangleDraw;
import org.example.comparator.PHashComparator;
import org.example.comparator.SimpleComparator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

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

//        start = System.nanoTime();
//        for (int i = 0; i < 100; i++) {
//            imageAccessor.getPixel(0);
//        }
//        end = System.nanoTime();
//        System.out.println("Time taken to read: " + (end - start) + " ns");
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
        end = System.nanoTime();
        System.out.println("Time taken to compare: " + (end - start) + " ns");

        start = System.nanoTime();
        PixelGroup pixelGroup = new PixelGroup(3);
        List<Rectangle> groups = pixelGroup.listConnectedMismatches(mismatched);
        end = System.nanoTime();
        System.out.println("Time taken to group mismatches: " + (end - start) + " ns");

//        BufferedImage grouped = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
//        Graphics2D g2d = grouped.createGraphics();
//        g2d.setColor(Color.BLUE);
//        groups.forEach( group -> {
//            int minX = group[0][0];
//            int minY = group[0][1];
//            int maxX = group[1][0];
//            int maxY = group[1][1];
//            g2d.drawRect(minX, minY, maxX-minX, maxY-minY);
//        });
//        g2d.dispose();
//        File outputFile = new File("rectangle_output.png");
//        ImageIO.write(grouped, "png", outputFile);


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
}

