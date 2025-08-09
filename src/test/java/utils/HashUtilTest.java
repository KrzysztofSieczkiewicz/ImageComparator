package utils;

import org.example.utils.HashUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.BitSet;

public class HashUtilTest {
    private static final float DELTA = 0.0001f;

    @Test
    public void testCalculateHammingDistance_zeroDistance() {
        BitSet hash1 = BitSet.valueOf(new byte[]{0b0101, 0b1010});
        BitSet hash2 = BitSet.valueOf(new byte[]{0b0101, 0b1010});

        int distance = HashUtil.calculateHammingDistance(hash1, hash2);

        Assertions.assertEquals(0, distance);
    }

    @Test
    public void testCalculateHammingDistance_nonZeroDistance() {
        BitSet hash1 = new BitSet();
        hash1.set(0);
        hash1.set(2);
        hash1.set(5);

        BitSet hash2 = new BitSet();
        hash2.set(0);
        hash2.set(1);
        hash2.set(5);

        int distance = HashUtil.calculateHammingDistance(hash1, hash2);

        Assertions.assertEquals(2, distance);
    }

    @Test
    public void testCalculateHammingDistance_maxDistance() {
        BitSet hash1 = new BitSet(8);
        hash1.set(0, 8);

        BitSet hash2 = new BitSet(8);

        int distance = HashUtil.calculateHammingDistance(hash1, hash2);

        Assertions.assertEquals(8, distance);
    }

    @Test
    public void testCalculateHammingDistance_differentLength() {
        BitSet hash1 = new BitSet(8);
        hash1.set(0);
        hash1.set(2);

        BitSet hash2 = new BitSet(10);
        hash2.set(0);
        hash2.set(2);
        hash2.set(5);

        int distance = HashUtil.calculateHammingDistance(hash1, hash2);
        Assertions.assertEquals(1, distance);

        hash1 = new BitSet(10);
        hash2.set(0);
        hash2.set(2);
        hash2.set(5);

        hash2 = new BitSet(8);
        hash1.set(0);
        hash1.set(2);
        hash1.set(3);
        distance = HashUtil.calculateHammingDistance(hash1, hash2);

        Assertions.assertEquals(3, distance);
    }

    @ParameterizedTest
    @CsvSource({
            "0  , 10, 1.0",
            "10 , 10, 0.9",
            "7  , 5 , 0.72",
            "150, 25, 0.76",
    })
    public void testCalculateSimilarity(int hammingDistance, int reducedImageSize, double expectedSimilarity) {
        double actualSimilarity = HashUtil.calculateSimilarity(hammingDistance, reducedImageSize);

        Assertions.assertEquals(expectedSimilarity, actualSimilarity, DELTA);
    }
}
