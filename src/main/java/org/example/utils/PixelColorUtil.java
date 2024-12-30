package org.example.utils;

public class PixelColorUtil {

    /**
     * Converts RGB space coordinates into HSV (cylindrical)
     *
     * @param rgb ARGB integer (alpha channel is ignored)
     * @return float representing HSV channels
     */
    public static float[] convertRGBtoHSV(int rgb) {
        float H = 0;
        float S = 0;
        float V;

        float red = ((rgb >> 16) & 0xFF) / 255f;
        float green = ((rgb >> 8) & 0xFF) / 255f;
        float blue = (rgb & 0xFF) / 255f;

        float min = Math.min(red, Math.min(green, blue));
        float max = Math.max(red, Math.max(green, blue));
        float delta = max-min;

        V = max;

        if (delta == 0) return new float[]{H,S,V};

        S = delta/max;

        float invDelta = 1 / delta;

        if (red == max)         H = (green - blue) * invDelta;
        else if (green == max)  H = 2 + (blue - red) * invDelta;
        else                    H = 4 + (red - green) * invDelta;

        H /= 6; // Normalize to [0, 1]
        if (H < 0) H += 1;

        return new float[]{H,S,V};
    }

    /**
     * Calculates a distance between two colors in the HSV color space.
     *
     * @return distance between colors in the HSV space
     */
    public static float calculateDistanceHSV(float[] hsv1, float[] hsv2) {
        float deltaH = Math.min(Math.abs(hsv1[0] - hsv2[0]), 1 - Math.abs(hsv1[0] - hsv2[0]));
        float deltaS = hsv1[1] - hsv2[1];
        float deltaV = hsv1[2] - hsv2[2];

        return deltaH * deltaH + deltaS * deltaS + deltaV * deltaV;
    }

    /**
     * Calculates a normalized distance between two colors in the HSV color space.
     *
     * @return normalized distance [0-100] between colors in the HSV space
     */
    public static int normalizedDistanceHSV(float[] hsv1, float[] hsv2) {
        float deltaH = Math.min(Math.abs(hsv1[0] - hsv2[0]), 1 - Math.abs(hsv1[0] - hsv2[0]));
        float deltaS = hsv1[1] - hsv2[1];
        float deltaV = hsv1[2] - hsv2[2];

        float squaredDistance = deltaH * deltaH + deltaS * deltaS + deltaV * deltaV;
        float maxSquaredDistance = 2.25f;

        return Math.round( (squaredDistance / maxSquaredDistance) * 100 );
    }

    /**
     * Calculates a distance between two colors in the RGB color space.
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
     * Calculates a normalized distance between two colors in the RGB color space.
     *
     * @return normalized distance [0-100] between colors in the RGB space
     */
    public static int normalizedDistanceRGB(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;

        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;

        int redDiff = r1-r2;
        int greenDiff = g1 - g2;
        int blueDiff = b1 - b2;

        int squaredDistance = (redDiff * redDiff) + (greenDiff * greenDiff) + (blueDiff * blueDiff);
        int maxSquaredDistance = 255*255*3;
        return (squaredDistance * 100) / maxSquaredDistance;
    }


    /**
     * Calculates a distance between two colors in the RGB color space using weighted channel values
     * Uses fixed, higher weight for green channel
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

    /**
     * Calculates a normalized distance between two colors in the RGB color space using weighted channel values
     * Uses fixed, higher weight for green channel
     *
     * @return normalized distance [0-100] between colors in the RGB space
     */
    public static int normalizedDistanceWeightedRGB(int rgb1, int rgb2) {
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

        int squaredWeightedDistance = (redWeight * redDiff * redDiff) + (greenWeight * greenDiff * greenDiff) + (blueWeight * blueDiff * blueDiff);
        int maxWeightedDistance = (1022 * 255 * 255) + (1024 * 255 * 255) + (1022 * 255 * 255);

        return (squaredWeightedDistance * 100) / maxWeightedDistance;
    }
}
