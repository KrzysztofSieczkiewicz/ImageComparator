package org.example;

import org.example.analyzers.feature.*;
import org.example.analyzers.feature.homography.Homography;
import org.example.analyzers.feature.homography.HomographyEvaluator;
import org.example.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        BufferedImage testImage = ImageIO.read(new File("src/TestImage.jpg"));
        //BufferedImage testImage2 = ImageIO.read(new File("src/Image3.png"));
        BufferedImage testImage2 = ImageUtil.resize(testImage, 1024, 512);
        testImage2 = ImageUtil.greyscale(testImage2);

        File file = new File("src/baseImage.png");
        File file2 = new File("src/baseImage2.png");
        ImageIO.write(testImage, "PNG", file);
        ImageIO.write(testImage2, "PNG", file2);

        // TODO [Current]: RANSAC, image matching, orientation

        // TODO: next step - increase keypoint stability
        //  1. Check if subpixel position calculation is correct and if it is stable - maybe better interpolation methods?
        //  then, check all the methods that process images for SIFT and especially blur/subtract images
        //  it may strongly affect comparison stability

        // TODO: last step: test all ImageUtil/ImageDataUtil/DerivativeUtil methods:
        //  check greyscale
        //  check resize
        //  check gaussian

        long start = System.nanoTime();

        ArrayList<Keypoint> keypoints1 = new MatrixSIFTAnalyzer().findImageKeypoints(testImage);
        ArrayList<Keypoint> keypoints2 = new MatrixSIFTAnalyzer().findImageKeypoints(testImage2);
        System.out.println(keypoints1.size());
        System.out.println(keypoints2.size());

        BufferedImage result = new SIFTVisualizer().drawKeypoints(testImage, keypoints1);
        File keypointsOutputFile = new File("keypoints_output.png");
        ImageIO.write(result, "png", keypointsOutputFile);

        ArrayList<FeatureMatch> matches = new SIFTMatcher(150f, 0.8f).matchKeypoints(keypoints1, keypoints2);
        System.out.println(matches.size());

        BufferedImage matchingResult = new SIFTVisualizer().drawMatches(testImage, testImage2, matches);
        File outputFile = new File("matches_output.png");
        ImageIO.write(matchingResult, "png", outputFile);

        Homography homography = new HomographyEvaluator().estimateHomography(matches);
        System.out.println("Homography: \n" + Arrays.deepToString( homography.getMatrix() ));

        long end = System.nanoTime();
        System.out.println("Time taken: " + (end-start) + "ns");
    }

}