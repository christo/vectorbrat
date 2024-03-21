package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConstAccelBeamPhysicsTest {

    @Test
    public void timeStepFromRest() {
        // change in speed per second
        float xyAccel = 1f;
        // maximum speed permitted
        float maxSpeed = 2f;
        // change in colour per second
        float colourRate = 0.5f;
        ConstAccelBeamPhysics sut = new ConstAccelBeamPhysics(xyAccel, maxSpeed, colourRate);
        BeamState bs = new BeamState(0f, 1f, 0f, 0f, Rgb.BLACK);

        sut.timeStep(1f, 0f, 1f, 1f, 1f, bs, 1_000_000L);
        // 1 ms has elapsed which is 1/1000 of 1 second so acceleration is 0.001
        // therefore velocity should be (0.001, -0.001)
        // and position should be (0.001, 0.999)
        assertEquals(0.001, bs.xVel, 0.0001);
        assertEquals(-0.001, bs.yVel, 0.0001);
        assertEquals(0.001, bs.xPos, 0.0001);
        assertEquals(0.999, bs.yPos, 0.0001);

        // continue to demand the same
        sut.timeStep(1f, 0f, 1f, 1f, 1f, bs, 1_000_000L);
        assertEquals(0.002, bs.xVel, 0.0001);
        assertEquals(-0.002, bs.yVel, 0.0001);
        assertEquals(0.003, bs.xPos, 0.0001);
        assertEquals(0.997, bs.yPos, 0.0001);
    }

}