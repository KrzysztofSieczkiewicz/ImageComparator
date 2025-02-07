package org.example.analyzers.feature;

/**
 * Helper class holding three consecutive scales from the octave
 */
public class OctaveSlice {

    private int octaveIndex;
    private float[][] previousScale;
    private float[][] currentScale;
    private float[][] nextScale;

    public OctaveSlice(int octaveIndex, float[][] previousScale, float[][] currentScale, float[][] nextScale) {
        this.octaveIndex = octaveIndex;
        this.previousScale = previousScale;
        this.currentScale = currentScale;
        this.nextScale = nextScale;
    }

    public int getOctaveIndex() {
        return octaveIndex;
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