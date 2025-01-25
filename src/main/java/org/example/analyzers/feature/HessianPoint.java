package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class HessianPoint {
    private final double[] eigenvalues;

    public HessianPoint(BufferedImage image, int x, int y) {
        double[][] hessianMatrix = approxHessianMatrix(image, x, y);

        double trace = hessianMatrix[0][0] + hessianMatrix[1][1];
        double determinant = (hessianMatrix[0][0] * hessianMatrix[1][1]) - (hessianMatrix[0][1] * hessianMatrix[1][0]);
        this.eigenvalues = calculateEigenvalues(trace, determinant);
    }

    public HessianPoint(BufferedImage image, PixelPoint point) {
        this(image, point.getX(), point.getY());
    }


    public boolean isLowContrast(double contrastThreshold) {
        return eigenvalues[0] * eigenvalues[1] < contrastThreshold;
    }

    public boolean isEdgeResponse(double ratioThreshold) {
        return eigenvalues[0] / eigenvalues[1] < ratioThreshold;
    }


    /**
     * Calculates hessian matrix eigenvalues
     * @return array {lambda1, lambda2}
     */
    private double[] calculateEigenvalues(double trace, double determinant) {
        double discriminant = Math.pow(trace, 2) - 4 * determinant;

        if (discriminant < 0) {
            return new double[]{-1, -1};
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double lambda1 = (trace + sqrtDiscriminant) / 2.0;
        double lambda2 = (trace - sqrtDiscriminant) / 2.0;

        return new double[]{lambda1, lambda2};
    }

    /**
     * Generates Hessian matrix for a single pixel using second order Sobel operators
     */
    private double[][] approxHessianMatrix(BufferedImage image, int x, int y) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] matrix = new double[2][2];

        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        ImageAccessor imageAccessor = ImageAccessor.create(image);
        ImageAccessor resultAccessor = ImageAccessor.create(resultImage);

        int[][] sobelX2 = {
                { 1, -2,  1},
                { 2, -4,  2},
                { 1, -2,  1}
        };
        int[][] sobelY2 = {
                { 1,  2,  1},
                {-2, -4, -2},
                { 1,  2,  1}
        };
        int[][] sobelXY = {
                { 0, -1,  0},
                {-1,  4, -1},
                { 0, -1,  0}
        };


        int d2x = 0, d2y = 0, d2xy = 0;
        for (int dx=-1; dx<=1; dx++) {
            for (int dy=-1; dy<=1; dy++) {
                int pixelX = x + dx;
                int pixelY = y + dy;

                int red = imageAccessor.getRed(pixelX, pixelY);
                int green = imageAccessor.getGreen(pixelX, pixelY);
                int blue = imageAccessor.getBlue(pixelX, pixelY);
                int intensity = (int) (0.2989*red + 0.5870*green + 0.1140*blue);

                d2x += intensity * sobelX2[dx + 1][dy + 1];
                d2y += intensity * sobelY2[dx + 1][dy + 1];
                d2xy += intensity * sobelXY[dx + 1][dy + 1];
            }
        }

        int hessianValue = (int) Math.sqrt(d2x * d2x + d2y * d2y + 2 * d2xy * d2xy);
        hessianValue = Math.min(255, Math.max(0, hessianValue));
        resultAccessor.setOpaquePixel(x, y, hessianValue, hessianValue, hessianValue);


        return matrix;
    }
}