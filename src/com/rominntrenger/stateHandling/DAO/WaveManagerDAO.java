package com.rominntrenger.stateHandling.DAO;

import com.rominntrenger.objects.WaveManager;
import com.rominntrenger.objects.WaveManager.WaveSate;

public class WaveManagerDAO {

    public int waveNumber;

    /**
     * Used by Gson when loading
     */
    public WaveManagerDAO() {
    }

    public WaveManagerDAO(WaveManager wm) {
        if (wm.getState() != WaveSate.PAUSE)
            waveNumber = wm.getWaveNumber() - 1;
    }
}
