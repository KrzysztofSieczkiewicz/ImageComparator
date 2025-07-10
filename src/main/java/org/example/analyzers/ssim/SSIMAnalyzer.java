package org.example.analyzers.ssim;

import org.example.utils.ImageUtil;
import org.example.utils.TriFunction;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.util.function.BiFunction;

public class SSIMAnalyzer {
    private final int windowDimension = 3;

    // Gaussian Kernel
    private final double sigma = 1.5;
    private double[] gaussianKernel;

    // Dynamic range - maximal value that can be set to a pixel in the image - by default set for 8bit images
    private final double dynamicRange = 255;

    // Empirical constants
    private final double k1 = 0.01;
    private final double k2 = 0.03;

    //
    private final double alpha = 1.0;
    private final double beta = 1.0;
    private final double gamma = 1.0;

    // Stability constants
    private final double c1 = Math.pow(k1 * dynamicRange, 2);
    private final double c2 = Math.pow(k2 * dynamicRange, 2);
    private final double c3 = k2 / 2;

    private TriFunction<Double, Double, Double, Double> ssimCalculationMethod;


    public SSIMAnalyzer() {
        gaussianKernel = ImageUtil.generateGaussianKernel(windowDimension, sigma);

        if (alpha == 1 && beta == 1 && gamma == 1) {
            ssimCalculationMethod = this::computeWindowSimplifiedSSIM;
        } else {
            ssimCalculationMethod = this::computeWindowSSIM;
        }
    }


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
                double windowCovariance = calculateWeightedCovariance(firstImageData, secondImageData);

                double luminanceComponent = calculateLuminanceComponent(firstWindowMean, secondWindowMean);
                double contrastComponent = calculateContrastComponent(firstWindowStDev, secondWindowStDev);
                double structuralComponent = calculateStructuralComponent(firstWindowStDev, secondWindowStDev, windowCovariance);

                System.out.println("Luminance component = " + luminanceComponent);
                System.out.println("Contrast component = " + contrastComponent);
                System.out.println("Structural component = " + structuralComponent);
            }
        }
    }

    /**
     * Computes SSIM score for the window using simplified approach by multiplying components
     * @param luminance component of the window
     * @param contrast component of the window
     * @param structural component of the window
     * @return combined SSIM score for the window
     */
    private Double computeWindowSimplifiedSSIM(double luminance, double contrast, double structural) {
        return luminance * contrast * structural;
    }

    /**
     * Computes SSIM score for the window using components and their exponent factors.
     * @param luminance component of the window
     * @param contrast component of the window
     * @param structural component of the window
     * @return combined SSIM score for the window
     */
    private double computeWindowSSIM(double luminance, double contrast, double structural) {
        double l_term = (alpha == 0) ? 1.0 : Math.pow(luminance, alpha);
        double c_term = (beta == 0) ? 1.0 : Math.pow(contrast, beta);
        double s_term = (gamma == 0) ? 1.0 : Math.pow(structural, gamma);

        return l_term * c_term * s_term;
    }

    /**
     * @param windowData 1D array of image window
     * @return mean value of pixels in the window
     */
    private double calculateWeightedWindowMean(int[] windowData) {
        double sum = 0;
        for (int i = 0; i < windowData.length; i++) {
            sum += windowData[i] * gaussianKernel[i];
        }
        return sum/windowData.length;
    }

    /**
     * @param windowData 1D array of image window
     * @param windowMean mean value of window pixels
     * @return std deviation
     */
    private double calculateWeightedWindowStDev(int[] windowData, double windowMean) {
        double sumOfWeightedSquares = 0;

        for (int i = 0; i < windowData.length; i++) {
            sumOfWeightedSquares += gaussianKernel[i] * Math.pow(windowData[i] - windowMean, 2);
        }
        return Math.sqrt(sumOfWeightedSquares / (windowData.length - 1));
    }

    /**
     * @param firstWindowData 1D array of the first image window
     * @param secondWindowData 1D array of the second image window
     * @return covariance
     */
    private double calculateWeightedCovariance(int[] firstWindowData, int[] secondWindowData) {
        double firstWindowMean = calculateWeightedWindowMean(firstWindowData);
        double secondWindowMean = calculateWeightedWindowMean(secondWindowData);

        double sum = 0;
        for (int i = 0; i < firstWindowData.length; i++) {
            sum += gaussianKernel[i] * (firstWindowData[i] - firstWindowMean) * (secondWindowData[i] - secondWindowMean);
        }
        return sum / (firstWindowData.length - 1);
    }

    /**
     * @param firstWindowMean mean value of pixels in the first window
     * @param secondWindowMean mean value of pixels in the second window
     * @return luminance component
     */
    private double calculateLuminanceComponent(double firstWindowMean, double secondWindowMean) {
        double numerator = 2 * firstWindowMean * secondWindowMean + c1;
        double denominator = Math.pow(firstWindowMean, 2) + Math.pow(secondWindowMean, 2) + c1;

        if (denominator == 0.0) {
            return 0.0;
        }

        return numerator / denominator;
    }

    /**
     * @param firstWindowStDev std deviation of pixels in the first window
     * @param secondWindowStDev std deviation of pixels in the second window
     * @return contrast component
     */
    private double calculateContrastComponent(double firstWindowStDev, double secondWindowStDev) {
        double numerator = 2 * firstWindowStDev * secondWindowStDev + c2;
        double denominator = Math.pow(firstWindowStDev, 2) + Math.pow(secondWindowStDev, 2) + c2;

        return numerator / denominator;
    }

    /**
     * @param firstStdDev std deviation of pixels in the first window
     * @param secondStdDev std deviation of pixels in the second window
     * @param covariance covariance between two windows
     * @return structural component
     */
    private double calculateStructuralComponent(double firstStdDev, double secondStdDev, double covariance) {
        double numerator = covariance + c3;
        double denominator = firstStdDev * secondStdDev + c3;

        if (denominator == 0.0) {
            return 0.0;
        }

        return numerator / denominator;
    }
}
