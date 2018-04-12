package com.rominntrenger.main;

import com.bluebook.graphics.Sprite;
import com.rominntrenger.main.maploader.NewLevel;
import com.rominntrenger.main.maploader.MapLoader;

import java.awt.image.BufferedImage;

public class RomInntrenger {

    public static void main(String[] args) {
       /*  MapLoader loader = new MapLoader();
        BufferedImage level;
        level = loader.loadImage("mapStart",32,32);
        ID[][] newLevel = LevelLoader.loadLevel(level);
        System.out.println(cake[3][6]);
        */

        MapLoader loader = new MapLoader();
        BufferedImage thisMap;
        thisMap = loader.loadImage("mapStart",32,32);
        NewLevel level = new NewLevel("../bg/backgroundGradient_01", thisMap);
        level.createLevel();
    }
}
