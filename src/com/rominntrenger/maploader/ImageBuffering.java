package com.rominntrenger.maploader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class ImageBuffering {

    /**
     * Returns a BufferedImage from a given image file.
     * @param name name of a map in assets/sprite/maps, without .png
     * @throws IllegalArgumentException if there is no map with that name
     */
    public BufferedImage loadImage(String name) {
        String path = "sprite/maps/" + name + ".png";
        try (InputStream in = ImageBuffering.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalArgumentException("Map '" + name + "' was not found in assets/" + path);
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read map '" + name + "'", e);
        }
    }

}
