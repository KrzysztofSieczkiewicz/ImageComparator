//package org.example.comparator;
//
//import java.util.List;
//
//public class MismatchesList {
//    private final int totalMismatched;
//    private boolean[][] mismatchedPixels;
//
//    public Mismatches(boolean[][] mismatchedPixels, int totalMismatched) {
//        this.totalMismatched = totalMismatched;
//        this.mismatchedPixels = mismatchedPixels;
//    }
//
//    public int getMismatchesCount() {
//        return totalMismatched;
//    }
//
//    public boolean[][] getPixels() {
//        return mismatchedPixels;
//    }
//
//    public void setMismatchedPixels(boolean[][] mismatchedPixels) {
//        this.mismatchedPixels = mismatchedPixels;
//    }
//
//    public void excludeResults(boolean[][] excluded) {
//        for (int x=0; x<mismatchedPixels.length; x++) {
//            for (int y=0; y<mismatchedPixels[0].length; y++) {
//                if (excluded[x][y]) mismatchedPixels[x][y] = false;
//            }
//        }
//    }
//}
//
