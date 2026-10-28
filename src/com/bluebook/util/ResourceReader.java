package com.bluebook.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Reads text files from the classpath, such as the settings and weapon JSON files in assets/
 */
public class ResourceReader {

    /**
     * @param path path on the classpath, e.g. "settings/Default.json"
     * @return the file's contents, read as UTF-8
     * @throws IllegalArgumentException if the file does not exist
     */
    public static String readText(String path) {
        try (InputStream stream = ResourceReader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalArgumentException("Resource '" + path + "' was not found");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read resource '" + path + "'", e);
        }
    }
}
