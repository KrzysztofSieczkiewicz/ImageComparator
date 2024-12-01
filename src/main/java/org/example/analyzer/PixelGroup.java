package org.example.analyzer;

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
        final int[] xNeighbours = {-1, -1, -1, 0, 0, 1, 1, 1};
        final int[] yNeighbours = {-1, 0, 1, -1, 1, -1, 0, 1};

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
}
