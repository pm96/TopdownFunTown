package com.rominntrenger.objects.weapon;

import com.bluebook.util.ResourceReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class WeaponComponentGSONHandler {


    public static void saveTest() {
        WeaponComponentHolderDAO wcDAO = new WeaponComponentHolderDAO();
        wcDAO.barrels.add(new WeaponBarrel());
        wcDAO.bases.add(new WeaponBase());
        wcDAO.clips.add(new WeaponClip());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        writeFile("weaponComponents.json", gson.toJson(wcDAO));

    }

    public static WeaponComponentHolderDAO loadTest() {
        Gson gson = new Gson();
        return gson.fromJson(ResourceReader.readText("weaponComponents.json"), WeaponComponentHolderDAO.class);
    }

    private static void writeFile(String path, String content) {
        try {
            File f = new File(path);
            if (!f.exists())
                f.createNewFile();
            PrintWriter writer =
                new PrintWriter(f);
            writer.write(content);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
