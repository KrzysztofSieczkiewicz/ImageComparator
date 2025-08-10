package analyzers.direct;

import org.example.analyzers.common.PixelPoint;
import org.example.analyzers.direct.ColorSpace;
import org.example.analyzers.direct.DirectAnalyzer;
import org.example.analyzers.direct.Mismatches;
import org.example.comparators.DirectComparatorConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectAnalyzerTest {

    private DirectComparatorConfig defaultConfig;
    private DirectComparatorConfig fastCompareConfig;

    @BeforeEach
    void setUp() {
        defaultConfig = new DirectComparatorConfig()
                .colorDistanceThreshold(10)
                .colorSpace(ColorSpace.RGB);
        fastCompareConfig = new DirectComparatorConfig()
                .pixelsSkipped(1)
                .colorDistanceThreshold(10)
                .colorSpace(ColorSpace.RGB);
//        weightedRgbConfig = new DirectComparatorConfig()
//                .colorDistanceThreshold(10)
//                .colorSpace(ColorSpace.WEIGHTED_RGB);
//        hsvConfig = new DirectComparatorConfig()
//                .colorDistanceThreshold(10)
//                .colorSpace(ColorSpace.HSV);
    }

    @Test
    void testCompare_RGB() {
        DirectAnalyzer analyzer = new DirectAnalyzer(defaultConfig);
        BufferedImage actual = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        BufferedImage checked = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        actual.setRGB(0, 0, 0xc86464);
        checked.setRGB(0, 0, 0x3c6464);
        Mismatches mismatches = analyzer.compare(actual, checked);

        Assertions.assertEquals(Collections.emptyList(), mismatches.getMismatchedPixels());

        actual.setRGB(1, 0, 0xc86464);
        checked.setRGB(1, 0, 0x366464);
        mismatches = analyzer.compare(actual, checked);

        Assertions.assertNotEquals(Collections.emptyList(), mismatches.getMismatchedPixels());
    }

    @Test
    void testCompare_HSV() {
        DirectComparatorConfig config = new DirectComparatorConfig()
                .colorDistanceThreshold(10)
                .colorSpace(ColorSpace.HSV);

        DirectAnalyzer analyzer = new DirectAnalyzer(config);
        BufferedImage actual = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        BufferedImage checked = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        actual.setRGB(0, 0, 0x6666cc);
        checked.setRGB(0, 0, 0x2a2a53);
        Mismatches mismatches = analyzer.compare(actual, checked);

        Assertions.assertEquals(Collections.emptyList(), mismatches.getMismatchedPixels());

        actual.setRGB(1, 0, 0x6666cc);
        checked.setRGB(1, 0, 0x27274d);
        mismatches = analyzer.compare(actual, checked);

        Assertions.assertNotEquals(Collections.emptyList(), mismatches.getMismatchedPixels());
    }

    @Test
    void testCompare_weightedRGB() {
        DirectComparatorConfig config = new DirectComparatorConfig()
                .colorDistanceThreshold(10)
                .colorSpace(ColorSpace.WEIGHTED_RGB);

        DirectAnalyzer analyzer = new DirectAnalyzer(config);
        BufferedImage actual = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        BufferedImage checked = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);

        actual.setRGB(0, 0, 0xc86464);
        checked.setRGB(0, 0, 0x286464);
        Mismatches mismatches = analyzer.compare(actual, checked);

        Assertions.assertEquals(Collections.emptyList(), mismatches.getMismatchedPixels());

        actual.setRGB(1, 0, 0xc86464);
        checked.setRGB(1, 0, 0x206464);
        mismatches = analyzer.compare(actual, checked);

        Assertions.assertNotEquals(Collections.emptyList(), mismatches.getMismatchedPixels());
    }

    @Test
    void testCompare_noMismatch() {
        DirectAnalyzer analyzer = new DirectAnalyzer(defaultConfig);
        BufferedImage actual = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        BufferedImage checked = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        actual.setRGB(0, 0, 0xFF0000);
        checked.setRGB(0, 0, 0xFF0000);

        Mismatches mismatches = analyzer.compare(actual, checked);

        Assertions.assertEquals(Collections.emptyList(), mismatches.getMismatchedPixels());
    }

    @Test
    void testCompare_fullMismatch() {
        DirectAnalyzer analyzer = new DirectAnalyzer(defaultConfig);
        BufferedImage actual = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        BufferedImage checked = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        actual.setRGB(0, 0, 0x000000);
        checked.setRGB(0, 0, 0xFFFFFF);

        Mismatches mismatches = analyzer.compare(actual, checked);

        Assertions.assertEquals(1, mismatches.getMismatchedPixels().size());
    }

    @Test
    void testCompareEveryNth_noMismatchesWithGap_returnsEmptyList() {
        DirectAnalyzer analyzer = new DirectAnalyzer(fastCompareConfig);
        BufferedImage actual = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        BufferedImage checked = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);

        Mismatches mismatches = analyzer.compareEveryNth(actual, checked);

        Assertions.assertEquals(Collections.emptyList(), mismatches.getMismatchedPixels());
    }

    @Test
    void testCompareEveryNth_mismatchesWithGap_returnsOnlySpecificPoints() {
        DirectAnalyzer analyzer = new DirectAnalyzer(fastCompareConfig);
        BufferedImage actual = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        BufferedImage checked = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);

        actual.setRGB(0, 0, 0x000000);
        checked.setRGB(0, 0, 0xFFFFFF);
        actual.setRGB(2, 2, 0x000000);
        checked.setRGB(2, 2, 0xFFFFFF);

        List<PixelPoint> expectedMismatches = new ArrayList<>();
        expectedMismatches.add(new PixelPoint(0, 0));
        expectedMismatches.add(new PixelPoint(2, 2));

        Mismatches mismatches = analyzer.compareEveryNth(actual, checked);

        Assertions.assertEquals(expectedMismatches.size(), mismatches.getMismatchedPixels().size());
        for (PixelPoint p : expectedMismatches) {
            assert(mismatches.getMismatchedPixels().contains(p));
        }
    }
}
