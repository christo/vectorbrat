package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;
import org.junit.Test;

import static org.junit.Assert.*;

public class LinearBeamPhysicsTest {

    @Test
    public void timeStep() {

        LinearBeamPhysics sut = new LinearBeamPhysics(2f, 0.5f);
        BeamState bs = new BeamState(0f, 1f, 0f, 0f, Rgb.BLACK);

        // we are at 0,1 BLACK and want -1,-1 WHITE
        // xyRate is 2 units/s, requested timestep is 1000
        sut.timeStep(-1f, -1f, 1f, 1f, 1f, bs, 1_000_000_000L);
        // enough time for the beam to move to destination but colour only half way
        assertEquals(-1, bs.xPos, 0.01);
        assertEquals(-1, bs.yPos, 0.01);
        assertEquals(0.5, bs.rgb.red(), 0.01);
        assertEquals(0.5, bs.rgb.green(), 0.01);
        assertEquals(0.5, bs.rgb.blue(), 0.01);

    }
}