package org.example.analyzer;

import java.awt.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Stack;

public class BitSetMismatchManager {

    /*
    TODO: Move ImageWidth and ImageHeight to the global vars - it can be read once from base image and then accessed elsewhere
     */

    private final int[][] neighboursMatrix;

    public BitSetMismatchManager(int groupingRadius) {
        this.neighboursMatrix = generateNeighboursMatrix(groupingRadius);
    }

    public List<Rectangle> groupMismatches(BitSet mismatches, int imageWidth, int imageHeight) {

        boolean[] visited = new boolean[mismatches.size()];
        List<Rectangle> groups = new ArrayList<>();

        for (int x=0; x<imageWidth; x++) {
            for (int y=0; y<imageHeight; y++) {
                int index = x*imageHeight + y;
                if (visited[index]) continue;
                if (!mismatches.get(index)) continue;

                groups.add(bitDFS(mismatches, visited, imageWidth, imageHeight, x, y));

            }
        }
        return groups;
    }

    public Rectangle bitDFS(BitSet mismatches, boolean[] visited, int width, int height, int x, int y) {
        int index = x*width + y;
        final int[] xNeighbours = neighboursMatrix[0];
        final int[] yNeighbours = neighboursMatrix[1];

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{x,y});
        visited[index] = true;

        int minX = x;
        int maxX = x;
        int minY = y;
        int maxY = y;

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int currX = current[0];
            int currY = current[1];

            for (int i=0; i<xNeighbours.length; i++) {
                int newX = currX + xNeighbours[i];
                int newY = currY + yNeighbours[i];

                if (checkBit(mismatches, visited, x, y, index, width, height)) {
                    if (newX > maxX) maxX = newX;
                    else if (newX < minX) minX = newX;
                    if (newY > maxY) maxY = newY;
                    else if (newY < minY) minY = newY;

                    stack.push(new int[]{newX, newY});
                    visited[index] = true;
                }
            }
        }
        return new Rectangle(minX, minY, maxX-minX, maxY-minY);
    }


    // TODO: POSSIBLY DISSOLVE
    private boolean checkBit(BitSet mismatches, boolean[] visited, int x, int y, int index, int width, int height) {
        return  x>=0 && y>=0 && x< width && y< height && !visited[index] && mismatches.get(index);
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
