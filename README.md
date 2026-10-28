# TopdownFunTown
This was created by 3 spectacular ppl in the lords year of 2017 or something, and it's an instant classic

**RomInntrengere** is a top-down wave shooter written in Java and JavaFX. You fight waves of
aliens, pick up weapon parts and health, and try to survive as long as possible. Up to four
players can play together using gamepads.

## Project layout

| Path | Contents |
| --- | --- |
| `src/com/bluebook` | **BlueBook**, our own small game engine: game loop (`engine/GameEngine`), rendering, physics (quadtree, colliders, rigid bodies, 2D lighting), camera, audio and input |
| `src/com/rominntrenger` | The game itself: player, enemies (FSM AI), weapons, items, waves, menus and save states |
| `src/com/rominntrenger/main/RomInntrenger.java` | Entry point (`main` class) |
| `assets/` | Sprites, audio, fonts, `weaponComponents.json` and `settings/Default.json` (game balance and screen settings) |
| `assets/sprite/maps/` | Levels. Each pixel colour maps to a tile or object, see `uml/HexValueList` |
| `lib/jinput/` | JInput for gamepad support, with its native libraries. Other libraries come from Maven Central via Gradle |
| `org/`, `uml/` | PlantUML diagrams and the IntelliJ code style |

The engine runs everything on the JavaFX application thread. Each frame, `GameEngine` runs game
logic and then collisions and lighting in fixed steps of 1/60 second (several steps if the screen
is drawn slower than that), and then draws the frame.

## Running the game

You only need JDK 21. Gradle and JavaFX are downloaded automatically:

```sh
./gradlew run
```

On Windows, use `gradlew.bat run`. To open the project in IntelliJ IDEA, open the folder and let
it import the Gradle build.

If no audio device is available, the game runs without sound. If no gamepad is found (or
`/dev/input` can't be read on Linux), it falls back to keyboard and mouse.

## Controls

| Key | Action |
| --- | --- |
| `W` `A` `S` `D` | Move |
| Mouse | Aim |
| Left mouse button | Shoot |
| Right mouse button | Reload |
| `Esc` | Open the menu (or quit after you die) |
| `P` | Restart from the last save after you die |
| `Up` / `Down` | Save / load the game |
| `F` | Leave fullscreen |
| `F1` | Toggle debug view (colliders, quadtree, FPS) |
| `F2` | Freeze/unfreeze the game |

The game also saves automatically at the start of each new wave. Saves are JSON files in
`.rominntrengere/save.json` in your home folder. With gamepads connected, each
gamepad controls one player: left stick moves and right stick aims.
