package org.example.utils;

public class PixelColorUtil {

    /**
     * Compute a distance between provided colors. More significant difference between two colors results in larger distance.
     * Uses fixed and increased weight for green channel as it's more important for human eye. Identical colors result in distance equal to 0 (or very close)
     * <p>
     * Performs calculations in the integer space and converts to the double at the end
     *
     * @param color1 the first color
     * @param color2 the second color
     * @return distance between colors as a double
     */
    public static double calculateColorDistance(java.awt.Color color1, java.awt.Color color2) {
        // Retrieve red channels
        int r1 = color1.getRed();
        int r2 = color2.getRed();

        // Calculate difference per channel
        int redDiff = r1-r2;
        int greenDiff = color1.getGreen() - color2.getGreen();
        int blueDiff = color1.getBlue() - color2.getBlue();

        // Mean of red channel
        int redMean = (r1 + r2) >> 1;

        // Calculate weighted squared distance
        int redWeight = 512 + redMean;   // simplified from "(512+redMean) / 256"
        int greenWeight = 1024;          // fixed, higher weight for green
        int blueWeight = 767 - redMean;  // simplified from "2 + ((255-redMean) / 256)"

        // Calculate weighted distance
        int weightedDistance = (redWeight * redDiff * redDiff) + (greenWeight * greenDiff * greenDiff) + (blueWeight * blueDiff * blueDiff);

        return Math.sqrt(weightedDistance / 256.0);
    }


}
