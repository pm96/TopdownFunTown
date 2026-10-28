package com.rominntrenger.stateHandling;

import com.rominntrenger.objects.weapon.WeaponClip;
import com.rominntrenger.stateHandling.DAO.MetaDAO;
import com.rominntrenger.stateHandling.DAO.PlayerDAO;
import com.rominntrenger.stateHandling.DAO.WaveManagerDAO;
import com.rominntrenger.stateHandling.DAO.WeaponDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveFileTest {

    @TempDir
    Path folder;

    private static MetaDAO exampleSave() {
        PlayerDAO player = new PlayerDAO();
        player.playerID = 1;
        player.playerColor = new double[]{1, 0.5, 0.25, 1};
        player.playerHealth = 70;
        player.maxPlayerHealth = 100;
        player.posX = 1500;
        player.posY = 3720;
        player.spritePath = "friendlies/character";
        player.isAnimation = true;
        player.spriteFrames = 4;
        player.weapon = new WeaponDAO();
        player.weapon.ammoRemaining = 13;
        player.weapon.weaponClip = new WeaponClip();
        player.weapon.weaponClip.character = 'S';

        MetaDAO save = new MetaDAO();
        save.players.add(player);
        save.waveManager = new WaveManagerDAO();
        save.waveManager.waveNumber = 7;
        return save;
    }

    @Test
    void writtenSaveCanBeReadBack() throws Exception {
        Path file = folder.resolve("nested/save.json");
        SaveFile.write(exampleSave(), file);

        MetaDAO loaded = SaveFile.read(file);
        assertEquals(1, loaded.version);
        assertEquals(7, loaded.waveManager.waveNumber);
        assertEquals(1, loaded.players.size());
        PlayerDAO player = loaded.players.get(0);
        assertEquals(70, player.playerHealth);
        assertEquals(1500, player.posX);
        assertArrayEquals(new double[]{1, 0.5, 0.25, 1}, player.playerColor);
        assertEquals(13, player.weapon.ammoRemaining);
        assertEquals('S', player.weapon.weaponClip.character);
    }

    @Test
    void saveIsReadableJson() throws Exception {
        Path file = folder.resolve("save.json");
        SaveFile.write(exampleSave(), file);
        String json = Files.readString(file);
        assertTrue(json.contains("\"waveNumber\": 7"), json);
    }

    @Test
    void missingSaveIsReportedAsMissing() {
        assertThrows(NoSuchFileException.class, () -> SaveFile.read(folder.resolve("none.json")));
    }

    @Test
    void damagedSaveIsRejected() throws Exception {
        Path file = folder.resolve("save.json");
        Files.writeString(file, "{\"players\": [");
        assertThrows(SaveFile.InvalidSaveException.class, () -> SaveFile.read(file));

        Files.writeString(file, "{}");
        assertThrows(SaveFile.InvalidSaveException.class, () -> SaveFile.read(file));
    }

    @Test
    void oldJavaSerializedSaveIsRejectedWithoutDeserializing() throws Exception {
        Path file = folder.resolve("myFile.data");
        try (OutputStream out = Files.newOutputStream(file);
             ObjectOutputStream objects = new ObjectOutputStream(out)) {
            objects.writeObject(new ArrayList<String>());
        }
        SaveFile.InvalidSaveException e =
            assertThrows(SaveFile.InvalidSaveException.class, () -> SaveFile.read(file));
        assertTrue(e.getMessage().contains("older version"));
    }
}
