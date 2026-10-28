package com.rominntrenger.stateHandling;

import com.rominntrenger.main.RomInntrenger;
import com.rominntrenger.messageHandling.MessageHandler;
import com.rominntrenger.objects.WaveManager;
import com.rominntrenger.objects.player.Player;
import com.rominntrenger.stateHandling.DAO.MetaDAO;
import com.rominntrenger.stateHandling.DAO.PlayerDAO;
import com.rominntrenger.stateHandling.DAO.WaveManagerDAO;

import java.io.UncheckedIOException;

public class SaveStateSaver {

    public static void save(RomInntrenger rom) {
        MetaDAO meta = new MetaDAO();
        for (Player p : rom.players) {
            meta.players.add(new PlayerDAO(p));
        }
        meta.waveManager = new WaveManagerDAO(WaveManager.getInstance());

        try {
            SaveFile.write(meta, SaveFile.DEFAULT);
            MessageHandler.getInstance().writeMessage("Save complete");
        } catch (UncheckedIOException e) {
            e.printStackTrace();
            MessageHandler.getInstance().writeMessage("Could not save the game");
        }
    }

}
