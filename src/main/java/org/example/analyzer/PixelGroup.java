package org.example.analyzer;

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
                    // ADD ISLAND TO THE LIST HERE
                    count++;
                }

                visited[x][y] = true;
            }
        }

        System.out.println("Mismatched islands: " + count);
    }

    private void searchConnected(boolean[][] matrix, boolean[][] visited, int x, int y) {

        int[] xNeighbours = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] yNeighbours = {-1, 0, 1, -1, 1, -1, 0, 1};

        visited[x][y] = true;

        for (int i=0; i<8; i++) {
            int newX = x + xNeighbours[i];
            int newY = y + yNeighbours[i];

            if (!canBeChecked(matrix, visited, newX, newY)) continue;
            searchConnected(matrix, visited, newX, newY);
        }
    }

    private boolean canBeChecked(boolean[][] matrix, boolean[][] visited, int x, int y) {
        int X = matrix.length;
        int Y = matrix[0].length;

        if (x < 0 || y < 0 || x >= X || y >= Y) return false;

        return !visited[x][y] && matrix[x][y];
    }
}
