package com.rominntrenger.maploader;

import java.awt.image.BufferedImage;

public class LevelImageLoader {

    /**
     * Creates and returns a multidimentional array consisting of IDs
     * based on the Hex values from a BufferedImage (converted from RGB).
     *
     * @param image
     * @return
     */
    public static ID[][] loadLevel(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        int blockSize;
        String color = new String();
        ID[][] gameMap = new ID[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int pixel = image.getRGB(x, y);
                color = toHex(pixel);

                switch (color) {
                    case ("440000"):
                        gameMap[x][y] = ID.SpawnPlayer;
                        break;
                    case ("FFFFFF"):
                        gameMap[x][y] = ID.Wall;
                        break;
                    case ("FF0000"):
                        gameMap[x][y] = ID.AlienSpawner;
                        break;
                    case ("FF0044"):
                        gameMap[x][y] = ID.AlienSpawnerBig;
                        break;

                    //ENEMIES
                    case ("EE2222"):
                        gameMap[x][y] = ID.AlienGreen;
                        break;
                    case ("EE2244"):
                        gameMap[x][y] = ID.AlienGreenKey;
                        break;
                    case ("EE4444"):
                        gameMap[x][y] = ID.AlienPurple;
                        break;
                    case ("EE6666"):
                        gameMap[x][y] = ID.AlienExplode;
                        break;
                    case ("77FFFF"):
                        gameMap[x][y] = ID.HealingItemSmall;
                        break;
                    case ("770000"):
                        gameMap[x][y] = ID.HealingItemBig;
                        break;

                    default:
                        gameMap[x][y] = ID.Empty;
                        break;

                }

            }
        }
        return gameMap;
    }

    /**
     * Returns a Hex Value String from a given RGB value.
     *
     * @param value
     * @return
     */
    public static String toHex(int value) {
        String hexColor = String.format("%06X", (0xFFFFFF & value));
        return hexColor;
    }

}