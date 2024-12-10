package org.example.mismatchMarker;

import java.awt.*;
import java.util.*;
import java.util.List;

// TODO: FIX THIS!
public class MismatchManager {
    private final int[][] neighboursMatrix = generateNeighboursMatrix(1);

    /**
     * Joins connected mismatched pixels into single group bound by a rectangle
     *
     * @param mismatches HashSet containing mismatched pixels
     * @return List of rectangles bounding mismatched pixels groups
     */
    public List<Rectangle> groupMismatches(HashSet<PixelPoint> mismatches) {
        List<Rectangle> groups = new ArrayList<>();
        HashSet<PixelPoint> visited = new HashSet<>();

        for (PixelPoint mismatch : mismatches) {
            if (visited.contains(mismatch)) continue;
            groups.add(searchDFS(mismatches, visited, mismatch.getX(), mismatch.getY()));
        }

        return groups;
    }

    /**
     * Performs DFS on provided HashSet of mismatches
     *
     * @param matrix HashSet of points to be searched
     * @param visited HashSet of already visited elements
     * @param x X pixel coordinate
     * @param y Y pixel coordinate
     * @return the bounding rectangle for the mismatched group
     */
    private Rectangle searchDFS(HashSet<PixelPoint> matrix, HashSet<PixelPoint> visited, int x, int y) {
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


// TODO: If sure that matrices won't return - delete everything below
/*
package org.example.mismatchMarker;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MismatchManager {
    private final int[][] neighboursMatrix;

    public MismatchManager(int groupingRadius) {
        this.neighboursMatrix = generateNeighboursMatrix(groupingRadius);
    }

    public List<Rectangle> groupMismatches(boolean[][] mismatches) {
        int X = mismatches.length;
        int Y = mismatches[0].length;

        List<Rectangle> groups = new ArrayList<>();

        boolean[][] visited = new boolean[X][Y];

        for (int x=0; x<mismatches.length; x++) {
            for (int y = 0; y < mismatches[0].length; y++) {
                if (visited[x][y]) continue;
                if (!mismatches[x][y]) continue;
                groups.add(searchDFS(mismatches, visited, x, y));
            }
        }

        return groups;
    }

private Rectangle searchDFS(boolean[][] matrix, boolean[][] visited, int x, int y) {
    final int[] xNeighbours = neighboursMatrix[0];
    final int[] yNeighbours = neighboursMatrix[1];

    Stack<int[]> stack = new Stack<>();
    stack.push(new int[]{x,y});
    visited[x][y] = true;

    int minX = x;
    int maxX = x;
    int minY = y;
    int maxY = y;

    while(!stack.isEmpty()) {
        int[] current = stack.pop();
        int currX = current[0];
        int currY = current[1];

        for (int i=0; i<xNeighbours.length; i++) {
            int newX = currX + xNeighbours[i];
            int newY = currY + yNeighbours[i];

            if (checkElement(matrix, visited, newX, newY)) {
                if (newX > maxX) maxX = newX;
                else if (newX < minX) minX = newX;
                if (newY > maxY) maxY = newY;
                else if (newY < minY) minY = newY;

                stack.push(new int[]{newX, newY});
                visited[newX][newY] = true;
            }
        }
    }

    return new Rectangle(minX, minY, maxX-minX, maxY-minY);
}


    private boolean checkElement(boolean[][] matrix, boolean[][] visited, int x, int y) {
        int X = matrix.length;
        int Y = matrix[0].length;

        return  x>=0 && y>=0 && x<X && y<Y && !visited[x][y] && matrix[x][y];
    }

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
*/