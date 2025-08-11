package analyzers.direct;

import org.example.analyzers.ExcludedAreas;
import org.example.analyzers.common.PixelPoint;
import org.example.analyzers.direct.Mismatches;
import org.example.analyzers.direct.MismatchesGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class MismatchesTest {
    private ArrayList<PixelPoint> mismatchedPixels;
    private Mismatches mismatches;
    private final int GROUPING_RADIUS = 1;

    @BeforeEach
    void setUp() {
        mismatchedPixels = new ArrayList<>();
        mismatches = new Mismatches(mismatchedPixels, GROUPING_RADIUS);
    }

    @Test
    void excludeResults_excludedPixels() {
        mismatchedPixels.add(new PixelPoint(10, 10));
        mismatchedPixels.add(new PixelPoint(20, 20));
        mismatchedPixels.add(new PixelPoint(30, 30));

        ExcludedAreas excluded = new ExcludedAreas();
        excluded.excludeArea(new Rectangle(15, 15, 10, 10));

        mismatches.excludeResults(excluded);

        Assertions.assertEquals(2, mismatches.getMismatchedPixels().size());
        Assertions.assertFalse(mismatches.getMismatchedPixels().contains(new PixelPoint(20, 20)));
        Assertions.assertTrue(mismatches.getMismatchedPixels().contains(new PixelPoint(10, 10)));
        Assertions.assertTrue(mismatches.getMismatchedPixels().contains(new PixelPoint(30, 30)));
    }

    @Test
    void groupMismatches_connectOrthogonalNeighbours() {
        // a group of 3 pixels
        mismatchedPixels.add(new PixelPoint(10, 10));
        mismatchedPixels.add(new PixelPoint(11, 10));
        mismatchedPixels.add(new PixelPoint(12, 10));

        // an isolated pixel
        mismatchedPixels.add(new PixelPoint(20, 20));

        mismatches = new Mismatches(mismatchedPixels, GROUPING_RADIUS);
        List<MismatchesGroup> groups = mismatches.groupMismatches();

        Assertions.assertEquals(2, groups.size());

        MismatchesGroup group1 = groups.get(0);
        Assertions.assertEquals(3, group1.size());
        Assertions.assertEquals(10, group1.minX());
        Assertions.assertEquals(12, group1.maxX());
        Assertions.assertEquals(10, group1.minY());
        Assertions.assertEquals(10, group1.maxY());

        MismatchesGroup group2 = groups.get(1);
        Assertions.assertEquals(1, group2.size());
        Assertions.assertEquals(20, group2.minX());
        Assertions.assertEquals(20, group2.maxX());
        Assertions.assertEquals(20, group2.minY());
        Assertions.assertEquals(20, group2.maxY());
    }

    @Test
    void groupMismatches_connectDiagonalNeighbours() {
        mismatchedPixels.add(new PixelPoint(10, 10));
        mismatchedPixels.add(new PixelPoint(11, 11));
        mismatchedPixels.add(new PixelPoint(12, 12));

        mismatches = new Mismatches(mismatchedPixels, GROUPING_RADIUS);

        List<MismatchesGroup> groups = mismatches.groupMismatches();

        Assertions.assertEquals(1, groups.size());
        MismatchesGroup group = groups.get(0);
        Assertions.assertEquals(3, group.size());
        Assertions.assertEquals(10, group.minX());
        Assertions.assertEquals(12, group.maxX());
        Assertions.assertEquals(10, group.minY());
        Assertions.assertEquals(12, group.maxY());
    }

    @Test
    void groupMismatches_emptyGroup() {
        List<MismatchesGroup> groups = mismatches.groupMismatches();
        Assertions.assertTrue(groups.isEmpty());
    }

    @Test
    void constructor_shouldInitializeFieldsCorrectly() {
        ArrayList<PixelPoint> pixels = new ArrayList<>();
        pixels.add(new PixelPoint(1, 1));
        pixels.add(new PixelPoint(2, 2));

        Mismatches testMismatches = new Mismatches(pixels, 2);

        Assertions.assertEquals(pixels, testMismatches.getMismatchedPixels());
        Assertions.assertEquals(1, testMismatches.getMismatchesCount());
    }
}
