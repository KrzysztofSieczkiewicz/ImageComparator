package org.example.utils;

public class PixelColorUtil {

    // TODO: Ensure that HSV returned from convertRGBtHSV is returned normalized
    // meaning that H is between 0 and 360
    // S is between 0 and 1
    // V is between 0 and 1
    /**
     * Converts RGB space coordinates into HSV (cylindrical)
     * @param rgb rgb integer
     * @return float representing HSV channels
     */
    public static float[] convertRGBtoHSV(int rgb) {
        float H = 0;
        float S = 0;
        float V;

        float red_calc = ((rgb >> 16) & 0xFF) / 255f;
        float green_calc = ((rgb >> 8) & 0xFF) / 255f;
        float blue_calc = (rgb & 0xFF) / 255f;

        float min = Math.min(red_calc, Math.min(green_calc, blue_calc));
        float max = Math.max(red_calc, Math.max(green_calc, blue_calc));
        float delta = max-min;

        V = max;

        if (delta == 0) return new float[]{H,S,V};

        S = delta/max;

        float deltaR = ( (max-red_calc)/6f + (delta/2f) ) / delta;
        float deltaG = ( (max-green_calc)/6f + (delta/2f) ) / delta;
        float deltaB = ( (max-blue_calc)/6f + (delta/2f) ) / delta;

        if (red_calc == max) H = deltaB - deltaG;
        else if (green_calc == max) H = (1/3f) + deltaR - deltaB;
        else if (blue_calc == max) H = (2/3f) + deltaG - deltaR;

        if (H < 0) H+=1;
        if (H > 1) H-=1;

        return new float[]{H,S,V};
    }

    /**
     * Calculates a distance between provided colors in the HSV color space.
     * Black-White max distance is 0.003921
     *
     * @return squared distance between colors in the HSV space
     */
    public static float calculateDistanceHSV(float[] hsv1, float[] hsv2) {
        float diffH = Math.abs(hsv1[0] - hsv2[0]);
        float deltaH = Math.min(diffH, 360 - diffH);
        float deltaS = hsv1[1]-hsv2[1];
        float deltaV = hsv1[2]-hsv2[2];

        return deltaH*deltaH + deltaS*deltaS + deltaV*deltaV;
    }

    /**
     * Calculates a distance between provided colors in the RGB color space.
     * Maximum distance is 441.67
     *
     * @return distance between colors in the RGB space
     */
    public static float calculateDistanceRGB(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;

        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;

        int redDiff = r1-r2;
        int greenDiff = g1 - g2;
        int blueDiff = b1 - b2;

        return (redDiff * redDiff) + (greenDiff * greenDiff) + (blueDiff * blueDiff);
    }

    /**
     * Calculates a distance between provided colors in the RGB color space using weighted channel values
     * Uses fixed and increased weight for green channel as it's more important for human eye.
     * Maximum distance is 50.38
     *
     * @return distance between colors in the RGB space
     */
    public static float calculateDistanceWeightedRGB(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;

        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;

        int redDiff = r1-r2;
        int greenDiff = g1 - g2;
        int blueDiff = b1 - b2;

        int redMean = (r1 + r2) >> 1;

        // Calculated color weights (multiplied by 256 to keep them as int)
        int redWeight = 512 + (redMean << 1);
        int greenWeight = 1024;
        int blueWeight = 512 + ((255 - redMean) << 1);

        int weightedColors = (redWeight * redDiff * redDiff) + (greenWeight * greenDiff * greenDiff) + (blueWeight * blueDiff * blueDiff);

        return weightedColors / 65536f;
    }
}
