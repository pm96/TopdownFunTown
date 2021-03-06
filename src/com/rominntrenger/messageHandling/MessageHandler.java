package com.rominntrenger.messageHandling;

import com.bluebook.graphics.Sprite;
import com.bluebook.renderer.RenderLayer;
import com.bluebook.util.GameObject;
import com.bluebook.util.GameSettings;
import com.bluebook.util.Vec2;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.util.Duration;

import java.io.InputStream;

/**
 * Class for handling messages written to the screen
 */
public class MessageHandler extends GameObject {
    private boolean isActive = true;
    private Sprite sprite;
    private String message;
    private String printMessage;
    private long start;
    private long duration = 5;
    private int width = 1920, height = 500;
    private Font font;
    private int resolutionX;
    private int resolutionY;

    /**
     * Constructor for MessageHandler.
     * Sets render layer, font and resolution.
     */
    private MessageHandler() {
        super(Vec2.ZERO, Vec2.ZERO, new Sprite(""));
        allwaysOnScreen = true;
        this.setRenderLayer(RenderLayer.RenderLayerName.GUI);
        InputStream is = getClass().getClassLoader().getResourceAsStream("fonts/Pixel-Miners.otf");
        font = Font.loadFont(is, 40);

        resolutionX = GameSettings.getInt("game_resolution_X");
        resolutionY = GameSettings.getInt("game_resolution_Y");
    }

    private static MessageHandler instance;

    public static MessageHandler getInstance() {
        if (instance == null) {
            instance = new MessageHandler();
        }
        return instance;
    }

    @Override
    public void update(double delta) {
        super.update(delta);
        if (System.currentTimeMillis() > start + duration * 1000) {
            isActive = false;
            sprite = null;
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        super.draw(gc);
        if (isActive) {
            //fills the box
            gc.setFill(Color.rgb(13, 13, 13, 0.8));
            int margin = 20;
            gc.fillRect(margin / 2, resolutionY - height / 2 - margin / 2, (resolutionX - margin),
                height - margin);

            //border for box
            gc.setStroke(Color.rgb(217, 217, 217));
            gc.setLineWidth(5);
            gc.strokeRect(margin / 2, resolutionY - height / 2 - margin / 2, (resolutionX - margin),
                height - margin);

            //sets font smoothing and font type
            gc.setFill(Color.WHITE);
            gc.setFontSmoothingType(FontSmoothingType.LCD);
            gc.setFont(font);

            if (sprite != null) {
                sprite.drawGUI(gc,
                    new Vec2(margin / 2 + 15, resolutionY - (height + margin - 25) / 2),
                    (height - 50) / 2, (height - 50) / 2);
                //displays text
                gc.fillText(printMessage, margin / 2 + height / 2 + 50,
                    resolutionY - (height - 150) / 2);

            } else {
                //displays text
                gc.fillText(printMessage, margin / 2 + 50, resolutionY - (height - 150) / 2);

            }


        }

    }

    /**
     * This function will write a message to the screen  and also display a sprite
     *
     * @param s      Message to be displayed
     * @param sprite Sprite to display
     */
    public void writeMessage(String s, Sprite sprite) {

        this.sprite = sprite;
        sprite.setOrigin(transform);
        writeMessage(s);
    }

    /**
     * This function writes a message to the screen inside box from drawMessageBox function
     *
     * @param s - string that the text will be.
     */
    public void writeMessage(String s) {
        isActive = true;
        start = System.currentTimeMillis();
        message = s;

        //attempt at typing effect for text
        final Animation animation = new Transition() {
            {
                setCycleDuration(Duration.millis(duration / 2 * 1000));
            }

            @Override
            protected void interpolate(double frac) {
                final int lenght = message.length();
                final int n = Math.round(lenght * (float) frac);
                printMessage = message.substring(0, n);
            }
        };
        animation.play();
    }

    /**
     * sets the duration of the display box.
     *
     * @param duration is the given duration for the display box
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

}
