package org.example.analyzers.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RANSAC {

    private final int NUMBER_OF_ITERATIONS = 1000;
    private final double THRESHOLD = 3.0;

    public double[][] estimateHomography(List<FeatureMatch> matches) {
        if (matches.size() < 4) {
            throw new IllegalArgumentException("At least 4 matches are required for homography estimation");
        }

        double[][] bestH = null;
        int maxInliers = 0;

        Random rand = new Random();

        for (int i=0; i<NUMBER_OF_ITERATIONS; i++) {
            // 1. Select random 4 matches
            List<FeatureMatch> subset = getRandomSubset(matches, 4, rand);

            // 2. Compute homography
            double[][] H = computeHomography(subset);

            // 3. Count inliers
            int inliers = countInliers(matches, H);

            // 4. Keep save the best homography
            if (inliers > maxInliers) {
                maxInliers = inliers;
                bestH = H;
            }
        }

        // 5. Refine the output using inliers
        List<FeatureMatch> inlierMatches = getInliers(matches, bestH);
        return computeHomography(inlierMatches);
    }

    private List<FeatureMatch> getRandomSubset(List<FeatureMatch> list, int subsetSize, Random rand) {
        List<FeatureMatch> subset = new ArrayList<>();
        while (subset.size() < subsetSize) {
            FeatureMatch match = list.get( rand.nextInt(list.size()) );
            if (!subset.contains(match)) subset.add((match));
        }

        return subset;
    }

    private double[][] computeHomography(List<FeatureMatch> matches) {
        int n = matches.size();
        double[][] A = new double[2*n][9];

        for(int i=0; i<n; i++) {
            Keypoint k1 = matches.get(i).getKeypoint1();
            Keypoint k2 = matches.get(i).getKeypoint2();

            double x1 = k1.getSubPixelX(), y1 = k1.getSubPixelY();
            double x2 = k2.getSubPixelX(), y2 = k2.getSubPixelY();

            A[2*i]   = new double[] {-x1, -y1, -1,   0,  0,  0, x1*x2, y1*x2, x2};
            A[2*i+1] = new double[] {  0,   0,  0, -x1,-y1, -1, x2*y2, y1*y2, y2};
        }

        // TODO: implement in the matrixUtil
        double[][] H = solveHomography(A);

        for (int j=0; j<9; j++) {
            H[j/3][j%3] /= H[2][2];
        }

        return H;
    }

    private int countInliers(List<FeatureMatch> matches, double[][] H) {
        int count = 0;
        for (FeatureMatch match: matches) {
            if (isInlier(match, H)) count++;
        }
        return count;
    }

    private boolean isInlier(FeatureMatch match, double[][] H) {
        Keypoint k1 = match.getKeypoint1();
        Keypoint k2 = match.getKeypoint2();

        double[] projected = applyHomography(H, k1.getSubPixelX(), k1.getSubPixelY());
        double dx = projected[0] - k2.getSubPixelX();
        double dy = projected[0] - k2.getSubPixelY();

        return Math.sqrt( dx*dx + dy+dy) < THRESHOLD;
    }

    private double[] applyHomography(double[][] H, double x, double y) {
        double w = H[2][0]*x + H[2][1]*y + H[2][2];
        double newX = (H[0][0]*x + H[0][1]*y + H[0][2])/w;
        double newY = (H[1][0]*x + H[1][1]*y + H[1][2])/w;
        return new double[]{newX, newY};
    }

    private List<FeatureMatch> getInliers(List<FeatureMatch> matches, double[][] H) {
        List<FeatureMatch> inliers = new ArrayList<>();
        for (FeatureMatch match : matches) {
            if (isInlier(match, H)) inliers.add(match);
        }
        return inliers;
    }
}
