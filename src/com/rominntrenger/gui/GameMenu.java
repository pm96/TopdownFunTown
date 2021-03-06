package com.rominntrenger.gui;

import com.bluebook.audio.AudioPlayer;
import com.bluebook.engine.GameApplication;
import com.rominntrenger.main.RomInntrenger;
import com.rominntrenger.stateHandling.SaveStateLoader;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import java.io.File;
import java.io.IOException;

/**
 * Class that containing all functionality for creating the game manu
 */
 class GameMenu extends Parent {
    private VBox menu0, menu1, menu2;
    private Slider soundSlider;
    private Stage primaryStage;
    private final int offset = 400;
    private Clip clip;
    private AudioPlayer ap;
    private FloatControl gainControl;
    private MenuButton resumeButton, optionsButton, exitButton, backButtonMainMenu,
        soundButton, backButtonSoundMenu, soundCaption, soundValue,
        loadFromFile, restartButton;

    /**
     * Constructor creating menus and calls {@link #addButton}  with fadetransitions for the menus
     * @param primaryStage - the primary stage where the menu will be nested
     */
    public GameMenu(Stage primaryStage) {
        this.primaryStage = primaryStage;
        menu0 = new VBox(10);
        menu1 = new VBox(10);
        menu2 = new VBox(10);

        menu0.setTranslateX(100);
        menu0.setTranslateY(300);

        menu1.setTranslateX(100);
        menu1.setTranslateY(300);

        menu2.setTranslateX(100);
        menu2.setTranslateY(300);


        menu1.setTranslateX(offset);
        menu2.setTranslateX(offset);

        addButton();

        Rectangle bg = new Rectangle(1920, 1080);
        bg.setFill(Color.GREY);
        bg.setOpacity(0.3);

        getChildren().addAll(bg, menu0);
    }

    /**
     * Adds buttons to the menus created by the constructor in {@link GameMenu}
     */
    private void addButton() {
        ap = new AudioPlayer("audio/MoodyLoop.wav");

        resumeButton = new MenuButton("START/RESUME");
        resumeButton.setOnMouseClicked(event -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), this);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.setOnFinished(evt -> setVisible(false));
            fadeTransition.play();
           try {
                GameApplication.getInstance().callGame(primaryStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        restartButton = new MenuButton("RESTART");

        optionsButton = new MenuButton("OPTIONS");
        optionsButton.setOnMouseClicked(event -> {
            getChildren().add(menu1);

            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.25), menu0);
            translateTransition.setToX(menu0.getTranslateX() - offset);

            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), menu1);
            translateTransition1.setToX(menu0.getTranslateX());

            translateTransition.play();
            translateTransition1.play();

            translateTransition.setOnFinished(evt -> getChildren().remove(menu0));
        });

        exitButton = new MenuButton("EXIT");
        exitButton.setOnMouseClicked(event -> System.exit(0));

        loadFromFile = new MenuButton("LOAD FROM FILE");
        loadFromFile.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select saved file");
            fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Data files", "*.data", "*.txt"),
                new ExtensionFilter("All files", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if(selectedFile != null){
                try {
                    GameApplication.getInstance().callGame(primaryStage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SaveStateLoader.loadPreviousSave((RomInntrenger)GameApplication.getInstance(), selectedFile);
                System.out.println("LOADING");


            }});

        backButtonMainMenu = new MenuButton("BACK");
        backButtonMainMenu.setOnMouseClicked(event -> {
            getChildren().add(menu0);

            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.25), menu1);
            translateTransition.setToX(menu1.getTranslateX() + offset);

            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), menu0);
            translateTransition1.setToX(menu1.getTranslateX());

            translateTransition.play();
            translateTransition1.play();

            translateTransition.setOnFinished(evt -> getChildren().remove(menu1));
        });

        soundButton = new MenuButton("SOUND");
        soundButton.setOnMouseClicked(event -> {
            getChildren().add(menu2);

            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.25), menu1);
            translateTransition.setToX(menu1.getTranslateX() - offset);

            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), menu2);
            translateTransition1.setToX(menu1.getTranslateX());

            translateTransition.play();
            translateTransition1.play();
            ap.playLoop();

            translateTransition.setOnFinished(evt -> getChildren().remove(menu1));
        });
        backButtonSoundMenu = new MenuButton("BACK");
        backButtonSoundMenu.setOnMouseClicked(event -> {
            getChildren().add(menu1);

            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.25), menu2);
            translateTransition.setToX(menu2.getTranslateX() + offset);

            TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), menu1);
            translateTransition1.setToX(menu2.getTranslateX());

            translateTransition.play();
            translateTransition1.play();
            ap.stop();
            ap.close();

            translateTransition.setOnFinished(evt -> getChildren().remove(menu2));
        });

        HBox volDisplay = new HBox(10);
        soundSlider = new Slider(0, 1, 1);
        soundSlider.setShowTickLabels(true);
        soundSlider.setShowTickMarks(true);

        soundCaption = new MenuButton("SOUND LEVEL:");
        soundValue = new MenuButton("100");

        clip = ap.getClip();
        gainControl = ap.getGainControl();
        soundSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val,
                                                 Number new_val) -> {
            if (clip.isControlSupported(Type.MASTER_GAIN)) {
                FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * (float) soundSlider.getValue()) + gainControl.getMinimum();
                volume.setValue((gain));
            }
            soundValue.setText(((int) ((double) new_val * 100)) + "");
        });

        menu0.getChildren().addAll(resumeButton, loadFromFile, optionsButton, exitButton);
        menu1.getChildren().addAll(backButtonMainMenu, soundButton);
        volDisplay.getChildren().addAll(soundCaption, soundSlider, soundValue);
        menu2.getChildren().addAll(backButtonSoundMenu, volDisplay);
    }


}

