package com.chromosundrift.vectorbrat.geom;

import org.junit.Test;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.junit.Assert.*;

public class RgbTest {

    public static final float ONE_MILLI = 0.001f;

    @Test
    public void boundedLerpComplete() {
        Rgb rgb = new Rgb(0, 0, 0);

        long nsTimeStep = NANOSECONDS.convert(2, MILLISECONDS);

        Rgb newRgb = rgb.boundedLerp(1f, 0f, 0f, nsTimeStep, ONE_MILLI);
        assertEquals(1f, newRgb.red(), 0.0001);
        assertEquals(0f, newRgb.green(), 0.0001);
        assertEquals(0f, newRgb.blue(), 0.0001);
    }
}