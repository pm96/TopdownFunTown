# TopdownFunTown
This was created by 3 spectacular ppl in the lords year of 2017 or something, and it's an instant classic

**RomInntrengere** is a top-down wave shooter written in Java and JavaFX. You fight waves of
aliens, pick up weapon parts and health, and try to survive as long as possible. Up to four
players can play together using gamepads.

## Project layout

| Path | Contents |
| --- | --- |
| `src/com/bluebook` | **BlueBook**, our own small game engine: game loop, rendering, physics (quadtree, colliders, rigid bodies, 2D lighting), camera, audio and input |
| `src/com/rominntrenger` | The game itself: player, enemies (FSM AI), weapons, items, waves, menus and save states |
| `src/com/rominntrenger/main/RomInntrenger.java` | Entry point (`main` class) |
| `assets/` | Sprites, audio, fonts, `weaponComponents.json` and `settings/Default.json` (game balance and screen settings) |
| `assets/sprite/maps/` | Levels. Each pixel colour maps to a tile or object, see `uml/HexValueList` |
| `lib/` | Gson and JInput (gamepad support, including native libraries) |
| `org/`, `uml/` | PlantUML diagrams and the IntelliJ code style |

`src/com/bluebook/OpenGL` is an unfinished LWJGL experiment. It is not used by the game and is
excluded from the build.

## Running the game

You need a JDK (11 or newer, tested with 21) and the [OpenJFX SDK](https://gluonhq.com/products/javafx/)
for the same version. Set `JAVAFX` to the SDK's `lib` folder, then run from the repository root:

```sh
JAVAFX=/path/to/javafx-sdk/lib
CP="lib/gson-2.8.2.jar:lib/jinput/jinput-2.0.7.jar:lib/jutils-1.0.0.jar"
FX="--module-path $JAVAFX --add-modules javafx.controls,javafx.fxml
    --add-exports javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED"

# Compile
mkdir -p out
find src/com -name '*.java' -not -path '*/OpenGL/*' > out/sources.txt
javac -encoding UTF-8 -d out $FX -cp "$CP" @out/sources.txt

# Run
java $FX -cp "out:src:assets:$CP" -Djava.library.path=lib/jinput com.rominntrenger.main.RomInntrenger
```

On Windows, use `;` instead of `:` as the classpath separator.

The `--add-exports` flag is needed because the physics code uses JavaFX's internal
`com.sun.javafx.geom.Line2D`. `src` is on the classpath for the FXML file, and `assets` for sprites,
audio and settings.

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
| `Up` / `Down` | Save / load the game (`./myFile.data`) |
| `F` | Leave fullscreen |

The game also saves automatically at the start of each new wave. With gamepads connected, each
gamepad controls one player: left stick moves and right stick aims.
