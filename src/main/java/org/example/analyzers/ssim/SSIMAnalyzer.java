package org.example.analyzers.ssim;

import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

/**
 * TODO: REPLACE sigma variable with better name - it is reused and necessary
 * TODO: ADD dynamicRange variable as configurable or as being read
 */
public class SSIMAnalyzer {
    private final int windowDimension = 3;
    private final double sigma = 0.001;

    // Dynamic range - maximal value that can be set to a pixel in the image - by default set for 8bit images
    private final double dynamicRange = 255;

    // Empirical constants
    private final double k1 = 0.01;
    private final double k2 = 0.03;

    // Stability constants
    private final double c1 = Math.pow(k1 * dynamicRange, 2);
    private final double c2 = Math.pow(k2 * dynamicRange, 2);
    private final double c3 = k2 / 2;


    public void compareImages(BufferedImage firstImage, BufferedImage secondImage) {
        ImageAccessor firstImageAccessor = ImageAccessor.create(firstImage);
        ImageAccessor secondImageAccessor = ImageAccessor.create(secondImage);

        int[] firstImageData = firstImageAccessor.getPixelsArray();
        int[] secondImageData = secondImageAccessor.getPixelsArray();

        int maxWidth = firstImageAccessor.getWidth();
        int maxHeight = firstImageAccessor.getHeight();

        LuminanceComponent lc = new LuminanceComponent();
        ContrastComponent cc = new ContrastComponent();
        StructuralComponent sc = new StructuralComponent();

        for (int x=0; x<maxWidth*maxHeight; x++) {
            int[] firstImageWindow = getWindowData(firstImageData, x, windowDimension);
            int[] secondImageWindow = getWindowData(secondImageData, x, windowDimension);

            // ADD WINDOW STD DEV CALC HERE

            double luminanceComponent = lc.calculateLuminanceComponent(firstImageWindow, secondImageWindow, dynamicRange, k1);
            double contrastComponent = cc.calculateContrastComponent(firstImageWindow, secondImageWindow, dynamicRange, sigma);
            double structuralComponent = sc.calculateStructuralComponent(firstImageData, secondImageData, 0, 0, dynamicRange, k2);

            System.out.println("Luminance component = " + luminanceComponent);
            System.out.println("Contrast component = " + contrastComponent);
            System.out.println("Structural component = " + structuralComponent);
        }
    }

    private int[] getWindowData(int[] imageData, int index, int windowDimension) {
        int[] windowData = new int[windowDimension*windowDimension];
        for (int dx=0; dx<windowDimension*windowDimension; dx++) {
            windowData[dx] = imageData[index+dx];
        }

        return windowData;
    }

    public int sumWindowValues(int[][] imageData, int startX, int startY, int windowDimension) {
        int sum = 0;

        for (int x=0; x<windowDimension; x++) {
            for (int y=0; y<windowDimension; y++) {
                sum += imageData[startX+x][startY+y];
            }
        }

        return sum;
    }
}
