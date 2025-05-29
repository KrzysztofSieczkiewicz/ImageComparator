package org.example.analyzers.ssim;

public class LuminanceComponent {

    private double calculateWindowMean(int[] windowData) {
        int sum = 0;
        for (int pixel: windowData) {
            sum += pixel;
        }

        return (double) sum / windowData.length;
    }

    public double calculateLuminanceComponent(int[] firstWindowData, int[] secondWindowData, double dynamicRange, double k1) {
        double firstWindowMean = calculateWindowMean(firstWindowData);
        double secondWindowMean = calculateWindowMean(secondWindowData);
        double c1 = Math.pow(k1 * dynamicRange, 2);

        double numerator = 2 * firstWindowMean * secondWindowMean + c1;
        double denominator = Math.pow(firstWindowMean, 2) + Math.pow(secondWindowMean, 2) + c1;

        if (denominator == 0.0) {
            return 0.0;
        }

        return numerator / denominator;
    }
}