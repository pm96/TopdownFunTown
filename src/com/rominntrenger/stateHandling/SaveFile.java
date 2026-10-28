package com.rominntrenger.stateHandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.rominntrenger.stateHandling.DAO.MetaDAO;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Reads and writes save files as JSON
 */
public class SaveFile {

    /**
     * Where the game saves automatically and with the save key. It is in the user's home folder,
     * since an installed game may not be allowed to write next to itself
     */
    public static final Path DEFAULT =
        Paths.get(System.getProperty("user.home"), ".rominntrengere", "save.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * The first bytes of a file written by Java serialization, which older versions used
     */
    private static final int JAVA_SERIALIZATION_MAGIC = 0xACED;

    /**
     * Thrown when a save file exists but can't be loaded
     */
    public static class InvalidSaveException extends Exception {

        public InvalidSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void write(MetaDAO save, Path file) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            Files.writeString(file, GSON.toJson(save), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write save file " + file, e);
        }
    }

    /**
     * @throws NoSuchFileException if there is no save file
     * @throws InvalidSaveException if the file isn't a save file this version can read
     */
    public static MetaDAO read(Path file) throws IOException, InvalidSaveException {
        if (isJavaSerialized(file)) {
            throw new InvalidSaveException(
                "This save is from an older version of the game and can't be loaded", null);
        }

        String json = Files.readString(file, StandardCharsets.UTF_8);
        MetaDAO save;
        try {
            save = GSON.fromJson(json, MetaDAO.class);
        } catch (JsonParseException e) {
            throw new InvalidSaveException("The save file is damaged", e);
        }
        if (save == null || save.players == null || save.waveManager == null) {
            throw new InvalidSaveException("The save file is missing data", null);
        }
        return save;
    }

    private static boolean isJavaSerialized(Path file) throws IOException {
        try (InputStream in = Files.newInputStream(file)) {
            int first = in.read();
            int second = in.read();
            return first >= 0 && second >= 0 && (first << 8 | second) == JAVA_SERIALIZATION_MAGIC;
        }
    }
}
