package org.example;

import org.example.analyzers.feature.*;
import org.example.utils.ImageUtil;

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
        BufferedImage testImage2 = ImageUtil.resize(testImage, testImage.getWidth()-50, testImage.getHeight()-100);

        File file = new File("src/baseImage.png");
        File file2 = new File("src/baseImage2.png");
        ImageIO.write(testImage, "PNG", file);
        ImageIO.write(testImage2, "PNG", file2);

        // TODO:
        //  then, check all the methods that process images for SIFT and especially blur/subtract images
        //  it may strongly affect comparison stability

        // TODO: next step - increase keypoint stability
        //  2. Consider computing DoG images from more than 2 scales
        //  4. Check if subpixel position calculation is correct and if it is stable - maybe better interpolation methods?
        //  now minor, but maybe important:
        //  1. Make sure that greyscale methods are properly calculating the greyscale
        //  2. In SIFTMatcher - split methods - one for checking keypoints against neighbours, another - for additional check that keypoints are in the same or neighbouring scale

        long start = System.nanoTime();

        ArrayList<Keypoint> keypoints1 = new MatrixSIFTAnalyzer().computeImageKeypoints(testImage);
        ArrayList<Keypoint> keypoints2 = new MatrixSIFTAnalyzer().computeImageKeypoints(testImage2);
        System.out.println(keypoints1.size());
        System.out.println(keypoints2.size());

        BufferedImage result = new SIFTVisualizer().drawKeypoints(testImage, keypoints1);
        File keypointsOutputFile = new File("keypoints_output.png");
        ImageIO.write(result, "png", keypointsOutputFile);

        ArrayList<FeatureMatch> matches = new SIFTMatcher(150).matchKeypoints(keypoints1, keypoints2, 0.8f);
        System.out.println(matches.size());

        BufferedImage matchingResult = new SIFTVisualizer().drawMatches(testImage, testImage2, matches);
        File outputFile = new File("matches_output.png");
        ImageIO.write(matchingResult, "png", outputFile);

        long end = System.nanoTime();
        System.out.println("Time taken: " + (end-start) + "ns");
    }

}