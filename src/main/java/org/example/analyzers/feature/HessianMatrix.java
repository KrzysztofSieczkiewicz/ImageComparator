package org.example.analyzers.feature;

import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class HessianMatrix {

    double[][] eigenvalues;
    double trace;
    double determinant;

    // TODO: calculate trace, determinant and eigenvalues;





    /**
     * Generates Hessian matrix using second order Sobel operators
     */
    private BufferedImage approxHessianMatrix(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
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

        for (int x=1; x<width-1; x++) {
            for (int y=1; y<height-1; y++) {
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
            }
        }

        return resultImage;
    }
}
