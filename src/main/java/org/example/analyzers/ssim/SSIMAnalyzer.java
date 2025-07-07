package org.example.analyzers.ssim;

import org.example.utils.ImageUtil;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class SSIMAnalyzer {
    private final int windowDimension = 3;

    // Gaussian Kernel
    private final double sigma = 1.5;
    private double[] gaussianKernel = ImageUtil.generateGaussianKernel(windowDimension, sigma);

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

        for (int x=0; x<maxWidth-windowDimension; x++) {
            for (int y=0; y<maxHeight-windowDimension; y++) {
                int[] firstImageWindow = ImageUtil.getWindowData(firstImageData, maxWidth, windowDimension, x, y);
                int[] secondImageWindow = ImageUtil.getWindowData(secondImageData, maxWidth, windowDimension, x, y);

                double firstWindowMean = calculateWeightedWindowMean(firstImageWindow);
                double secondWindowMean = calculateWeightedWindowMean(secondImageWindow);
                double firstWindowStDev = calculateWeightedWindowStDev(firstImageWindow, firstWindowMean);
                double secondWindowStDev = calculateWeightedWindowStDev(secondImageWindow, secondWindowMean);

                double luminanceComponent = calculateLuminanceComponent(firstWindowMean, secondWindowMean);
                double contrastComponent = calculateContrastComponent(firstWindowStDev, secondWindowStDev);
                double structuralComponent = calculateStructuralComponent(firstImageWindow, secondImageWindow, firstWindowStDev, secondWindowStDev);

                System.out.println("Luminance component = " + luminanceComponent);
                System.out.println("Contrast component = " + contrastComponent);
                System.out.println("Structural component = " + structuralComponent);
            }
        }
    }

    private double calculateWeightedWindowMean(int[] windowData) {
        double sum = 0;
        for (int i = 0; i < windowData.length; i++) {
            sum += windowData[i] * gaussianKernel[i];
        }
        return sum/windowData.length;
    }

    private double calculateWeightedWindowStDev(int[] windowData, double windowMean) {
        double sumOfWeightedSquares = 0;

        for (int i = 0; i < windowData.length; i++) {
            sumOfWeightedSquares += gaussianKernel[i] * Math.pow(windowData[i] - windowMean, 2);
        }
        return Math.sqrt(sumOfWeightedSquares / (windowData.length - 1));
    }

    private double calculateWeightedCovariance(int[] firstWindowData, int[] secondWindowData) {
        double firstWindowMean = calculateWeightedWindowMean(firstWindowData);
        double secondWindowMean = calculateWeightedWindowMean(secondWindowData);

        double sum = 0;
        for (int i = 0; i < firstWindowData.length; i++) {
            sum += gaussianKernel[i] * (firstWindowData[i] - firstWindowMean) * (secondWindowData[i] - secondWindowMean);
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
        double numerator = calculateWeightedCovariance(firstWindowData, secondWindowData) + c3;
        double denominator = firstStdDev * secondStdDev + c3;

        if (denominator == 0.0) {
            return 0.0;
        }

        return numerator / denominator;
    }
}
