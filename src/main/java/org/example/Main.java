package org.example;

import org.example.analyzers.feature.*;
import org.example.utils.ImageDataUtil;
import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    // TODO: test all ImageUtil method:
    //  check greyscale
    //  check resize
    //  check gaussian

    public static void main(String[] args) throws IOException {
        BufferedImage testImage = ImageIO.read(new File("src/TestImage.jpg"));
        BufferedImage actualImage = ImageIO.read(new File("src/image3.png"));
        BufferedImage checkedImage = ImageIO.read(new File("src/image4.png"));

        // TODO: clean this up - it starts to get extremely cluttered
        //  also - add a config param for SIFTMatcher to discard matches above certain distance
        //  thoroughly check how util methods are performing and if they work accurately
        //  then, check all the methods that process images for SIFT and especially blur/subtract images
        //  it may strongly affect comparison stability

        testImage = actualImage;

        BufferedImage testImage2 = testImage;
        //BufferedImage testImage2 = ImageUtil.resize(testImage, 924, 512);
        testImage2 = ImageUtil.greyscale(testImage2);
        File file = new File("src/baseImage.png");
        try {
            ImageIO.write(testImage2, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        ImageAccessor testImageAccessor = ImageAccessor.create(testImage);
//        int[][] pixels = testImageAccessor.getPixels();
//        float[][] floatPixels = ImageDataUtil.greyscaleToFloat(pixels);
//        pixels = ImageDataUtil.greyscale(pixels);
//        saveImage(pixels, "TestImageBaseInt.png");
//        saveImageFloat(floatPixels, "TestImageBaseFloat.png");


        long start = System.nanoTime();

        ArrayList<Keypoint> keypoints1 = new MatrixSIFTAnalyzer().computeImageKeypoints(testImage);
        ArrayList<Keypoint> keypoints2 = new MatrixSIFTAnalyzer().computeImageKeypoints(testImage2);

        System.out.println(keypoints1.size());
        System.out.println(keypoints2.size());

        BufferedImage result = new SIFTVisualizer().drawKeypoints(testImage, keypoints1);
        File keypointsOutputFile = new File("keypoints_output.png");
        ImageIO.write(result, "png", keypointsOutputFile);

        ArrayList<FeatureMatch> matches = new SIFTMatcher().matchKeypoints(keypoints1, keypoints2, 0.8f);

        System.out.println(matches.size());

        BufferedImage matchingResult = new SIFTVisualizer().drawMatches(testImage, testImage2, matches);
        File outputFile = new File("matches_output.png");
        ImageIO.write(matchingResult, "png", outputFile);


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
            //if (k.getOctaveIndex() != 3) continue;
            if((int)k.getSubPixelX() > image.getWidth()-1 || (int)k.getSubPixelX() < 0) continue;
            if((int)k.getSubPixelY() > image.getHeight()-1 || (int)k.getSubPixelY() < 0) continue;

            int rgb = (255 << 16) | (0 << 8) | 0;

            int tempX = Math.round(k.getSubPixelX());
            int tempY = Math.round(k.getSubPixelY());

            int x = tempX * (int) Math.pow(2, k.getOctaveIndex());
            int y = tempY * (int) Math.pow(2, k.getOctaveIndex());

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