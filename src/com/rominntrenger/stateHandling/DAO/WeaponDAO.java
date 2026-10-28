package com.rominntrenger.stateHandling.DAO;

import com.bluebook.graphics.AnimationSprite;
import com.rominntrenger.objects.weapon.Weapon;
import com.rominntrenger.objects.weapon.WeaponBarrel;
import com.rominntrenger.objects.weapon.WeaponBase;
import com.rominntrenger.objects.weapon.WeaponClip;

public class WeaponDAO {

    public double speed;
    public int dmg;
    public int alteredDmg;
    public int ammoCap;
    public int ammoRemaining;


    public String projectilePath;

    public double shootInterval;

    public WeaponClip weaponClip;
    public WeaponBarrel weaponBarrel;
    public WeaponBase weaponBase;

    public String spritePath;
    public boolean isAnimation = false;
    public int spriteFrames = 1;


    /**
     * Used by Gson when loading
     */
    public WeaponDAO() {
    }

    public WeaponDAO(Weapon w) {
        speed = w.getSpeed();
        dmg = w.getDmg();
        alteredDmg = w.getAlteredDmg();
        ammoCap = w.getAmmoCap();
        ammoRemaining = w.getAmmoRemaining();
        projectilePath = w.getProjectilePath();
        shootInterval = w.getShootInterval();
        weaponClip = w.getWeaponClip();
        weaponBarrel = w.getWeaponBarrel();
        weaponBase = w.getWeaponBase();

        spritePath = w.getSprite().getPath();
        if (w.getSprite() instanceof AnimationSprite) {
            isAnimation = true;
            spriteFrames = ((AnimationSprite) w.getSprite()).getFrameCount();
        }

    }

}
