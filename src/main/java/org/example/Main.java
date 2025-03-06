package org.example;

import org.example.analyzers.feature.*;
import org.example.analyzers.feature.keypoints.FeatureMatch;
import org.example.analyzers.feature.keypoints.Keypoint;
import org.example.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // TODO: finish refactoring analyzer
    // then - check gaussians and dogs generation
    // too many keypoints are found on flat areas and not on the edges - something is off

    public static void main(String[] args) throws IOException {
        BufferedImage testImage = ImageIO.read(new File("src/TestImage.jpg"));
        BufferedImage testImage2 = ImageUtil.resize(testImage, 1024, 512);

//        BufferedImage testImage = ImageIO.read(new File("src/Chicken.png"));
//        BufferedImage testImage2 = ImageUtil.resize(testImage, 1024, 512);

//        BufferedImage testImage = ImageIO.read(new File("src/Eiffel1.jpg"));
//        BufferedImage testImage2 = ImageIO.read(new File("src/Eiffel2.jpg"));
//        testImage2 = ImageUtil.resize(testImage2, 512, 1024);

        testImage = ImageUtil.greyscale(testImage);
        //testImage = ImageUtil.resize(testImage, testImage.getWidth()/2, testImage.getHeight()/2);

        File file = new File("src/baseImage.png");
        File file2 = new File("src/baseImage2.png");
        ImageIO.write(testImage, "PNG", file);
        ImageIO.write(testImage2, "PNG", file2);

        // TODO: last step: test all ImageUtil/ImageDataUtil/DerivativeUtil methods:
        //  check greyscale
        //  check resize
        //  check gaussian

        long start = System.nanoTime();

        List<Keypoint> keypoints1 = new SIFTAnalyzer().findKeypoints(testImage);
        BufferedImage result1 = new SIFTVisualizer().drawKeypoints(testImage, keypoints1);
        File keypointsOutputFile1 = new File("keypoints_output_1.png");
        ImageIO.write(result1, "png", keypointsOutputFile1);
        System.out.println("I managed with keypoints 1: " + keypoints1.size());


//        List<Keypoint> keypoints2 = new SIFTAnalyzer().findKeypoints(testImage2);
//        BufferedImage result2 = new SIFTVisualizer().drawKeypoints(testImage2, keypoints2);
//        File keypointsOutputFile2 = new File("keypoints_output_2.png");
//        ImageIO.write(result2, "png", keypointsOutputFile2);
//        System.out.println("I managed with keypoints 2: " + keypoints2.size());
//
//        ArrayList<FeatureMatch> matches = new SIFTAnalyzer().matchKeypoints(keypoints1, keypoints2);
//
//        System.out.println("I managed with matching");
//
//        //Homography homography = new HomographyEvaluator().estimateHomography(matches);
//
//        System.out.println("I managed with homography");
//
//        long end = System.nanoTime();
//        System.out.println("Time taken: " + (end-start) + "ns");
//
//        BufferedImage matchingResult = new SIFTVisualizer().drawMatches(testImage, testImage2, matches);
//        File outputFile = new File("matches_output.png");
//        ImageIO.write(matchingResult, "png", outputFile);
//
//        System.out.println("Keypoints1: " + keypoints1.size());
//        System.out.println("Keypoints2: " + keypoints2.size());
//        System.out.println("Matches: " + matches.size());

//        System.out.println("Homography: \n" + Arrays.deepToString( homography.getMatrix() ));
    }

}