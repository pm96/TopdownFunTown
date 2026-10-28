package com.rominntrenger.objects.blocks;

import com.bluebook.physics.BoxCollider;
import com.bluebook.util.GameObject;
import com.bluebook.util.Vec2;

/**
 * Invisible object that blocks movement and light for a rectangle of {@link Wall} tiles.
 * Using one collider for many tiles keeps collision checks and lighting fast
 */
public class WallCollider extends GameObject {

    /**
     * @param center center of the rectangle in world coordinates
     * @param tilesWide width of the rectangle, in tiles
     * @param tilesHigh height of the rectangle, in tiles
     */
    public WallCollider(Vec2 center, int tilesWide, int tilesHigh) {
        super(center, Vec2.ZERO, null);
        setSize(new Vec2(tilesWide, tilesHigh));
        collider = new BoxCollider(this);
        collider.setTag("Block");
        collider.addInteractionLayer("Obscure");
    }
}
