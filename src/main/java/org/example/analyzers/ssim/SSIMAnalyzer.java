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

        for (int x=0; x<maxWidth*maxHeight - windowDimension*windowDimension; x++) {
            int[] firstImageWindow = getWindowData(firstImageData, x, windowDimension);
            int[] secondImageWindow = getWindowData(secondImageData, x, windowDimension);

            double firstWindowMean = calculateWindowMean(firstImageWindow);
            double secondWindowMean = calculateWindowMean(secondImageWindow);
            double firstWindowStDev = calculateWindowStDev(firstImageWindow, firstWindowMean);
            double secondWindowStDev = calculateWindowStDev(secondImageWindow, secondWindowMean);

            double luminanceComponent = calculateLuminanceComponent(firstWindowMean, secondWindowMean);
            double contrastComponent = calculateContrastComponent(firstWindowStDev, secondWindowStDev);
            double structuralComponent = calculateStructuralComponent(firstImageWindow, secondImageWindow, firstWindowStDev, secondWindowStDev);

            System.out.println("Luminance component = " + luminanceComponent);
            System.out.println("Contrast component = " + contrastComponent);
            System.out.println("Structural component = " + structuralComponent);
        }
    }

    private int[] getWindowData(int[] imageData, int index, int windowDimension) {
        int[] windowData = new int[windowDimension*windowDimension];
        if (windowDimension * windowDimension >= 0)
            System.arraycopy(imageData, index + 0, windowData, 0, windowDimension * windowDimension);

        return windowData;
    }

    private double calculateWindowMean(int[] windowData) {
        int sum = 0;
        for (int value: windowData) {
            sum += value;
        }
        return (double) sum/windowData.length;
    }

    private double calculateWindowStDev(int[] windowData, double windowMean) {
        double sumOfSquares = 0;

        for (double value: windowData) {
            sumOfSquares += Math.pow(value-windowMean, 2);
        }
        return Math.sqrt(sumOfSquares / (windowData.length - 1));
    }

    private double calculateCovariance(int[] firstWindowData, int[] secondWindowData) {
        double firstWindowMean = calculateWindowMean(firstWindowData);
        double secondWindowMean = calculateWindowMean(secondWindowData);

        double sum = 0;
        for (int i=0; i< firstWindowData.length; i++) {
            sum += (firstWindowData[i] - firstWindowMean) * (secondWindowData[i] - secondWindowMean);
        }
        return sum / (firstWindowData.length - 1);
    }

    public double calculateLuminanceComponent(double firstWindowMean, double secondWindowMean) {
        double numerator = 2 * firstWindowMean * secondWindowMean + c1;
        double denominator = Math.pow(firstWindowMean, 2) + Math.pow(secondWindowMean, 2) + c1;

        if (denominator == 0.0) {
            return 0.0;
        }

        return numerator / denominator;
    }

    public double calculateContrastComponent(double firstWindowStDev, double secondWindowStDev) {
        double numerator = 2 * firstWindowStDev * secondWindowStDev + c2;
        double denominator = Math.pow(firstWindowStDev, 2) + Math.pow(secondWindowStDev, 2) + c2;

        return numerator / denominator;
    }

    private double calculateStructuralComponent(int[] firstWindowData, int[] secondWindowData, double firstStdDev, double secondStdDev) {
        double numerator = calculateCovariance(firstWindowData, secondWindowData) + c3;
        double denominator = firstStdDev * secondStdDev + c3;

        if (denominator == 0.0) {
            return 0.0;
        }

        return numerator / denominator;
    }
}
