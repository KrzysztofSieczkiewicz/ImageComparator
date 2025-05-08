package org.example.analyzers.ssim;

import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class SSIMAnalyzer {

    public void slideWindow(BufferedImage firstImage, BufferedImage secondImage, int windowDimension) {
        ImageAccessor firstAccessor = ImageAccessor.create(firstImage);
        ImageAccessor secondAccessor = ImageAccessor.create(secondImage);
        int[][] firstImageData = firstAccessor.getPixels();
        int[][] secondImageData = secondAccessor.getPixels();
        int imageWidth = firstAccessor.getWidth();
        int imageHeight = firstAccessor.getHeight();
        int windowSize = windowDimension * windowDimension;

        double sigma = 0.001;

        for (int x=0; x<imageWidth-windowSize; x++) {
            for (int y=0; y<imageHeight-windowSize; y++) {
                int sumLuminosity1 = sumWindowValues(firstImageData, x, y, windowDimension);
                double meanLuminosity1 = (double) sumLuminosity1 /windowSize;

                int sumLuminosity2 = sumWindowValues(secondImageData, x, y, windowDimension);
                double meanLuminosity2 = (double) sumLuminosity2 /windowSize;

                double luminosityComponent = (2 * meanLuminosity1 * meanLuminosity2 + sigma) / (meanLuminosity1*meanLuminosity1 + meanLuminosity2*meanLuminosity2 + sigma);
            }
        }
    }

    public int sumWindowValues(int[][] imageData, int startX, int startY, int windowDimension) {
        int sum = 0;

        for (int x=0; x<windowDimension; x++) {
            for (int y=0; y<windowDimension; y++) {
                sum += imageData[startX+x][startY+y];
            }
        }

        return sum;
    }
}
