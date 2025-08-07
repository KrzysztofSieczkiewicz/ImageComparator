package org.example.analyzers.ssim;

import org.example.comparators.SSIMComparatorConfig;
import org.example.utils.ImageUtil;
import org.example.analyzers.common.TriFunction;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public class SSIMAnalyzer {
    private final Kernel gaussianKernel;

    // Components exponents
    private final double alpha;
    private final double beta;
    private final double gamma;

    // Stability constants
    private final double c1;
    private final double c2;
    private final double c3;

    private final TriFunction<Double, Double, Double, Double> ssimCalculationMethod;


    public SSIMAnalyzer(SSIMComparatorConfig config) {
        double sigma = config.getSigma();
        int windowDimension = config.getWindowSize();
        int dynamicRange = config.getDynamicRange();
        double k1 = config.getK1();
        double k2 = config.getK2();

        this.gaussianKernel = ImageUtil.generateGaussianKernel(windowDimension, sigma);
        this.alpha = config.getAlpha();
        this.beta = config.getBeta();
        this.gamma = config.getGamma();
        this.c1 = Math.pow(k1 * dynamicRange, 2);
        this.c2 = Math.pow(k2 * dynamicRange, 2);
        this.c3 = c2 / 2;

        if (alpha == 1 && beta == 1 && gamma == 1) {
            ssimCalculationMethod = this::computeWindowSimplifiedSSIM;
        } else {
            ssimCalculationMethod = this::computeWindowWeightedSSIM;
        }
    }

    public double calculateImagesSSIM(BufferedImage firstImage, BufferedImage secondImage) {
        int[] firstImageLuminosity = ImageUtil.extractLuminosity(firstImage);
        int[] secondImageLuminosity = ImageUtil.extractLuminosity(secondImage);
        int imgWidth = firstImage.getWidth();
        int imgHeight = firstImage.getHeight();
        int numPixels = firstImageLuminosity.length;

        int[] firstImageSquaredData = new int[numPixels];
        int[] secondImageSquaredData = new int[numPixels];
        int[] imagesProductData = new int[numPixels];

        for(int i=0; i<numPixels; i++) {
            firstImageSquaredData[i] = firstImageLuminosity[i] * firstImageLuminosity[i];
            secondImageSquaredData[i] = secondImageLuminosity[i] * secondImageLuminosity[i];
            imagesProductData[i] = firstImageLuminosity[i] * secondImageLuminosity[i];
        }

        double[] firstImageWeightedMeanData = ImageUtil.convolve(firstImageLuminosity, imgWidth, imgHeight, gaussianKernel);
        double[] secondImageWeightedMeanData = ImageUtil.convolve(secondImageLuminosity, imgWidth, imgHeight, gaussianKernel);
        double[] firstImageWeightedSquaredData = ImageUtil.convolve(firstImageSquaredData, imgWidth, imgHeight, gaussianKernel);
        double[] secondImageWeightedSquaredData = ImageUtil.convolve(secondImageSquaredData, imgWidth, imgHeight, gaussianKernel);
        double[] imagesWeightedProductData = ImageUtil.convolve(imagesProductData, imgWidth, imgHeight, gaussianKernel);

        double totalSSIM = 0.0;
        int validWindows = 0;

        for (int i = 0; i < numPixels; i++) {
            double firstMean = firstImageWeightedMeanData[i];
            double secondMean = secondImageWeightedMeanData[i];
            double firstVariance = firstImageWeightedSquaredData[i];
            double secondVariance = secondImageWeightedSquaredData[i];
            double product = imagesWeightedProductData[i];

            // sigma^2 = E[X^2] - (E[X])^2
            double firstStdDevSquared = Math.max(0, firstVariance - (firstMean * firstMean));
            double secondStdDevSquared = Math.max(0, secondVariance - (secondMean * secondMean));

            // sigma_xy = E[XY] - (E[X] * E[Y])
            double covariance = product - (firstMean * secondMean);

            double firstStdDev = Math.sqrt(firstStdDevSquared);
            double secondStdDev = Math.sqrt(secondStdDevSquared);

            double luminanceComponent = calculateLuminanceComponent(firstMean, secondMean);
            double contrastComponent = calculateContrastComponent(firstStdDev, secondStdDev);
            double structuralComponent = calculateStructuralComponent(firstStdDev, secondStdDev, covariance);

            double currentPixelSSIM = ssimCalculationMethod.apply(luminanceComponent, contrastComponent, structuralComponent);

            if (!Double.isNaN(currentPixelSSIM) && !Double.isInfinite(currentPixelSSIM)) {
                totalSSIM += currentPixelSSIM;
                validWindows++;
            }
        }

        return validWindows > 0 ? totalSSIM / validWindows : 0.0;
    }


    /**
     * Computes SSIM score for the window using simplified approach - multiplying components
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
    private double computeWindowWeightedSSIM(double luminance, double contrast, double structural) {
        double l_term = Math.pow(luminance, alpha);
        double c_term = Math.pow(contrast, beta);
        double s_term = Math.pow(structural, gamma);

        return l_term * c_term * s_term;
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
