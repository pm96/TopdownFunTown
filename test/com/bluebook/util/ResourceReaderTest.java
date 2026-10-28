package com.bluebook.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceReaderTest {

    @Test
    void readsUtf8() {
        assertEquals("Rominntrengere æøå ÆØÅ\n", ResourceReader.readText("utf8.txt"));
    }

    @Test
    void missingResourceIsReported() {
        assertThrows(IllegalArgumentException.class, () -> ResourceReader.readText("nope.json"));
    }
}
