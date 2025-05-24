package org.example.analyzers.ssim;

public class StructuralComponent {

    private double calculateWindowMean(int[] imageWindowData) {
        int sum = 0;
        for (int value: imageWindowData) {
            sum += value;
        }
        return (double) sum/imageWindowData.length;
    }

    private double calculateCovariance(int[] firstWindowData, double firstWindowMean, int[] secondWindowData, double secondWindowMean) {
        double sum = 0;
        for (int i=0; i< firstWindowData.length; i++) {
            sum += (firstWindowData[i] - firstWindowMean) * (secondWindowData[i] - secondWindowMean);
        }
        return sum / (firstWindowData.length - 1);
    }

    private double calculateCovariance(int[] firstWindowData, int[] secondWindowData) {
        double firstWindowMean = calculateWindowMean(firstWindowData);
        double secondWindowMean = calculateWindowMean(secondWindowData);
        return calculateCovariance(firstWindowData, firstWindowMean, secondWindowData, secondWindowMean);
    }

    private double calculateStructuralComponent(int[] firstWindowData, int[] secondWindowData, double firstStdDev, double secondStdDev, double c3) {
        double numerator = calculateCovariance(firstWindowData, secondWindowData) + c3;
        double denominator = firstStdDev * secondStdDev + c3;

        if (denominator == 0.0) {
            return 0.0;
        }

        return numerator / denominator;
    }

    public double calculateStructuralComponent(int[] firstWindowData, int[] secondWindowData, double firstStdDev, double secondStdDev, int dynamicRange, double k2) {
        double c2 = Math.pow(k2 * dynamicRange, 2);
        double c3 = c2 / 2.0;
        return calculateStructuralComponent(firstWindowData, secondWindowData, firstStdDev, secondStdDev, c3);
    }
}
