package com.rominntrenger.stateHandling.DAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Everything in a save file. It is stored as JSON, see {@link com.rominntrenger.stateHandling.SaveFile}
 */
public class MetaDAO {

    /**
     * Format of the save file, so a later version of the game can recognise older saves
     */
    public int version = 1;

    public List<PlayerDAO> players = new ArrayList<>();
    public WaveManagerDAO waveManager;

}
