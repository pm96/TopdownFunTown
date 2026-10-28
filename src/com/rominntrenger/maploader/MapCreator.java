package com.rominntrenger.maploader;

import com.bluebook.audio.AudioPlayer;
import com.bluebook.camera.OrthographicCamera;
import com.bluebook.graphics.Sprite;
import com.bluebook.renderer.RenderLayer;
import com.bluebook.util.GameObject;
import com.bluebook.util.GameSettings;
import com.bluebook.util.Vec2;
import com.rominntrenger.objects.AlienHive;
import com.rominntrenger.objects.PlayerSpawn;
import com.rominntrenger.objects.blocks.Wall;
import com.rominntrenger.objects.blocks.WallCollider;
import com.rominntrenger.objects.enemy.AlienExplode;
import com.rominntrenger.objects.enemy.AlienGreen;
import com.rominntrenger.objects.enemy.AlienPurple;
import com.rominntrenger.objects.health.HealingItem;
import javafx.scene.canvas.GraphicsContext;

import java.awt.image.BufferedImage;

/**
 * Creating GameMap/Area with player, enemy and obstacle array lists.
 */
public class MapCreator extends GameObject {

    ID[][] currentLevel;
    GameObject[][] grid;
    BufferedImage newMap;
    GraphicsContext gc;
    Vec2 vector;

    private String name = "Space Adventures in Space";
    private AudioPlayer soundTrack;
    private ID tempID;

    /**
     * Constructor for MapCreator given a path.
     * @param path
     */
    public MapCreator(String path) {
        super(Vec2.ZERO, Vec2.ZERO, new Sprite("bg/tilebg02"));
        allwaysOnScreen = true;
        ImageBuffering loader = new ImageBuffering();
        this.newMap = loader.loadImage(path);
        setRenderLayer(RenderLayer.RenderLayerName.BACKGROUND);
    }

    /**
     * Draws to the screen.
     * @param gc
     */
    @Override
    public void draw(GraphicsContext gc) {
        /**
         * Draws tiles where the camera can see them.
         */
        Vec2 offset = OrthographicCamera.getOffset();
        Vec2 squareSize = GameSettings.getSquareScale();
        Vec2 screen = GameSettings.getScreen();
        int xNum = (int) (-offset.getX() / squareSize
            .getX()); // Squares to not draw (Left of camera)
        int yNum = (int) (-offset.getY() / squareSize.getY());
        int squaresScreenX = (int) (screen.getX() / squareSize
            .getX()); // Square to draw across camera
        int squaresScreenY = (int) (screen.getY() / squareSize.getY());
        for (int i = xNum - 1; i < xNum + squaresScreenX + 10; i++) {
            for (int j = yNum - 4; j < yNum + squaresScreenY + 10; j++) {
                double x = squareSize.getX() * i + offset.getX() + squareSize.getX() / 2;
                double y = squareSize.getY() * j + offset.getY() + squareSize.getY() / 2;

                gc.drawImage(sprite.getImg(),
                    x,
                    y,
                    squareSize.getX(), squareSize.getY());
            }
        }
    }

    /**
     * Creates objects based on the IDs given in the {@link LevelImageLoader}
     * Also adds it to a new GameObject array.
     */
    public void createLevel() {
        currentLevel = LevelImageLoader.loadLevel(newMap);
        int x = currentLevel.length;
        int y = currentLevel[0].length;
        grid = new GameObject[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {

                tempID = currentLevel[i][j];

                vector = new Vec2(i, j);
                vector = Vec2.multiply(vector, GameSettings.getSquareScale());
                switch (tempID) {

                    case Wall:
                        grid[i][j] = new Wall(vector, Vec2.ZERO, new Sprite("bg/wall"));
                    break;

                    case AlienSpawner:
                        grid[i][j] = new AlienHive(vector);
                    break;

                    case AlienSpawnerBig:
                        grid[i][j] = new AlienHive(vector, true);
                    break;

                    case SpawnPlayer:
                        PlayerSpawn.position = vector;
                    break;

                    //ENEMIES
                    case AlienGreen:
                        grid[i][j] = new AlienGreen(vector);
                        break;
                    case AlienPurple:
                        grid[i][j] = new AlienPurple(vector);
                        break;
                    case AlienExplode:
                        grid[i][j] = new AlienExplode(vector);
                        break;

                    case HealingItemSmall:
                        grid[i][j] = new HealingItem(vector, Vec2.ZERO, new Sprite("items/healSmall"), true);
                        break;

                    case HealingItemBig:
                        grid[i][j] = new HealingItem(vector, Vec2.ZERO, new Sprite("items/healBig"), false);
                        break;
                }

            }
        }

        createWallColliders();
    }

    /**
     * Covers the walls with as few rectangles as possible, and gives each rectangle one collider.
     * Each row of walls is stretched as far right as it goes, then as far down as the whole row
     * allows
     */
    private void createWallColliders() {
        int width = currentLevel.length;
        int height = currentLevel[0].length;
        boolean[][] covered = new boolean[width][height];
        Vec2 squareSize = GameSettings.getSquareScale();

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (!isUncoveredWall(i, j, covered)) {
                    continue;
                }

                int right = i;
                while (right + 1 < width && isUncoveredWall(right + 1, j, covered)) {
                    right++;
                }

                int bottom = j;
                while (bottom + 1 < height && isRowOfUncoveredWalls(i, right, bottom + 1, covered)) {
                    bottom++;
                }

                for (int x = i; x <= right; x++) {
                    for (int y = j; y <= bottom; y++) {
                        covered[x][y] = true;
                    }
                }

                // Tile positions are tile centers, so the rectangle's center is between the first
                // and last tile
                Vec2 center = Vec2.multiply(new Vec2((i + right) / 2.0, (j + bottom) / 2.0), squareSize);
                new WallCollider(center, right - i + 1, bottom - j + 1);
            }
        }
    }

    private boolean isUncoveredWall(int x, int y, boolean[][] covered) {
        return currentLevel[x][y] == ID.Wall && !covered[x][y];
    }

    private boolean isRowOfUncoveredWalls(int left, int right, int y, boolean[][] covered) {
        for (int x = left; x <= right; x++) {
            if (!isUncoveredWall(x, y, covered)) {
                return false;
            }
        }
        return true;
    }

}