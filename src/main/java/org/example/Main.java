package org.example;

import edu.uci.ics.jung.graph.Graph;
import org.example.analyzers.feature.Keypoint;
import org.example.analyzers.feature.MatrixSIFTAnalyzer;
import org.example.analyzers.feature.SIFTMatcher;
import org.example.utils.ImageDataUtil;
import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    // TODO: test all ImageUtil method:
    //  check greyscale
    //  check resize
    //  check gaussian

    // TODO: Comparison should  be accessed via separate ComparatorObjects
    //  ORBComparator, HashComparator and DirectComparator, all should be able to accept Config and Images
    //  Excluded areas and should be accepted by "compare()" method

    public static void main(String[] args) throws IOException {
        BufferedImage testImage = ImageIO.read(new File("src/TestImage.jpg"));
        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));

//        testImage = ImageUtil.resize(testImage, 1024, 512);
//        File file = new File("src/baseImage.png");
//        try {
//            ImageIO.write(testImage, "PNG", file);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        ImageAccessor testImageAccessor = ImageAccessor.create(testImage);
//        int[][] pixels = testImageAccessor.getPixels();
//        float[][] floatPixels = ImageDataUtil.greyscaleToFloat(pixels);
//        pixels = ImageDataUtil.greyscale(pixels);
//        saveImage(pixels, "TestImageBaseInt.png");
//        saveImageFloat(floatPixels, "TestImageBaseFloat.png");


        long start = System.nanoTime();

        ArrayList<Keypoint> keypoints1 = new MatrixSIFTAnalyzer().computeImageKeypoints(testImage);
        ArrayList<Keypoint> keypoints2 = new MatrixSIFTAnalyzer().computeImageKeypoints(testImage);

        System.out.println(keypoints1.size());


//        Graph<Keypoint, Double> test = new SIFTMatcher().matchKeypoints(keypoints1, keypoints2);
//
//        test.getEdges().forEach( edge -> {
//                System.out.println("Edge: " + edge.toString());
//            }
//        );


        long end = System.nanoTime();
        System.out.println("Time taken: " + (end-start) + "ns");

        BufferedImage newImage = ImageUtil.deepCopy(testImage);
        ImageAccessor newAccessor = ImageAccessor.create(newImage);
        int[][] newPixels = newAccessor.getPixels();
        saveImageFloatWithKeypoints(ImageDataUtil.convertToFloatMatrix(newPixels), "PaintedKeypoints.png", keypoints1);

    }


    public static void saveImageFloat(float[][] imageData, String filePath) {
        if (imageData == null || imageData.length == 0 || imageData[0].length == 0) {
            throw new IllegalArgumentException("Invalid image data");
        }

        int width = imageData.length;
        int height = imageData[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelValue = (int) (imageData[x][y]);
                int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue;
                image.setRGB(x, y, rgb);
            }
        }

        try {
            File outputFile = new File(filePath);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveImageFloatWithKeypoints(float[][] imageData, String filePath, ArrayList<Keypoint> keypoints) {
        if (imageData == null || imageData.length == 0 || imageData[0].length == 0) {
            throw new IllegalArgumentException("Invalid image data");
        }

        int width = imageData.length;
        int height = imageData[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelValue = (int) (imageData[x][y]);
                int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue;
                image.setRGB(x, y, rgb);
            }
        }

        for (Keypoint k: keypoints) {
            if (k.getOctaveIndex() > 0) continue;
            if((int)k.getSubPixelX() > image.getWidth()-1 || (int)k.getSubPixelX() < 0) continue;
            if((int)k.getSubPixelY() > image.getHeight()-1 || (int)k.getSubPixelY() < 0) continue;

            int rgb = (250 << 16) | (0 << 8) | 0;

            int x = Math.round(k.getSubPixelX());
            int y = Math.round(k.getSubPixelY());

            try {
                image.setRGB(x, y, rgb);
            } catch (Exception ignore) {}
        }

        try {
            File outputFile = new File(filePath);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveImage(int[][] imageData, String filePath) {
        if (imageData == null || imageData.length == 0 || imageData[0].length == 0) {
            throw new IllegalArgumentException("Invalid image data");
        }

        int width = imageData.length;
        int height = imageData[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelValue = (imageData[x][y]);
                int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue;
                image.setRGB(x, y, rgb);
            }
        }

        try {
            File outputFile = new File(filePath);
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}