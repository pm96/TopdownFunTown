package com.rominntrenger.main;

import javafx.application.Application;

/**
 * Starts the game from a plain main class. Java refuses to start a JavaFX Application subclass
 * directly when JavaFX is on the classpath instead of the module path, which is how the packaged
 * game (see the jpackage task in build.gradle.kts) runs
 */
public class Launcher {

    public static void main(String[] args) {
        Application.launch(RomInntrenger.class, args);
    }
}
