package org.example.analyzers.feature;

import org.example.analyzers.common.PixelPoint;
import org.example.utils.accessor.ImageAccessor;

import java.awt.image.BufferedImage;

public class Keypoint {
    private float x,y;

    public Keypoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Keypoint(KeypointCandidate candidate) {
        this.x = candidate.getX();
        this.y = candidate.getY();


    }

}
