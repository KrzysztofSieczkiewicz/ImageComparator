package org.example.analyzer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PixelGroup {
    private final int[][] neighboursMatrix;

    public PixelGroup(int groupingRadius) {
        this.neighboursMatrix = generateNeighboursMatrix(groupingRadius);
    }

    public List<Rectangle> listConnectedMismatches(boolean[][] pixels) {
        int X = pixels.length;
        int Y = pixels[0].length;

        List<Rectangle> groups = new ArrayList<>();

        boolean[][] visited = new boolean[X][Y];

        for (int x=0; x<pixels.length; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                if (visited[x][y]) continue;
                if (pixels[x][y]) {
                    groups.add(searchDFS(pixels, visited, x, y));
                }
            }
        }

        return groups;
    }

    /**
     * Performs DFS on provided boolean[][] matrix
     *
     * @param matrix matrix to be searched
     * @param visited matrix of already visited elements
     * @param x X pixel coordinate in the image matrix
     * @param y Y pixel coordinate in the image matrix
     * @return minX,minY maxX,maxY points for each misma
     */
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

    /**
     * verifies if provided pixel can be searched and if contains a mismatch
     *
     * @param matrix matrix that is being searched through
     * @param visited matrix of visited fields
     * @param x X pixel coordinate in the image matrix
     * @param y Y pixel coordinate in the image matrix
     * @return if the pixel can be checked and is it a mismatch
     */
    private boolean checkElement(boolean[][] matrix, boolean[][] visited, int x, int y) {
        int X = matrix.length;
        int Y = matrix[0].length;

        return  x>=0 && y>=0 && x<X && y<Y && !visited[x][y] && matrix[x][y];
    }


    /**
     * Generate index offsets for surrounding entries based on provided distance
     *
     * @param distance half of resulting matrix width/height
     * @return matrix containing two offset arrays - for X and Y coordinates respectively
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