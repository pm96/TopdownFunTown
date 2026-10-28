package com.rominntrenger.maploader;

import com.rominntrenger.maploader.WallRectangles.Rect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WallRectanglesTest {

    /**
     * Builds walls[x][y] from rows of text, '#' is a wall
     */
    private static boolean[][] walls(String... rows) {
        boolean[][] walls = new boolean[rows[0].length()][rows.length];
        for (int y = 0; y < rows.length; y++) {
            for (int x = 0; x < rows[y].length(); x++) {
                walls[x][y] = rows[y].charAt(x) == '#';
            }
        }
        return walls;
    }

    /**
     * Every wall tile is in exactly one rectangle, and no rectangle covers an empty tile
     */
    private static void assertExactCover(boolean[][] walls, List<Rect> rects) {
        int[][] coverCount = new int[walls.length][walls[0].length];
        for (Rect r : rects) {
            for (int x = r.left(); x <= r.right(); x++) {
                for (int y = r.top(); y <= r.bottom(); y++) {
                    assertTrue(walls[x][y], "rectangle " + r + " covers empty tile " + x + "," + y);
                    coverCount[x][y]++;
                }
            }
        }
        for (int x = 0; x < walls.length; x++) {
            for (int y = 0; y < walls[x].length; y++) {
                assertEquals(walls[x][y] ? 1 : 0, coverCount[x][y], "tile " + x + "," + y);
            }
        }
    }

    @Test
    void lineBecomesOneRectangle() {
        List<Rect> rects = WallRectangles.find(walls("#####"));
        assertEquals(List.of(new Rect(0, 0, 4, 0)), rects);
        assertEquals(5, rects.get(0).width());
        assertEquals(1, rects.get(0).height());
    }

    @Test
    void blockBecomesOneRectangle() {
        assertEquals(List.of(new Rect(1, 1, 3, 2)), WallRectangles.find(walls(
            ".....",
            ".###.",
            ".###.",
            ".....")));
    }

    @Test
    void lShapeIsCoveredExactly() {
        boolean[][] walls = walls(
            "#....",
            "#....",
            "#####");
        List<Rect> rects = WallRectangles.find(walls);
        assertEquals(2, rects.size());
        assertExactCover(walls, rects);
    }

    @Test
    void noWallsGivesNoRectangles() {
        assertTrue(WallRectangles.find(walls("...", "...")).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"map_0", "map_1", "map_2", "map_3", "map_4", "map_5", "map_6",
        "map_7", "map_8", "startMap"})
    void mapsAreCoveredExactly(String map) throws IOException {
        boolean[][] walls = loadWalls(map);
        List<Rect> rects = WallRectangles.find(walls);
        assertExactCover(walls, rects);
    }

    @Test
    void firstMapNeedsFarFewerCollidersThanTiles() throws IOException {
        boolean[][] walls = loadWalls("map_0");
        int tiles = 0;
        for (boolean[] column : walls) {
            for (boolean wall : column) {
                tiles += wall ? 1 : 0;
            }
        }
        List<Rect> rects = WallRectangles.find(walls);
        assertEquals(686, tiles);
        assertEquals(20, rects.size());
        assertFalse(rects.isEmpty());
    }

    private static boolean[][] loadWalls(String map) throws IOException {
        BufferedImage image;
        try (InputStream in = WallRectanglesTest.class.getClassLoader()
            .getResourceAsStream("sprite/maps/" + map + ".png")) {
            assertNotNull(in, map + " not found");
            image = ImageIO.read(in);
        }
        ID[][] level = LevelImageLoader.loadLevel(image);
        boolean[][] walls = new boolean[level.length][level[0].length];
        for (int x = 0; x < level.length; x++) {
            for (int y = 0; y < level[x].length; y++) {
                walls[x][y] = level[x][y] == ID.Wall;
            }
        }
        return walls;
    }
}
