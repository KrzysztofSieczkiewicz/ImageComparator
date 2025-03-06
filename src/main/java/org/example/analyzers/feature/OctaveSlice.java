package org.example.analyzers.feature;

public class OctaveSlice {
    private final float[][][] images;
    private final int octaveIndex;
    private final int scaleIndex;
    private final double downscalingRatio;

    private final int centralIndex;
    private final float[][][] peripheralImages;


    public OctaveSlice(float[][][] images, int octaveIndex, int scaleIndex, double downscalingFactor) {
        this.images = images;
        this.octaveIndex = octaveIndex;
        this.scaleIndex = scaleIndex;
        this.downscalingRatio = Math.pow(downscalingFactor, octaveIndex);

        this.centralIndex = (images.length-1) / 2;
        this.peripheralImages = extractPeripheralImages(centralIndex);
    }

    private float[][][] extractPeripheralImages(int centralIndex) {
        float[][][] peripheralImages = new float
                [images.length-1]
                [images[0].length]
                [images[0][0].length];

        int index = 0;
        for (int i=0; i<images.length; i++) {
            if (i==centralIndex) continue;
            peripheralImages[index] = images[i];
            index++;
        }

        return peripheralImages;
    }

    public float[][] getMainImage() {
        return images[centralIndex];
    }

    // WHY IS NOT WORKING FFS
    public float[][][] getPeripheralImages() {
        return peripheralImages;
    }

    public float[][][] getImages() {
        return images;
    }

    public int getOctaveIndex() {
        return octaveIndex;
    }

    public int getScaleIndex() {
        return scaleIndex;
    }

    public int getCentralIndex() {
        return centralIndex;
    }

    public double getDownscalingRatio() {
        return downscalingRatio;
    }
}
