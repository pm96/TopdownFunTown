package com.bluebook.engine;

import com.bluebook.input.Input;
import com.bluebook.physics.HitDetectionHandler;
import com.bluebook.renderer.CanvasRenderer;
import com.bluebook.util.GameObject;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is the main engine of the gamePane
 * <p>
 * Everything runs in one game loop on the JavaFX application thread: each frame runs game logic
 * and then collisions and lighting in fixed time steps, then draws. Input events and menus are also handled on
 * this thread, so game objects never need to be shared between threads.
 */
public class GameEngine {

    /**
     * Game logic and collisions always run in steps of this many seconds (60 per second), no
     * matter how fast the screen is drawn, so fast objects can't skip through walls on slow
     * machines
     */
    private static final double TIME_STEP = 1.0 / 60.0;

    /**
     * Most steps to simulate in one frame, so the game slows down instead of freezing when a
     * machine can't keep up
     */
    private static final int MAX_STEPS_PER_FRAME = 5;

    /**
     * Time that has passed but not been simulated yet
     */
    private double unsimulatedTime = 0;

    /**
     * Seconds of game time simulated so far, see {@link #currentTimeMillis()}
     */
    private static double gameTime = 0;

    /**
     * Objects may be created or destroyed while the list is updated, the copy on write list
     * makes iterating safe while that happens
     */
    private CopyOnWriteArrayList<GameObject> updateObjects = new CopyOnWriteArrayList<>();

    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;

    /**
     * Used to draw DEBUG information on screen, can be triggered by F1 by default
     */
    public static boolean DEBUG = false;

    private boolean isPaused = true;

    /**
     * Timestamp of the previous simulated frame, 0 if the next frame is the first after a pause
     */
    private long previousFrameNanos = 0;

    private static GameEngine singleton;

    /**
     * Frames per second of the game loop, this will be drawn on screen in DEBUG_MODE triggered by {@link GameEngine#DEBUG} and drawn in {@link com.bluebook.renderer.FPSLineGraph}
     */
    public double draw_FPS = 0.0;

    /**
     * Milliseconds spent on logic and collisions in the last frame, shown in DEBUG_MODE
     */
    public double update_ms = 0.0;

    private Canvas canvas;

    private AnimationTimer gameLoop;

    /**
     * Constructor for GameEngine
     *
     * @param canvas Canvas for gamePane to be drawn to
     */
    public GameEngine(Canvas canvas) {
        singleton = this;
        CanvasRenderer.getInstance().setCanvas(canvas);

        this.canvas = canvas;

        startGameLoop();
    }

    protected void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused) {
                    long start = System.nanoTime();
                    if (previousFrameNanos != 0) {
                        unsimulatedTime += (now - previousFrameNanos) / 1_000_000_000.0;
                    }
                    previousFrameNanos = now;

                    int steps = 0;
                    while (unsimulatedTime >= TIME_STEP && steps < MAX_STEPS_PER_FRAME
                        && !isPaused) {
                        gameTime += TIME_STEP;
                        update(TIME_STEP);
                        HitDetectionHandler.getInstance().update();
                        clearPressedInput();
                        unsimulatedTime -= TIME_STEP;
                        steps++;
                    }
                    if (steps == MAX_STEPS_PER_FRAME) {
                        unsimulatedTime = 0;
                    }
                    update_ms = (System.nanoTime() - start) / 1_000_000.0;
                }

                CanvasRenderer.getInstance().drawAll();

                // Finding FSP for debugging
                long oldFrameTime = frameTimes[frameTimeIndex];
                frameTimes[frameTimeIndex] = now;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                if (frameTimeIndex == 0) {
                    arrayFilled = true;
                }
                if (arrayFilled) {
                    long elapsedNanos = now - oldFrameTime;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                    draw_FPS = 1_000_000_000.0 / elapsedNanosPerFrame;
                }
            }
        };
        gameLoop.start();
    }

    /**
     * Stops game logic and collisions, the screen is still drawn
     */
    public void pauseGame() {
        isPaused = true;
    }

    /**
     * Resumes game logic and collisions
     */
    public void resumeGame() {
        if (isPaused) {
            isPaused = false;
            previousFrameNanos = 0;
            unsimulatedTime = 0;
            // Keys pressed in a menu shouldn't also act in the game
            clearPressedInput();
        }
    }

    private void clearPressedInput() {
        if (Input.getInstance() != null) {
            Input.getInstance().clearPressed();
        }
    }

    /**
     * Will tell if {@link GameEngine#pauseGame()} is called and game is now  paused
     * @return
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Milliseconds of game time. Works like {@link System#currentTimeMillis()}, but stands still
     * while the game is paused, so cooldowns and timers in the game pause with it
     */
    public static long currentTimeMillis() {
        return (long) (gameTime * 1000);
    }

    /**
     * Singleton getter
     * @return
     */
    public static GameEngine getInstance() {
        return singleton;
    }

    /**
     * This is used by the constructor of gameobject so it's update function is called
     */
    public void addGameObject(GameObject go) {
        updateObjects.add(go);
    }

    /**
     * Removes the object from updateobjects, this is called during an gameobjects destruction
     */
    public void removeGameObject(GameObject go) {
        updateObjects.remove(go);
    }

    /**
     * This is called every frame from the game loop
     */
    public void update(double delta) {
        GameApplication.getInstance().update(delta);
        for (GameObject updateGameObject : updateObjects) {
            if (updateGameObject.isAlive()) {
                updateGameObject.update(delta);
            }
        }
    }
}
