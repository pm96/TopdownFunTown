package com.rominntrenger.gui;

import java.io.IOException;
import java.io.InputStream;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Class that creates the menu and handles moving back and forth between game and menu
 */
public class Menu extends Parent {
    private GameMenu gameMenu;
    private Scene scene;
    private Stage primaryStage;
    private Pane root;

    /**
     * Constructor for Menu, sets the Stage.
     * @param primaryStage is the primary stage in your javaFX application
     */
    public Menu(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Calls the menu by creating it
     */
    public void callMenu() {
        root = new Pane();
        root.setPrefSize(1920, 1080);

        InputStream is;
        is = getClass().getClassLoader().getResourceAsStream("sprite/pictures/bg.gif");
        System.out.println(is);
        Image image = new Image(is);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageView backgroundImage = new ImageView(image);
        backgroundImage.setFitWidth(1920);
        backgroundImage.setFitHeight(1080);
        boolean isBackgroundImageVisible = true;
        backgroundImage.setVisible(true);

        gameMenu = new GameMenu(primaryStage);
        gameMenu.setVisible(true);

        root.getChildren().addAll(backgroundImage, gameMenu);

        scene = new Scene(root);
        keyBoardInput();

        primaryStage.setScene(scene);
    }

    /**
     * Shows the menu. The game should be paused before calling this
     */
    public void show() {
        primaryStage.getScene().setRoot(root);
    }

    /**
     * Esc in the menu goes back to the game. Esc in the game is handled by the game loop, which
     * opens the menu, so the two never react to the same key press
     */
    public void keyBoardInput() {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE && scene.getRoot() == root) {
                event.consume();
                gameMenu.startOrResumeGame();
            }
        });
    }

    public Pane getRoot() {
        return root;
    }
}
