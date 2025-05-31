package org.example.analyzers.ssim;

public class LuminanceComponent {

    public double calculateLuminanceComponent(double firstWindowMean, double secondWindowMean, double dynamicRange, double k1) {
        double c1 = Math.pow(k1 * dynamicRange, 2);

        double numerator = 2 * firstWindowMean * secondWindowMean + c1;
        double denominator = Math.pow(firstWindowMean, 2) + Math.pow(secondWindowMean, 2) + c1;

        if (denominator == 0.0) {
            return 0.0;
        }

        return numerator / denominator;
    }
}