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
| `test/`, `test-resources/` | Unit tests (JUnit 5) |
| `assets/` | Sprites, audio, fonts, `weaponComponents.json` and `settings/Default.json` (game balance and screen settings) |
| `assets/sprite/maps/` | Levels. Each pixel colour maps to a tile or object, see `uml/HexValueList` |
| `org/`, `uml/` | PlantUML diagrams and the IntelliJ code style |

The engine runs everything on the JavaFX application thread. Each frame, `GameEngine` runs game
logic and then collisions and lighting in fixed steps of 1/60 second (several steps if the screen
is drawn slower than that), and then draws the frame.

## Running the game

You only need JDK 21. Gradle and JavaFX are downloaded automatically:

```sh
./gradlew run
```

Run the tests with `./gradlew test`, or build and test everything with `./gradlew build`. GitHub
Actions runs `./gradlew build` on every push and pull request.

On Windows, use `gradlew.bat run`.

### Sharing the game

`./gradlew jpackage` builds the game with its own Java runtime in `build/jpackage`, for the
operating system you run it on. The folder can be zipped and shared, and players don't need Java.
Add `-PinstallerType=msi`, `dmg`, `deb` or `rpm` for an installer instead.

The **Package** workflow in GitHub Actions builds it for Windows, macOS (Apple Silicon) and Linux.
Start it from the Actions tab and download the results, or push a tag like `v1.0.0` to publish
them as a GitHub release. The macOS build isn't signed, so the first time, right-click the app and
choose Open. To open the project in IntelliJ IDEA, open the folder and let
it import the Gradle build.

If no audio device is available, the game runs without sound. Without a gamepad, the game uses
keyboard and mouse.

## Controls

| Key | Action |
| --- | --- |
| `W` `A` `S` `D` | Move |
| Mouse | Aim |
| Left mouse button | Shoot |
| Right mouse button | Reload |
| `Esc` | Open the menu (or quit after you die) |
| `P` / `R` | After you die: load the last save / start over |
| `Up` / `Down` | Save / load the game |
| `F` | Leave fullscreen |
| `F1` | Toggle debug view (colliders, quadtree, FPS) |
| `F2` | Freeze/unfreeze the game |

Set `"map"` in `assets/settings/Default.json` to `map_0` to `map_8`, or to `random` for a
different map each time.

The game also saves automatically at the start of each new wave. Saves are JSON files in
`.rominntrengere/save.json` in your home folder. With gamepads connected when the
game starts, each gamepad (up to four) controls one player: left stick moves, right stick aims,
right trigger or right bumper shoots and B reloads. Gamepads are read with
[Jamepad](https://github.com/libgdx/Jamepad), so most PlayStation, Xbox and Switch controllers work.
