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
    // JInput (gamepads) is checked in with its native libraries, see lib/jinput
    implementation(files("lib/jinput/jinput-2.0.7.jar", "lib/jinput/jutils-1.0.0.jar"))
}

// The project predates Gradle, so it keeps its original layout:
// sources and the FXML file in src/, game assets in assets/
sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))
            // An old test that no longer compiles
            exclude("test/**", "main/**")
        }
        resources {
            setSrcDirs(listOf("src", "assets"))
            exclude("**/*.java", "test/**", "main/**", "META-INF/**")
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass = "com.rominntrenger.main.RomInntrenger"
    applicationDefaultJvmArgs = listOf("-Djava.library.path=lib/jinput")
}

tasks.named<JavaExec>("run") {
    // Saves (myFile.data) and the JInput natives are relative to the repository root
    workingDir = rootDir
}
