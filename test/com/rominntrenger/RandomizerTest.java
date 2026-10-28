package com.rominntrenger;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RandomizerTest {

    @Test
    void picksElementsInProportionToTheirWeight() {
        Randomizer<String> randomizer = new Randomizer<>(new Random(42));
        randomizer.addElement(1, "rare");
        randomizer.addElement(3, "common");

        Map<String, Integer> counts = new HashMap<>();
        int draws = 40_000;
        for (int i = 0; i < draws; i++) {
            counts.merge(randomizer.randomElement(), 1, Integer::sum);
        }

        assertEquals(0.25, counts.get("rare") / (double) draws, 0.01);
        assertEquals(0.75, counts.get("common") / (double) draws, 0.01);
    }

    @Test
    void ignoresElementsWithoutWeight() {
        Randomizer<String> randomizer = new Randomizer<>(new Random(1));
        randomizer.addElement(0, "never");
        randomizer.addElement(1, "always");

        for (int i = 0; i < 1000; i++) {
            assertFalse(randomizer.randomElement().equals("never"));
        }
    }
}
