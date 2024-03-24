package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;
import org.junit.Test;

import static org.junit.Assert.*;

public class LinearBeamPhysicsTest {

    @Test
    public void timeStep() {

        LinearBeamPhysics sut = new LinearBeamPhysics(2d, 0.5f);
        BeamState bs = new BeamState(0d, 1d, 0d, 0d, Rgb.BLACK);

        // we are at 0,1 BLACK and want -1,-1 WHITE
        // xyRate is 2 units/s, requested timestep is 1000
        sut.timeStep(-1d, -1d, 1f, 1f, 1f, bs, 1_000_000_000L);
        // enough time for the beam to move to destination but colour only half way
        assertEquals(-1d, bs.xPos, 0.01d);
        assertEquals(-1d, bs.yPos, 0.01d);
        assertEquals(0.5f, bs.rgb.red(), 0.01f);
        assertEquals(0.5f, bs.rgb.green(), 0.01f);
        assertEquals(0.5f, bs.rgb.blue(), 0.01f);

    }
}