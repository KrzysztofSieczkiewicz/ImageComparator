package org.example.analyzers.ssim;

import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

/**
 * TODO: REPLACE sigma variable with better name - it is reused and necessary
 * TODO: ADD dynamicRange variable as configurable or as being read
 */
public class SSIMAnalyzer {
    private final int windowDimension = 3;
    private final double sigma = 0.001;

    public void compareImages(BufferedImage firstImage, BufferedImage secondImage) {
        ImageAccessor firstImageAccessor = ImageAccessor.create(firstImage);
        ImageAccessor secondImageAccessor = ImageAccessor.create(secondImage);

        int maxWidth = firstImageAccessor.getWidth();
        int maxHeight = firstImageAccessor.getHeight();

        for (int x=0; x<maxWidth-windowDimension; x++) {
            for (int y=0; y<maxHeight-windowDimension; y++) {

            }
        }


    }


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
