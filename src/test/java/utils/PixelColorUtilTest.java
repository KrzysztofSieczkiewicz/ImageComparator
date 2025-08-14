package utils;

import com.sieczk.utils.PixelColorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class PixelColorUtilTest {
    private static final float DELTA = 0.0001f;

    @ParameterizedTest
    @CsvSource({
         // RGB value, H1,  S1,  V1
            "0xFF0000, 0.0, 1.0, 1.0",              // Red
            "0x00FF00, 0.33333334, 1.0, 1.0",       // Green
            "0x0000FF, 0.6666667, 1.0, 1.0",        // Blue
            "0xFFFFFF, 0.0, 0.0, 1.0",              // White
            "0x000000, 0.0, 0.0, 0.0",              // Black
            "0x808080, 0.0, 0.0, 0.5019608",        // Grey
            "0x800080, 0.8333333, 1.0, 0.5019608",  // A random purple
    })
    public void testWhiteRGBtoHSV(int rgb, float expectedH, float expectedS, float expectedV) {

        float[] expectedHSV = { expectedH, expectedS, expectedV };
        float[] actualHSV = PixelColorUtil.convertRGBtoHSV(rgb);

        Assertions.assertArrayEquals(expectedHSV, actualHSV, DELTA);
    }

    @ParameterizedTest
    @CsvSource({
          // H1,  S1,  V1,  H2,  S2,  V2,  expDist
            "0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 0",      // Identical colors (red to red)
            "0.0, 1.0, 1.0, 0.5, 1.0, 1.0, 11",     // Different hue, same saturation/value
            "0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 44",     // Different saturation, same hue/value
            "0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 44",     // Different value, same hue/saturation
            "0.0, 1.0, 1.0, 0.5, 0.0, 0.0, 100",    // Completely opposite colors (red to cyan-ish, min sat, min val)
            "0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 44",     // White to black
            "0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0"       // Identical colors (mid-point)
    })
    public void testDistanceHSV(
            float h1, float s1, float v1,
            float h2, float s2, float v2,
            int expectedDistance) {

        float[] hsv1 = new float[]{h1, s1, v1};
        float[] hsv2 = new float[]{h2, s2, v2};
        int actualDistance = PixelColorUtil.normalizedDistanceHSV(hsv1, hsv2);

        Assertions.assertEquals(expectedDistance, actualDistance);
    }

    @ParameterizedTest
    @CsvSource({
          // RGB,      RGB,      expDist
            "0xFF0000, 0xFF0000, 0",      // Red to red
            "0xFF0000, 0x00FF00, 67",     // Red to green
            "0xFF0000, 0x0000FF, 67",     // Red to blue
            "0xFF0000, 0x000000, 33",     // Red to black
            "0xFF0000, 0xFFFFFF, 67",     // Red to white
            "0x000000, 0xFFFFFF, 100",    // Black to white (maximum distance)
            "0xFFFFFF, 0xFFFFFF, 0",      // White to white
            "0x000000, 0x000000, 0",      // Black to black
            "0x808080, 0x808080, 0",      // Gray to gray
            "0x000000, 0x808080, 25"      // Black to gray
    })
    public void testDistanceRGB(int rgb1, int rgb2, int expectedDistance) {
        int actualDistance = PixelColorUtil.normalizedDistanceRGB(rgb1, rgb2);
        Assertions.assertEquals(expectedDistance, actualDistance);
    }

    @ParameterizedTest
    @CsvSource({
          // RGB,      RGB,      expDist
            "0xFF0000, 0xFF0000, 0",      // Red to red
            "0xFF0000, 0x00FF00, 58",     // Red to green
            "0xFF0000, 0x0000FF, 50",     // Red to blue
            "0xFF0000, 0x000000, 25",     // Red to black
            "0xFF0000, 0xFFFFFF, 50",     // Red to white
            "0x000000, 0xFFFFFF, 83",     // Black to white (maximum distance)
            "0xFFFFFF, 0xFFFFFF, 0",      // White to white
            "0x000000, 0x000000, 0",      // Black to black
            "0x808080, 0x808080, 0",      // Gray to gray
            "0x000000, 0x808080, 21"      // Black to gray
    })
    public void testDistanceWeightedRGB(int rgb1, int rgb2, int expectedDistance) {
        int actualDistance = PixelColorUtil.normalizedDistanceWeightedRGB(rgb1, rgb2);
        Assertions.assertEquals(expectedDistance, actualDistance);
    }
}
