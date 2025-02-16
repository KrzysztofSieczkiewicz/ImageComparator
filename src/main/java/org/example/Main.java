package org.example;

import org.example.analyzers.feature.*;
import org.example.analyzers.feature.homography.Homography;
import org.example.analyzers.feature.homography.HomographyEvaluator;
import org.example.analyzers.feature.keypoints.FeatureMatch;
import org.example.analyzers.feature.keypoints.Keypoint;
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

        // TODO: fix keypoint positions depending on octaves
        // TODO: consider adding Pyramid class to provide data throughout other classes

        // TODO: last step: test all ImageUtil/ImageDataUtil/DerivativeUtil methods:
        //  check greyscale
        //  check resize
        //  check gaussian


        long start = System.nanoTime();

        ArrayList<Keypoint> keypoints1 = new SIFTAnalyzer().findImageKeypoints(testImage);
        ArrayList<Keypoint> keypoints2 = new SIFTAnalyzer().findImageKeypoints(testImage2);

        ArrayList<FeatureMatch> matches = new SIFTMatcher(0.8f).matchKeypoints(keypoints1, keypoints2);
        Homography homography = new HomographyEvaluator().estimateHomography(matches);

        long end = System.nanoTime();
        System.out.println("Time taken: " + (end-start) + "ns");


        BufferedImage result = new SIFTVisualizer().drawKeypoints(testImage, keypoints1);
        File keypointsOutputFile = new File("keypoints_output.png");
        ImageIO.write(result, "png", keypointsOutputFile);

        BufferedImage matchingResult = new SIFTVisualizer().drawMatches(testImage, testImage2, matches);
        File outputFile = new File("matches_output.png");
        ImageIO.write(matchingResult, "png", outputFile);

        System.out.println("Keypoints1: " + keypoints1.size());
        System.out.println("Keypoints2: " + keypoints2.size());
        System.out.println("Matches: " + matches.size());
        System.out.println("Homography: \n" + Arrays.deepToString( homography.getMatrix() ));
    }

}