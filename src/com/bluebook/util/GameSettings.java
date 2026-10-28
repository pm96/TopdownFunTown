package com.bluebook.util;

import com.bluebook.util.Exceptions.SettingNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is used to load a settings json file and make them accessible
 * Each of the getXXXX will throw a {@link SettingNotFoundException} if the requested key is not
 * in the settings file, or an {@link IllegalArgumentException} if the value has the wrong type,
 * so a typo in a key or value is noticed right away.
 * <p>
 * Values are parsed the first time they are asked for and then cached, since some are read every
 * frame.
 */
public class GameSettings {

    public static int SCREEN_WIDTH, SCREEN_HEIGHT;

    private static Map<String, String> loadedSettings;

    private static final Map<String, Object> parsedSettings = new HashMap<>();

    public static double scale = 1.0;

    public static void setLoadedSettings(Map<String, String> inMap) {
        loadedSettings = inMap;
        parsedSettings.clear();
    }

    /**
     * Will return a double from the settings file if String matches key
     *
     * @param queryString is key that should match config in settings file
     */
    public static double getDouble(String queryString) {
        return get(queryString, Double.class, Double::parseDouble);
    }

    /**
     * Will return a String from the settings file if String matches key
     *
     * @param queryString is key that should match config in settings file
     */
    public static String getString(String queryString) {
        return get(queryString, String.class, value -> value);
    }

    /**
     * Will return a integer from the settings file if String matches key
     *
     * @param queryString is key that should match config in settings file
     */
    public static int getInt(String queryString) {
        return get(queryString, Integer.class, Integer::parseInt);
    }

    /**
     * Will return a boolean from the settings file if String matches key
     *
     * @param queryString is key that should match config in settings file
     */
    public static boolean getBoolean(String queryString) {
        return get(queryString, Boolean.class, value -> {
            if (!value.equals("true") && !value.equals("false")) {
                throw new IllegalArgumentException("not true or false");
            }
            return Boolean.parseBoolean(value);
        });
    }

    private static <T> T get(String key, Class<T> type, Function<String, T> parser) {
        Object cached = parsedSettings.get(key);
        if (type.isInstance(cached)) {
            return type.cast(cached);
        }

        String value = loadedSettings.get(key);
        if (value == null) {
            throw new SettingNotFoundException("Setting '" + key + "' does not exist in the settings file");
        }

        T parsed;
        try {
            parsed = parser.apply(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Setting '" + key + "' should be a "
                + type.getSimpleName() + ", but is '" + value + "'", e);
        }
        parsedSettings.put(key, parsed);
        return parsed;
    }

    /**
     * Will return a {@link Vec2} containing the Width (X) and Height (Y) of 1 square on the canvas
     * The number of squares is specified in the settings file under "grid_X"/"grid_y"
     *
     * @return a {@link Vec2} with the squares width and height in pixels
     */
    public static Vec2 getSquareScale() {
        int resolutionX = GameSettings.getInt("game_resolution_X");
        int gridX = GameSettings.getInt("grid_X");
        double x = resolutionX / gridX;
        x *= scale;

        int resolutionY = GameSettings.getInt("game_resolution_Y");
        int gridY = GameSettings.getInt("grid_Y");
        double y = resolutionY / gridY;
        y *= scale;

        return new Vec2(x, y);
    }

    /**
     * Will return a {@link Vec2} where X is width of canvas and Y is height of canvas
     *
     * @return A {@link Vec2} with the screens width and height
     */
    public static Vec2 getScreen() {
        int resolutionX = GameSettings.getInt("game_resolution_X");
        int resolutionY = GameSettings.getInt("game_resolution_Y");
        return new Vec2(resolutionX, resolutionY);
    }
}
