package com.bluebook.input;

import com.bluebook.util.Vec2;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

/**
 * GamepadInput reads gamepads such as PlayStation and Xbox controllers through Jamepad, which uses
 * SDL's controller database (assets/gamecontrollerdb.txt, from
 * https://github.com/mdqinc/SDL_GameControllerDB), so most controllers work with the same button
 * layout
 * <p>
 * Controls: left stick moves, right stick aims, right trigger or right bumper shoots, B reloads
 */
public class GamepadInput {

    /**
     * The game has colors and sprites for at most this many players
     */
    private static final int MAX_CONTROLLERS = 4;

    /**
     * Stick movement smaller than this is ignored, since sticks rarely rest exactly at 0
     */
    private static final double DEAD_ZONE = 0.15;

    private static final double TRIGGER_THRESHOLD = 0.5;

    private ControllerManager controllers;
    private int numberOfControllers = 0;

    private Vec2[] leftJoistick = new Vec2[0];
    private Vec2[] rightJoistick = new Vec2[0];
    private boolean[] shoot = new boolean[0];
    private boolean[] reload = new boolean[0];

    /**
     * This function will initialize the gamepads that are connected when the game starts
     */
    public GamepadInput() {
        try {
            controllers = new ControllerManager();
            controllers.initSDLGamepad();
            numberOfControllers = Math.min(controllers.getNumControllers(), MAX_CONTROLLERS);
        } catch (Throwable e) {
            // e.g. UnsatisfiedLinkError if there is no native library for this platform
            System.err.println("Gamepad support disabled: " + e);
            controllers = null;
            numberOfControllers = 0;
        }
        System.out.println("Number of controllers " + numberOfControllers);

        leftJoistick = new Vec2[numberOfControllers];
        rightJoistick = new Vec2[numberOfControllers];
        shoot = new boolean[numberOfControllers];
        reload = new boolean[numberOfControllers];
        for (int i = 0; i < numberOfControllers; i++) {
            leftJoistick[i] = Vec2.ZERO;
            rightJoistick[i] = Vec2.ZERO;
        }
    }

    public int getNumberOfControllers() {
        return numberOfControllers;
    }

    /**
     * This functions will read the gamepads and format the input into the format used by the game.
     * Should be called once per game step
     */
    public void pullEvents() {
        if (controllers == null) {
            return;
        }
        controllers.update();

        for (int i = 0; i < numberOfControllers; i++) {
            ControllerState state = controllers.getState(i);
            if (!state.isConnected) {
                leftJoistick[i] = Vec2.ZERO;
                rightJoistick[i] = Vec2.ZERO;
                shoot[i] = false;
                reload[i] = false;
                continue;
            }

            // Jamepad has up as positive Y, the game has down as positive Y
            leftJoistick[i] = applyDeadZone(new Vec2(state.leftStickX, -state.leftStickY));
            rightJoistick[i] = applyDeadZone(new Vec2(state.rightStickX, -state.rightStickY));
            shoot[i] = state.rightTrigger > TRIGGER_THRESHOLD || state.rb;
            reload[i] = state.b;
        }
    }

    /**
     * Releases the gamepads, call when the game closes
     */
    public void close() {
        if (controllers != null) {
            controllers.quitSDLGamepad();
            controllers = null;
        }
    }

    private static Vec2 applyDeadZone(Vec2 stick) {
        return stick.getMagnitude() < DEAD_ZONE ? Vec2.ZERO : stick;
    }

    /**
     * Will return the {@link Vec2} where  the LeftJoistick
     * @param id Number of controller you want vector from
     * @return
     */
    public Vec2 getLeftJoistick(int id) {
        if (id >= 0 && id < leftJoistick.length)
            return leftJoistick[id];
        else
            return Vec2.ZERO;
    }

    /**
     * Will return the {@link Vec2} where  the RightJoystick
     * @param id Number of controller you want vector from
     * @return
     */
    public Vec2 getRightJoystick(int id) {
        if (id >= 0 && id < rightJoistick.length)
            return rightJoistick[id];
        else
            return Vec2.ZERO;
    }

    /**
     * Will return weather  the  shoot button for gamepadID is down
     * @param id Number of controller you want input from
     * @return
     */
    public boolean isShoot(int id) {
        if (id >= 0 && id < shoot.length)
            return shoot[id];
        else
            return false;
    }

    /**
     * Will return weather  the  reload button for gamepadID is down
     * @param id Number of controller you want input from
     * @return
     */
    public boolean isReload(int id) {
        if (id >= 0 && id < reload.length)
            return reload[id];
        else
            return false;
    }

}
