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

        int[] firstImageData = firstImageAccessor.getPixelsArray();
        int[] secondImageData = secondImageAccessor.getPixelsArray();

        int maxWidth = firstImageAccessor.getWidth();
        int maxHeight = firstImageAccessor.getHeight();

        LuminanceComponent lc = new LuminanceComponent();
        ContrastComponent cc = new ContrastComponent();
        StructuralComponent sc = new StructuralComponent();

        for (int x=0; x<maxWidth*maxHeight; x++) {
            int[] firstImageWindow = getWindowData(firstImageData, x, windowDimension);
            int[] secondImageWindow = getWindowData(secondImageData, x, windowDimension);

            double luminanceComponent = lc.calculateLuminanceComponent(firstImageWindow, secondImageWindow, 1, 1);
            double contrastComponent = cc.calculateContrastComponent(firstImageWindow, secondImageWindow, 1, 1);
            double structuralComponent = sc.calculateStructuralComponent(firstImageData, secondImageData, 0, 0, 1, 1);
        }
    }

    private int[] getWindowData(int[] imageData, int index, int windowDimension) {
        int[] windowData = new int[windowDimension*windowDimension];
        for (int dx=0; dx<= windowDimension*windowDimension; dx++) {
            windowData[dx] = imageData[index+dx];
        }

        return windowData;
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
