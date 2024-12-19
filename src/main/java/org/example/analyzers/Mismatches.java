package org.example.analyzers;

import org.example.mismatchMarker.PixelPoint;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Mismatches {
    private final int[][] neighboursMatrix = generateNeighboursMatrix(3);

    private final int totalMismatched;
    private final ArrayList<PixelPoint> mismatchedPixels;

    public Mismatches(ArrayList<PixelPoint> mismatchedPixels) {
        this.totalMismatched = mismatchedPixels.size() -1;
        this.mismatchedPixels = mismatchedPixels;
    }

    public ArrayList<PixelPoint> getPixels() {
        return mismatchedPixels;
    }

    public int getMismatchesCount() {
        return totalMismatched;
    }

    /**
     * Excludes provided area from mismatches
     *
     * @param excluded ExcludedAreas object
     */
    public void excludePixels(ExcludedAreas excluded) {
        mismatchedPixels.removeIf(pixelPoint -> excluded.contains(
                pixelPoint.getX(), pixelPoint.getY()
        ));
    }

    /**
     * Joins connected mismatched pixels into single group bound by a rectangle
     *
     * @param mismatches ArrayList containing mismatched pixels
     * @return List of rectangles bounding mismatched pixels groups
     */
    public List<Rectangle> groupMismatches(ArrayList<PixelPoint> mismatches) {
        List<Rectangle> groups = new ArrayList<>();
        ArrayList<PixelPoint> visited = new ArrayList<>();

        for (PixelPoint mismatch : mismatches) {
            if (visited.contains(mismatch)) continue;
            groups.add(searchDFS(mismatches, visited, mismatch.getX(), mismatch.getY()));
        }

        return groups;
    }

    /**
     * Performs DFS on provided ArrayList of mismatches
     *
     * @param matrix ArrayList of points to be searched
     * @param visited ArrayList of already visited elements
     * @param x X pixel coordinate
     * @param y Y pixel coordinate
     * @return the bounding rectangle for the mismatched group
     */
    private Rectangle searchDFS(ArrayList<PixelPoint> matrix, ArrayList<PixelPoint> visited, int x, int y) {
        final int[] xNeighbours = neighboursMatrix[0];
        final int[] yNeighbours = neighboursMatrix[1];

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{x, y});

        visited.add(new PixelPoint(x,y));

        int minX = x;
        int maxX = x;
        int minY = y;
        int maxY = y;

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int currX = current[0];
            int currY = current[1];

            for (int i = 0; i < xNeighbours.length; i++) {
                int newX = currX + xNeighbours[i];
                int newY = currY + yNeighbours[i];
                PixelPoint neighbourPoint = new PixelPoint(newX, newY);

                if (!matrix.contains(neighbourPoint)) continue;
                if (visited.contains(neighbourPoint)) continue;

                if (newX > maxX) maxX = newX;
                else if (newX < minX) minX = newX;
                if (newY > maxY) maxY = newY;
                else if (newY < minY) minY = newY;

                stack.push(new int[]{newX, newY});
                visited.add(neighbourPoint);
            }
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }


    /**
     * Generate index offsets for surrounding entries based on provided distance
     *
     * @param distance half of the resulting matrix width/height
     * @return matrix containing two offset arrays for X and Y coordinates respectively
     */
    private int[][] generateNeighboursMatrix(int distance) {
        int size = (2 * distance + 1) * (2 * distance + 1) - 1;
        int[] xNeighbours = new int[size];
        int[] yNeighbours = new int[size];

        int index = 0;
        for (int dx = -distance; dx <= distance; dx++) {
            for (int dy = -distance; dy <= distance; dy++) {
                if (dx == 0 && dy == 0) continue;
                xNeighbours[index] = dx;
                yNeighbours[index] = dy;
                index++;
            }
        }

        return new int[][]{xNeighbours, yNeighbours};
    }
}
