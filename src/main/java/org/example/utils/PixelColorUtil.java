package org.example.utils;

public class PixelColorUtil {

    private void convertRGBtoHSV(short red, short blue, short green) {
        float H = 0;
        float S = 0;
        float V = 0;

        float red_calc = (float) red / 255;
        float green_calc = (float) green / 255;
        float blue_calc = (float) blue / 255;

        float min = Math.min(red_calc, Math.min(green_calc, blue_calc));
        float max = Math.max(red_calc, Math.max(green_calc, blue_calc));
        float delta = max-min;

        V = max;

        if (max == 0) return;

        S = delta/max;

        float deltaR = ( (max-red_calc)/6 + (delta/2) ) / delta;
        float deltaG = ( (max-green_calc)/6 + (delta/2) ) / delta;
        float deltaB = ( (max-blue_calc)/6 + (delta/2) ) / delta;

        if (red_calc == max) H = deltaB - deltaG;
        else if (green_calc == max) H = ((float) 1 /3) + deltaR - deltaB;
        else if (blue_calc == max) H = ((float) 2 /3) + deltaG - deltaR;

        if (H < 0) H+=1;
        if (H > 1) H-=1;
    }

    private void calculateDistanceHSV(float hue1, float sat1, float val1, float hue2, float sat2, float val2) {
        float deltaH = Math.min(hue1-hue2, hue1-hue2) / 100f;
        float deltaS = sat1-sat2;
        float deltaV = val1-val2 / 255f;

        double distance = Math.sqrt(deltaH*deltaH + deltaS*deltaS + deltaV*deltaV);
    }

    private void convertRGBtoXYZ(short red, short blue, short green) {
        float red_calc = (float) red / 255;
        float green_calc = (float) green / 255;
        float blue_calc = (float) blue / 255;

        double redCoeff = (red_calc > 0.04045) ? Math.pow((red_calc+0.055) / 1.055, 2.4) : (red_calc/12.92);
        double greenCoeff = (green_calc > 0.04045) ? Math.pow((green_calc+0.055) / 1.055, 2.4) : (green_calc/12.92);
        double blueCoeff = (blue_calc > 0.04045) ? Math.pow((blue_calc+0.055) / 1.055, 2.4) : (blue_calc/12.92);

        redCoeff = redCoeff * 100;
        greenCoeff = greenCoeff * 100;
        blueCoeff = blueCoeff * 100;

        double x = (redCoeff * 0.4124) + (greenCoeff * 0.3576) + (blueCoeff * 0.1805);
        double y = (redCoeff * 0.2126) + (greenCoeff * 0.7152) + (blueCoeff * 0.0722);
        double z = (redCoeff * 0.0193) + (greenCoeff * 0.1192) + (blueCoeff * 0.9505);
    }

    private void convertXYZtoLab(double X, double Y, double Z) {

    }

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
