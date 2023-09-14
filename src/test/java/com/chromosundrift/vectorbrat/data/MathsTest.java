package com.chromosundrift.vectorbrat.data;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MathsTest {

    @Test
    public void descendingRing() {
        assertEquals(List.of(4, 3, 2, 1, 0), Maths.decRing(5, 4).toList());
        assertEquals(List.of(2, 1, 0, 4, 3), Maths.decRing(5, 2).toList());
    }
}