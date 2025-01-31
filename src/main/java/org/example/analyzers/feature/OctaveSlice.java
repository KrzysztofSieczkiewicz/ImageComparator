package org.example.analyzers.feature;

/**
 * Helper class holding three consecutive scales from the octave
 */
public class OctaveSlice {

    private int scaleIndex;
    private float[][] previousScale;
    private float[][] currentScale;
    private float[][] nextScale;

    public OctaveSlice(int scaleIndex, float[][] previousScale, float[][] currentScale, float[][] nextScale) {
        this.scaleIndex = scaleIndex;
        this.previousScale = previousScale;
        this.currentScale = currentScale;
        this.nextScale = nextScale;
    }

    public int getScaleIndex() {
        return scaleIndex;
    }

    public float[][] getPreviousScale() {
        return previousScale;
    }

    public float[][] getCurrentScale() {
        return currentScale;
    }

    public float[][] getNextScale() {
        return nextScale;
    }
}