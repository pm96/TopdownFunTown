package com.rominntrenger.objects.weapon;

import com.bluebook.util.ResourceReader;
import com.google.gson.Gson;

/**
 * Loads the weapon parts defined in assets/weaponComponents.json
 */
public class WeaponComponentGSONHandler {

    public static WeaponComponentHolderDAO loadTest() {
        Gson gson = new Gson();
        return gson.fromJson(ResourceReader.readText("weaponComponents.json"), WeaponComponentHolderDAO.class);
    }
}
