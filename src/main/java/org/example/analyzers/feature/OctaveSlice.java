package org.example.analyzers.feature;

public class OctaveSlice {

    private final float[][][] images;
    private final int octaveIndex;
    private final int centralIndex;

    public OctaveSlice(float[][][] images, int octaveIndex) {
        this.images = images;
        this.octaveIndex = octaveIndex;

        this.centralIndex = (images.length - 1) / 2;
    }

    public float[][] getMainImage() {
        return images[centralIndex];
    }

    public float[][][] getImages() {
        return images;
    }

    public int getOctaveIndex() {
        return octaveIndex;
    }

    public int getCentralIndex() {
        return centralIndex;
    }
}
