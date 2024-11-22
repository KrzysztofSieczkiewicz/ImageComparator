package org.example;

import org.example.imageAccessor.ImageAccessor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
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

        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            imageAccessor.setPixel(0, 255, 255, 255, 255);
        }
        end = System.nanoTime();
        System.out.println("Time taken to write: " + (end - start) + " ns");


        // TODO - REPEAT AND SAVE TIMES, ADD "hasAlpha" CHECK AND COMPARE RESULTS - MAYBE SEPARATING ALPHA/NON-ALPHA HAS NO POINT
    }
}

