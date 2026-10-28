package com.bluebook.util;

import com.bluebook.util.Exceptions.SettingNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSettingsTest {

    @BeforeEach
    void loadSettings() {
        Map<String, String> settings = new HashMap<>();
        settings.put("speed", "450");
        settings.put("friction", "0.1");
        settings.put("fullscreen", "true");
        settings.put("title", "RomInntrengere");
        settings.put("broken", "yes");
        GameSettings.setLoadedSettings(settings);
    }

    @Test
    void readsTypedValues() {
        assertEquals(450, GameSettings.getInt("speed"));
        assertEquals(450.0, GameSettings.getDouble("speed"));
        assertEquals(0.1, GameSettings.getDouble("friction"));
        assertTrue(GameSettings.getBoolean("fullscreen"));
        assertEquals("RomInntrengere", GameSettings.getString("title"));
    }

    @Test
    void missingKeyNamesTheKey() {
        SettingNotFoundException e = assertThrows(SettingNotFoundException.class,
            () -> GameSettings.getDouble("sped"));
        assertTrue(e.getMessage().contains("sped"));
    }

    @Test
    void wrongTypeNamesKeyAndValue() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
            () -> GameSettings.getBoolean("broken"));
        assertTrue(e.getMessage().contains("broken"));
        assertTrue(e.getMessage().contains("yes"));

        assertThrows(IllegalArgumentException.class, () -> GameSettings.getInt("friction"));
    }

    @Test
    void reloadingSettingsClearsCachedValues() {
        assertEquals(450, GameSettings.getInt("speed"));
        GameSettings.setLoadedSettings(Map.of("speed", "300"));
        assertEquals(300, GameSettings.getInt("speed"));
    }

    @Test
    void defaultSettingsFileLoads() {
        String json = ResourceReader.readText("settings/Default.json");
        assertTrue(json.contains("\"game_resolution_X\""));
    }
}
