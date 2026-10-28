package com.rominntrenger.maploader;

import java.util.ArrayList;
import java.util.List;

/**
 * Covers the wall tiles of a map with as few rectangles as possible, so each rectangle can get one
 * collider instead of one per tile
 */
public class WallRectangles {

    /**
     * A rectangle of tiles, from (left, top) to (right, bottom), inclusive
     */
    public record Rect(int left, int top, int right, int bottom) {

        public int width() {
            return right - left + 1;
        }

        public int height() {
            return bottom - top + 1;
        }
    }

    /**
     * Each row of walls is stretched as far right as it goes, then as far down as the whole row
     * allows. Every wall tile ends up in exactly one rectangle
     *
     * @param walls walls[x][y] is true for wall tiles
     */
    public static List<Rect> find(boolean[][] walls) {
        List<Rect> rects = new ArrayList<>();
        if (walls.length == 0) {
            return rects;
        }
        int width = walls.length;
        int height = walls[0].length;
        boolean[][] covered = new boolean[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!isUncoveredWall(walls, covered, x, y)) {
                    continue;
                }

                int right = x;
                while (right + 1 < width && isUncoveredWall(walls, covered, right + 1, y)) {
                    right++;
                }

                int bottom = y;
                while (bottom + 1 < height && isRowOfUncoveredWalls(walls, covered, x, right, bottom + 1)) {
                    bottom++;
                }

                for (int i = x; i <= right; i++) {
                    for (int j = y; j <= bottom; j++) {
                        covered[i][j] = true;
                    }
                }
                rects.add(new Rect(x, y, right, bottom));
            }
        }
        return rects;
    }

    private static boolean isUncoveredWall(boolean[][] walls, boolean[][] covered, int x, int y) {
        return walls[x][y] && !covered[x][y];
    }

    private static boolean isRowOfUncoveredWalls(boolean[][] walls, boolean[][] covered, int left,
                                                 int right, int y) {
        for (int x = left; x <= right; x++) {
            if (!isUncoveredWall(walls, covered, x, y)) {
                return false;
            }
        }
        return true;
    }
}
