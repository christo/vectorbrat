package com.chromosundrift.vectorbrat.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SignalBufferTest {

    @Test
    public void constructor() {
        SignalBuffer sb = new SignalBuffer(5);
        assertEquals(0, sb.getActualSize());
        assertEquals(0f, sb.getX(0), 0.001f);
        assertEquals(0f, sb.getY(1), 0.001f);
        assertEquals(0f, sb.getR(2), 0.001f);
        assertEquals(0f, sb.getG(3), 0.001f);
        assertEquals(0f, sb.getB(4), 0.001f);
    }
}