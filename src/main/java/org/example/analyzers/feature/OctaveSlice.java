package org.example.analyzers.feature;

public class OctaveSlice {

    private final float[][][] images;
    private final int octaveIndex;
    private final int centralIndex;
    private final double downscalingRatio;

    public OctaveSlice(float[][][] images, int octaveIndex, double downscalingFactor) {
        this.images = images;
        this.octaveIndex = octaveIndex;
        this.downscalingRatio = Math.pow(downscalingFactor, octaveIndex);

        this.centralIndex = (images.length-1) / 2;
    }

    public float[][] getMainImage() {
        return images[centralIndex];
    }

    public float[][][] getSideImages() {
        float[][][] neighboursArray = new float
                [images.length-1]
                [images[0].length]
                [images[0][0].length];

        int index = 0;
        for (int i=0; i< images.length; i++) {
            if (i==centralIndex) continue;
            neighboursArray[index++] = images[i];
        }

        return neighboursArray;
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

    public double getDownscalingRatio() {
        return downscalingRatio;
    }
}
