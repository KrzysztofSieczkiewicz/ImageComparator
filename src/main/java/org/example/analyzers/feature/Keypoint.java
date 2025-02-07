package org.example.analyzers.feature;

public class Keypoint {

    private int octaveIndex;
    private float subPixelX, subPixelY;
    private float[] descriptor;

    public Keypoint(int octaveIndex, float subPixelX, float subPixelY, float[] descriptor) {
        this.octaveIndex = octaveIndex;
        this.subPixelX = subPixelX;
        this.subPixelY = subPixelY;

        this.descriptor = descriptor;
    }

    public int getOctaveIndex() {
        return octaveIndex;
    }

    public float getSubPixelX() {
        return subPixelX;
    }

    public float getSubPixelY() {
        return subPixelY;
    }

    public float[] getDescriptor() {
        return descriptor;
    }
}
