package com.rominntrenger.maploader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

public class ImageBuffering {

    private static HashMap<String, Image> images = new HashMap<>();
    private static HashMap<String, Image> bgImages = new HashMap<>();

    private BufferedImage image; // = new BufferedImage(188, 188, BufferedImage.TYPE_INT_RGB);

    /**
     * Returns a BufferedImage from a given image file.
     * @param name
     * @return
     */
    public BufferedImage loadImage(String name) {
        image = null;
        try {
            image = ImageIO.read(ImageBuffering.class.getClassLoader().getResourceAsStream("sprite/maps/"+ name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}