package com.sieczk.comparators;

import java.awt.image.BufferedImage;

public class DirectComparisonResult {
    private BufferedImage resultImage;
    private boolean isMatching;

    public DirectComparisonResult(BufferedImage resultImage, boolean isMatching) {
        this.resultImage = resultImage;
        this.isMatching = isMatching;
    }

    public BufferedImage getResultImage() {
        return resultImage;
    }

    public boolean getIsMatching() {
        return isMatching;
    }
}
