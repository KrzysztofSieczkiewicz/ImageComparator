package org.example.analyzers.ssim;

/**
 * Temporary class handling contrast component for SSIM comparison
 */
public class ContrastComponent {

    private double calculateWindowMean(int[] imageWindowData) {
        int sum = 0;
        for (int value: imageWindowData) {
            sum += value;
        }
        return (double) sum/imageWindowData.length;
    }

    private double calculateStandardDeviation(int[] imageWindowData) {
        double sumOfSquares = 0;
        double windowMean = calculateWindowMean(imageWindowData);

        for (double value: imageWindowData) {
            sumOfSquares += Math.pow(value-windowMean, 2);
        }
        return Math.sqrt(sumOfSquares / (imageWindowData.length - 1));
    }

    public double calculateContrastComponent(int[] firstWindow, int[] secondWindow, int dynamicRange, double sigma) {
        double firstStdDev = calculateStandardDeviation(firstWindow);
        double secondStdDev = calculateStandardDeviation(secondWindow);

        double c2 = Math.pow(sigma * dynamicRange, 2);

        double numerator = 2 * firstStdDev * secondStdDev + c2;
        double denominator = Math.pow(firstStdDev, 2) + Math.pow(secondStdDev, 2) + c2;

        return numerator / denominator;
    }
}
