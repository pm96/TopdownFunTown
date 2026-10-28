package com.bluebook.physics;

import com.bluebook.util.GameSettings;
import com.bluebook.util.Vec2;
import com.rominntrenger.objects.player.Player;
import java.awt.geom.Line2D;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is called once per frame from the game loop and handles every instance of {@link
 * BoxCollider}
 */
public class HitDetectionHandler {

    /**
     * Will trigger weather to use QuadTree or round robin to do hit detection, This is set in the setting file
     */
    public static boolean USE_QUADTREE = true;
    /**
     * Will trigger weather to use Raycast or not, this is set in the settings file
     */
    public static boolean DO_RAYCAST = true;

    public static boolean DO_SHADOW_SWEEP = true;

    private static HitDetectionHandler singleton;

    /**
     * List of colliders for calculating physics
     */
    public ArrayList<Collider> colliders = new ArrayList<>();

    /**
     * List of lines from colliders used to calculate Visibility
     */
    public ArrayList<Line2D.Float> lines = new ArrayList<>();

    // And out buffers needed for thread safe operation
    private ArrayList<Collider> colliderInBuffer = new ArrayList<>();
    private ArrayList<Collider> colliderOutBuffer = new ArrayList<>();
    public QuadTree qtTree;

    /**
     * Hopefully this will keep the garbadge collector from unloading
     * LineSegment.class and we avoid issues. Let's see
     * #hackerbois i'm in!
     * DO NOT REMOVE!
     */
    private LineSegment ln = new LineSegment(new Line2D.Float(0, 0, 1, 1), new Vec2(0, 0));
    private LinePointCompare lpc = new LinePointCompare();
    private LinePoint lp = new LinePoint();

    public static HitDetectionHandler getInstance() {
        if (singleton == null) {
            singleton = new HitDetectionHandler();
        }
        return singleton;
    }

    private HitDetectionHandler() {
        USE_QUADTREE = GameSettings.getBoolean("Physics_use_quadtree");
        DO_RAYCAST = GameSettings.getBoolean("Physics_use_raycast");
        DO_SHADOW_SWEEP = GameSettings.getBoolean("Physics_use_shadowSweep");
        Vec2 screen = GameSettings.getScreen();
        qtTree = new QuadTree(new Rectangle(-screen.getX() * 10, -screen.getY() * 10, screen.getX() * 20, screen.getY() * 20), 3); // Creating oversized quadtree

    }

    /**
     * Will update position to all {@link Collider} relative to their attached {@link com.bluebook.util.GameObject}
     */
    /**
     * Moves the colliders to their objects, then checks for collisions and updates lights.
     * Called once per frame from the game loop, after game logic
     */
    public void update() {
        updatePositions();
        lookForCollision();
    }

    public void updatePositions() {
        for (Collider collider : colliders) {
            collider.getGameObject().setSize(collider.getGameObject().getScale());
            collider.updatePosition();
        }
    }

    private void buildQuadTree() {
        qtTree.reset();
        for (Collider c : colliders) {
            qtTree.insert(c);
        }
    }

    private void checkCollision(Collider base) {
        ArrayList<Collider> otherColliders;
        if (USE_QUADTREE)
            otherColliders = qtTree.query(base);
        else
            otherColliders = colliders;

        Boolean notCollided = true;

        for (Collider dest : otherColliders) {
            if (base.getGameObject() != dest.getGameObject() &&
                base.getInteractionLayer().contains(dest.getTag()) &&
                base.instersects(dest)) { // Check not duplicate, interactionlayer and intersection

                base.setIntersection(
                    (Path) Shape.intersect(base.getShape(), dest.getShape()));
                base.setIntersectionCollider(dest);

                notCollided = false;

                if (base.listener != null) {
                    base.listener.onCollision(dest);
                }
            }
        }


        if (notCollided) {
            base.setIntersection(null);
            base.setIntersectionCollider(null);
        }

    }

    /**
     * This will go over  all collision and raytracing looking for intersections
     */
    protected void lookForCollision() {

        lines.clear();
        if (USE_QUADTREE)
            buildQuadTree();

        Vec2 screen = GameSettings.getScreen();

        for (Collider base : colliders) {
            checkCollision(base);

            if (DO_RAYCAST && !(base.getGameObject() instanceof Player) && base.interactionLayer.contains("Obscure")) {
                Line2D.Float[] baseLines = base.getLines();
                for (Line2D.Float l : baseLines) {
                    // Lines are in screen coordinates. Lights are only drawn on screen, and a wall
                    // outside the screen can't shadow anything on it, so those are skipped
                    if (l.intersects(-SCREEN_MARGIN, -SCREEN_MARGIN,
                        screen.getX() + 2 * SCREEN_MARGIN, screen.getY() + 2 * SCREEN_MARGIN)) {
                        lines.add(l);
                    }
                }


            }
        }

        if (DO_RAYCAST)
            doRaycasts();

        moveBuffer();
    }

    public static int counter = 0;

    /**
     * How far outside the screen walls are still included when calculating lights
     */
    private static final float SCREEN_MARGIN = 64;

    private void doRaycasts() {
        List<Light2D> lights = Light2D.lights;
        for (Light2D light : lights)
            if (DO_SHADOW_SWEEP) {
                // The edges of the screen bound the light where no wall is in the way
                Vec2 screen = GameSettings.getScreen();

                float LeftX = -SCREEN_MARGIN;
                float TopY = -SCREEN_MARGIN;
                float RightX = (float) screen.getX() + SCREEN_MARGIN;
                float BottomY = (float) screen.getY() + SCREEN_MARGIN;

                lines.add(new Line2D.Float(LeftX, TopY, RightX, TopY));
                lines.add(new Line2D.Float(RightX, TopY, RightX, BottomY));
                lines.add(new Line2D.Float(RightX, BottomY, LeftX, BottomY));
                lines.add(new Line2D.Float(LeftX, BottomY, LeftX, TopY));

                Vec2 source = light.source.getTransform().getGlobalPosition();

                ArrayList<LineSegment> lineSegments = new ArrayList<>();
                lines.forEach(l -> lineSegments.add(new LineSegment(l, source)));

                try {
                    light.calculateVisibility(lineSegments);
                } catch (IllegalArgumentException e) {
                    System.out.println("IllegalArgumentException");
                }
            }
    }

    private void moveBuffer() {
        colliders.addAll(colliderInBuffer);
        colliderInBuffer.clear();
        // Remove
        for (Collider c : colliderOutBuffer) {
            colliders.remove(c);
        }
        colliderOutBuffer.clear();
    }

    /**
     * Will add collider into temporary buffer to be added during next cycle
     *
     * @param collider {@link Collider}  to be added
     */
    protected void addCollider(Collider collider) {
        colliderInBuffer.add(collider);
    }

    /**
     * Will remove collider from hitdetection during next cycle
     *
     * @param collider {@link Collider} to be removed
     */
    protected void removeCollider(Collider collider) {
        colliderOutBuffer.add(collider);
    }


}
