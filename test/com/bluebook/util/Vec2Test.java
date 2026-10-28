package com.bluebook.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Vec2Test {

    private static final double EPSILON = 1e-9;

    @Test
    void addAndSubtract() {
        Vec2 sum = Vec2.add(new Vec2(1, 2), new Vec2(3, 4));
        assertEquals(4, sum.getX(), EPSILON);
        assertEquals(6, sum.getY(), EPSILON);

        Vec2 difference = Vec2.subtract(new Vec2(1, 2), new Vec2(3, 4));
        assertEquals(-2, difference.getX(), EPSILON);
        assertEquals(-2, difference.getY(), EPSILON);
    }

    @Test
    void normalizedVectorHasLengthOne() {
        Vec2 normalized = new Vec2(3, 4).getNormalizedVector();
        assertEquals(1, normalized.getMagnitude(), EPSILON);
        assertEquals(0.6, normalized.getX(), EPSILON);
        assertEquals(0.8, normalized.getY(), EPSILON);
    }

    @Test
    void angleInDegrees() {
        assertEquals(0, Vec2.RIGHT.getAngleInDegrees(), EPSILON);
        assertEquals(90, Vec2.DOWN.getAngleInDegrees(), EPSILON);
        assertEquals(0, Vec2.ZERO.getAngleInDegrees(), EPSILON);
    }

    @Test
    void constantsCannotBeChanged() {
        Vec2.ZERO.setX(5);
        assertEquals(0, Vec2.ZERO.getX(), EPSILON);
    }
}
