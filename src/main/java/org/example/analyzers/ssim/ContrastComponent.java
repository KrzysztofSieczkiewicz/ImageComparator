package org.example.analyzers.ssim;

/**
 * Temporary class handling contrast component for SSIM comparison
 */
public class ContrastComponent {

    public double calculateContrastComponent(double firstWindowStDev, double secondWindowStDev, double dynamicRange, double sigma) {
        double c2 = Math.pow(sigma * dynamicRange, 2);

        double numerator = 2 * firstWindowStDev * secondWindowStDev + c2;
        double denominator = Math.pow(firstWindowStDev, 2) + Math.pow(secondWindowStDev, 2) + c2;

        return numerator / denominator;
    }
}
