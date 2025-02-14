package org.example.analyzers.feature;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// TODO: start from the beggining
public class RANSAC {
    private final Random rand;

    private final int NUMBER_OF_ITERATIONS = 500;
    private final double THRESHOLD = 3.0;


    public RANSAC() {
        this.rand = new Random();
    }

    public double[][] estimateHomography(List<FeatureMatch> matches) {
        double[][] bestH = null;
        int maxInliers = 0;

        for (int i=0; i<NUMBER_OF_ITERATIONS; i++) {
            // 1. Select random 4 matches
            List<FeatureMatch> subset = getRandomSubset(matches, 4);

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


    private List<FeatureMatch> getRandomSubset(List<FeatureMatch> list, int subsetSize) {
        List<FeatureMatch> subset = new ArrayList<>();
        while (subset.size() < subsetSize) {
            FeatureMatch match = list.get( rand.nextInt(list.size()) );
            if (!subset.contains(match)) subset.add((match));
        }

        return subset;
    }


    /**
     * Computes homography based on provided matches using SVD to solve the linear system.
     */
    private double[][] computeHomography(List<FeatureMatch> matches) {
        int numberOfPoints = matches.size();
        double[][] A = new double[2 * numberOfPoints][9];

        for (int i=0; i<numberOfPoints; i++) {
            FeatureMatch match = matches.get(i);
            double x1 = match.getKeypoint1().getSubPixelX();
            double y1 = match.getKeypoint1().getSubPixelY();
            double x2 = match.getKeypoint2().getSubPixelX();
            double y2 = match.getKeypoint2().getSubPixelY();

            A[2*i]   = new double[] {-x1, -y1, -1,   0,   0,  0, x1*x2, y1*x2, x2};
            A[2*i+1] = new double[] {  0,   0,  0, -x1, -y1, -1, x1*y2, y1*y2, y2};
        }

        RealMatrix matrixA = new Array2DRowRealMatrix(A);

        SingularValueDecomposition svd = new SingularValueDecomposition(matrixA);
        int lastRowIndex = svd.getVT().getRowDimension() - 1;
        double[] hVector = svd.getVT().getRow(lastRowIndex);

        double[][] H = new double[3][3];
        for (int i = 0; i < 9; i++) {
            H[i / 3][i % 3] = hVector[i];
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                H[i][j] /= H[2][2];
            }
        }

        return H;
    }

    /**
     * Applies provided homography H to all matches, checks if re-projection error is within config threshold.
     * Returns a count of points that satisfy this check (as inliers)
     */
    private int countInliers(List<FeatureMatch> matches, double[][] H) {
        return getInliers(matches, H).size();
    }

    public List<FeatureMatch> getInliers(List<FeatureMatch> matches, double[][] H) {
        List<FeatureMatch> inliers = new ArrayList<>();

        for (FeatureMatch match : matches) {
            double x1 = match.getKeypoint1().getSubPixelX();
            double y1 = match.getKeypoint1().getSubPixelY();
            double x2 = match.getKeypoint2().getSubPixelX();
            double y2 = match.getKeypoint2().getSubPixelY();

            double[] point1Homog = {x1, y1, 1};  // Source point in homogeneous coordinates
            double[] transformedPoint = new double[3];

            for (int i = 0; i < 3; i++) {
                transformedPoint[i] = H[i][0] * point1Homog[0] + H[i][1] * point1Homog[1] + H[i][2] * point1Homog[2];
            }

            double x1Prime = transformedPoint[0] / transformedPoint[2];
            double y1Prime = transformedPoint[1] / transformedPoint[2];
            double error = Math.sqrt(Math.pow(x1Prime - x2, 2) + Math.pow(y1Prime - y2, 2));

            if (error < THRESHOLD) {
                inliers.add(match);
            }
        }

        return inliers;
    }
}
