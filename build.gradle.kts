plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

javafx {
    version = "21.0.5"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    // Gamepads, through SDL. Includes native libraries for Windows, macOS and Linux
    implementation("com.badlogicgames.jamepad:jamepad:2.30.0.0")

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// The project predates Gradle, so it keeps its original layout:
// sources and the FXML file in src/, game assets in assets/
sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))
        }
        resources {
            setSrcDirs(listOf("src", "assets"))
            exclude("**/*.java")
        }
    }
    test {
        java {
            setSrcDirs(listOf("test"))
        }
        resources {
            setSrcDirs(listOf("test-resources"))
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass = "com.rominntrenger.main.RomInntrenger"
}

// Builds a self-contained game with its own Java runtime in build/jpackage, for the operating
// system Gradle runs on. ./gradlew jpackage gives a folder you can zip and share; add
// -PinstallerType=msi, dmg, deb or rpm to get an installer (msi needs WiX on Windows)
// JavaFX is linked into the game's Java runtime as modules, everything else goes on the classpath
val jpackageInput by tasks.registering(Sync::class) {
    from(tasks.installDist.map { it.destinationDir.resolve("lib") })
    into(layout.buildDirectory.dir("jpackage-input/app"))
    exclude("javafx-*.jar")
}

val jpackageJavaFx by tasks.registering(Sync::class) {
    from(tasks.installDist.map { it.destinationDir.resolve("lib") })
    into(layout.buildDirectory.dir("jpackage-input/javafx"))
    include("javafx-*.jar")
}

val jpackage by tasks.registering(Exec::class) {
    group = "distribution"
    description = "Packages the game with a Java runtime for this operating system"
    dependsOn(jpackageInput, jpackageJavaFx)

    val installerType = providers.gradleProperty("installerType").orElse("app-image")
    val libDir = layout.buildDirectory.dir("jpackage-input/app")
    val javaFxDir = layout.buildDirectory.dir("jpackage-input/javafx")
    val outputDir = layout.buildDirectory.dir("jpackage")
    val mainJar = "${project.name}.jar"
    val jdk = javaToolchains.launcherFor(java.toolchain).map { it.metadata.installationPath }
    inputs.dir(libDir)
    inputs.dir(javaFxDir)
    inputs.property("installerType", installerType)
    outputs.dir(outputDir)

    doFirst {
        // jpackage refuses to overwrite earlier output
        outputDir.get().asFile.deleteRecursively()
        val isWindows = System.getProperty("os.name").startsWith("Windows")
        commandLine(
            jdk.get().file("bin/jpackage" + if (isWindows) ".exe" else "").asFile.absolutePath,
            "--type", installerType.get(),
            "--name", "RomInntrengere",
            "--app-version", "1.0.0",
            "--vendor", "TopdownFunTown",
            "--input", libDir.get().asFile.absolutePath,
            "--main-jar", mainJar,
            "--main-class", "com.rominntrenger.main.Launcher",
            "--dest", outputDir.get().asFile.absolutePath,
            // Only the Java modules the game and its libraries use (found with jdeps
            // --print-module-deps), which keeps the package much smaller than a full runtime
            "--module-path", javaFxDir.get().asFile.absolutePath,
            "--add-modules", "java.base,java.compiler,java.desktop,java.scripting,java.sql,jdk.jfr," +
                "jdk.unsupported,javafx.controls,javafx.fxml",
            "--jlink-options", "--strip-debug --no-man-pages --no-header-files",
        )
        if (isWindows && installerType.get() != "app-image") {
            args("--win-menu", "--win-shortcut")
        }
    }
}
