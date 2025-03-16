package org.example;

import org.example.analyzers.feature.*;
import org.example.analyzers.feature.keypoints.FeatureMatch;
import org.example.analyzers.feature.keypoints.Keypoint;
import org.example.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    // TODO: finish refactoring analyzer
    // TODO [NOW]: cleanup the code for Keypoint detection - descriptor and orientations might require rework
    // then - check gaussians and dogs generation
    // too many keypoints are found on flat areas and not on the edges - something is off

    public static void main(String[] args) throws IOException {
//        BufferedImage testImage = ImageIO.read(new File("src/TestImage.jpg"));
//        BufferedImage testImage2 = ImageUtil.resize(testImage, (int)(testImage.getWidth()*0.8), (int)(testImage.getHeight()*0.8));

//        BufferedImage testImage = ImageIO.read(new File("src/Woman.png"));
//        BufferedImage testImage2 = ImageUtil.resize(testImage, (int)(testImage.getWidth()*0.8), (int)(testImage.getHeight()*0.8));

        BufferedImage testImage = ImageIO.read(new File("src/Chicken.png"));
        BufferedImage testImage2 = ImageUtil.resize(testImage, 512, 512);
//        testImage2 = rotateImage(testImage, 45);

//        BufferedImage testImage = ImageIO.read(new File("src/Checkerboard.png"));
//        BufferedImage testImage2 = ImageUtil.resize(testImage, (int)(testImage.getWidth()*1.3), (int)(testImage.getHeight()*1.3));
//        testImage2 = ImageUtil.resize(testImage2, 512, 1024);

//        testImage = ImageUtil.greyscale(testImage);
//        testImage = ImageUtil.resize(testImage, testImage.getWidth()/2, testImage.getHeight()/2);
        //testImage2 = ImageUtil.resize(testImage, testImage.getWidth()/2, testImage.getHeight()/2);

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
        System.out.println("Keypoints 1: " + keypoints1.size());


        List<Keypoint> keypoints2 = new SIFTAnalyzer().findKeypoints(testImage2);
        BufferedImage result2 = new SIFTVisualizer().drawKeypoints(testImage2, keypoints2);
        File keypointsOutputFile2 = new File("keypoints_output_2.png");
        ImageIO.write(result2, "png", keypointsOutputFile2);
        System.out.println("Keypoints 2: " + keypoints2.size());

        ArrayList<FeatureMatch> matches = new SIFTAnalyzer().matchKeypoints(keypoints1, keypoints2);

        //Homography homography = new HomographyEvaluator().estimateHomography(matches);

        long end = System.nanoTime();
        System.out.println("Time taken: " + (end-start) + "ns");

        BufferedImage matchingResult = new SIFTVisualizer().drawMatches(testImage, testImage2, matches);
        File outputFile = new File("matches_output.png");
        ImageIO.write(matchingResult, "png", outputFile);
        System.out.println("Matches: " + matches.size());

        //System.out.println("Homography: \n" + Arrays.deepToString( homography.getMatrix() ));
    }

    public static BufferedImage rotateImage(BufferedImage originalImage, double angleDegrees) {
        // Convert degrees to radians
        double angleRadians = Math.toRadians(angleDegrees);

        // Get image dimensions
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Compute new dimensions after rotation
        double sin = Math.abs(Math.sin(angleRadians));
        double cos = Math.abs(Math.cos(angleRadians));
        int newWidth = (int) Math.floor(width * cos + height * sin);
        int newHeight = (int) Math.floor(height * cos + width * sin);

        // Create a new rotated image
        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());

        // Create a graphics object
        Graphics2D g2d = rotatedImage.createGraphics();

        // Apply anti-aliasing for better image quality
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Set up rotation transformation
        AffineTransform transform = new AffineTransform();
        transform.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0); // Centering
        transform.rotate(angleRadians, width / 2.0, height / 2.0); // Rotate around center

        // Draw the rotated image
        g2d.drawImage(originalImage, transform, null);
        g2d.dispose();

        return rotatedImage;
    }

}