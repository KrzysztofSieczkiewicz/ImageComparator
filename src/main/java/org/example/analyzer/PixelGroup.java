package org.example.analyzer;

import java.util.Arrays;
import java.util.Stack;

public class PixelGroup {

    public void listConnectedMismatches(boolean[][] pixels) {
        int X = pixels.length;
        int Y = pixels[0].length;

        int count = 0;
        boolean[][] visited = new boolean[X][Y];

        for (int x=0; x<pixels.length; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                if (visited[x][y]) continue;
                if (pixels[x][y]) {
                    searchConnected(pixels, visited, x, y);

                    // ADD ISLAND TO THE LIST INSTEAD
                    count++;
                }
            }
        }
        System.out.println("Mismatched islands: " + count);
    }

    private void searchConnected(boolean[][] matrix, boolean[][] visited, int x, int y) {
        int[][] neighboursMatrix = generateNeighboursMatrix(2);
        final int[] xNeighbours = neighboursMatrix[0];
        final int[] yNeighbours = neighboursMatrix[1];

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{x,y});
        visited[x][y] = true;

        while(!stack.isEmpty()) {
            int[] current = stack.pop();
            int currX = current[0];
            int currY = current[1];

            for (int i=0; i<8; i++) {
                int newX = currX + xNeighbours[i];
                int newY = currY + yNeighbours[i];

                if (canBeChecked(matrix, visited, newX, newY)) {
                    stack.push(new int[]{newX, newY});
                    visited[newX][newY] = true;
                }
            }
        }
    }

    private boolean canBeChecked(boolean[][] matrix, boolean[][] visited, int x, int y) {
        int X = matrix.length;
        int Y = matrix[0].length;

        return  x>=0 && y>=0 && x<X && y<Y && !visited[x][y] && matrix[x][y];
    }

    // TODO: MOVE THIS TO A SINGLE, ON-CONSTRUCTOR GENERATION BASED ON CONFIG FILE
    private int[][] generateNeighboursMatrix(int neighbourDistance) {
        int size = (2 * neighbourDistance + 1) * (2 * neighbourDistance + 1) - 1;
        int[] xNeighbours = new int[size];
        int[] yNeighbours = new int[size];

        int index = 0;
        for (int dx = -neighbourDistance; dx <= neighbourDistance; dx++) {
            for (int dy = -neighbourDistance; dy <= neighbourDistance; dy++) {
                if (dx == 0 && dy == 0) continue;
                xNeighbours[index] = dx;
                yNeighbours[index] = dy;
                index++;
            }
        }

        return new int[][]{xNeighbours, yNeighbours};
    }
}