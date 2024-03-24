package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.Assert.*;

/**
 * Unit test for {@link ConstAccelBeamPhysics}
 */
public class ConstAccelBeamPhysicsTest {

    @Test
    public void timeStepFromRest() {
        // change in speed per second
        double xyAccel = 1f;
        // maximum speed permitted
        double maxSpeed = 2f;
        // change in colour per second
        float colourRate = 0.5f;
        ConstAccelBeamPhysics sut = new ConstAccelBeamPhysics(xyAccel, maxSpeed, colourRate);
        BeamState bs = new BeamState(0d, 1d, 0d, 0d, Rgb.BLACK);

        sut.timeStep(1d, 0d, 1f, 1f, 1f, bs, 1_000_000_000L);
        // 1 ms has elapsed which is 1/1000 of 1 second so acceleration is 0.001
        // therefore velocity should be (0.001, -0.001)
        // and position should be (0.001, 0.999)
        assertEquals(0.001d, bs.xVel, 0.0001d);
        assertEquals(-0.001d, bs.yVel, 0.0001d);
        assertEquals(0.001d, bs.xPos, 0.0001d);
        assertEquals(0.999d, bs.yPos, 0.0001d);

        // continue to demand the same
        sut.timeStep(1d, 0d, 1f, 1f, 1f, bs, 1_000_000L);
        assertEquals(0.002d, bs.xVel, 0.0001d);
        assertEquals(-0.002d, bs.yVel, 0.0001d);
        assertEquals(0.003d, bs.xPos, 0.0001d);
        assertEquals(0.997d, bs.yPos, 0.0001d);
    }

    /**
     * Test to calculate the effects of approaching the demand point at high but not maximum speed.
     */
    @Test
    public void timeStepsWhileSpeeding() throws IOException {
        // TODO calculate assertions for this test and add assertions
        // change in speed per second
        double xyAccel = 1d;
        // maximum speed permitted
        double maxSpeed = 2d;
        // change in colour per second
        float colourRate = 0.5f;
        ConstAccelBeamPhysics sut = new ConstAccelBeamPhysics(xyAccel, maxSpeed, colourRate);
        BeamState bs = new BeamState(0d, -0.9d, 0d, -1.9d, Rgb.BLACK);
        long t = 0;
        long dt = 1_000_000L;
        double x = 0;
        double y = -1;
        StringBuilder csv = new StringBuilder("t, x, y, dx, dy, errX, errY, err\n");
        for (int i = 0; i < 100; i++) {
            t += dt;
            sut.timeStep(x, y, 1f, 1f, 1f, bs, dt);
            double positionErrorX = bs.xPos - x;
            double positionErrorY = bs.yPos - y;
            double positionError = Math.sqrt(positionErrorX * positionErrorX + positionErrorY * positionErrorY);
            csv.append("%s,%s,%s,%s,%s,%s,%s,%s\n".formatted(t, bs.xPos, bs.xPos, bs.xVel, bs.yVel, positionErrorX, positionErrorY, positionError));
        }
        Path writtenFile = Files.writeString(Path.of("/Users/christo/Desktop/testout.csv"), csv.toString(), StandardOpenOption.CREATE);
        System.out.println("wrote csv file: " + writtenFile);
    }

}