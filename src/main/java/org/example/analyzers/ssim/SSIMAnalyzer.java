package org.example.analyzers.ssim;

import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class SSIMAnalyzer {

    public void slideWindow(BufferedImage image, int windowSize) {
        ImageAccessor imageAccessor = ImageAccessor.create(image);
        int[][] imageData = imageAccessor.getPixels();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int windowLuminosity = 0;

        for (int x=0; x<imageWidth-windowSize; x++) {
            for (int y=0; y<imageHeight-windowSize; y++) {
                windowLuminosity = sumWindowValues(imageData, x, y, windowSize);
            }
        }
    }

    public int sumWindowValues(int[][] imageData, int startX, int startY, int windowSize) {
        int sum = 0;

        for (int x=0; x<windowSize; x++) {
            for (int y=0; y<windowSize; y++) {
                sum += imageData[startX+x][startY+y];
            }
        }

        return sum;
    }
}
