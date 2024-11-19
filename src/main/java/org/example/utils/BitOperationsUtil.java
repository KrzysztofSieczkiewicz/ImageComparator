package org.example.utils;

public class BitOperationsUtil {

    /**
     * Finds the first (least significant) bit that is set to 1
     * @param mask mask to be searched
     * @return index of the least significant set bit
     */
    public static int findFirstSetBitIndex(int mask) {
        // Integer.numberOfTrailingZeros gives the index of the first set bit (0-based)
        return (mask == 0) ? -1 : Integer.numberOfTrailingZeros(mask);
    }

}
