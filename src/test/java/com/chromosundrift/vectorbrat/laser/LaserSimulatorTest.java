package com.chromosundrift.vectorbrat.laser;

import com.chromosundrift.vectorbrat.geom.Box;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.SimplePather;
import com.chromosundrift.vectorbrat.physics.BeamPhysics;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import com.chromosundrift.vectorbrat.physics.LinearBeamPhysics;
import com.chromosundrift.vectorbrat.physics.TimeMachine;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class LaserSimulatorTest {
    private static final Logger logger = LoggerFactory.getLogger(LaserSimulatorTest.class);

    /**
     * Test for the simulator which has no trail and no physics.
     */
    @Test
    public void justDot() {
        BeamTuning tuning = mkBeamTuning();
        TimeMachine clock = new TimeMachine();
        BeamPhysics physics = new LinearBeamPhysics(1f, 1f);
        LaserSimulator simulator = new LaserSimulator(LaserSpec.laserWorld1600Pro(), tuning, physics, clock);

        Box box = new Box(-1f, -1f, 1f, 1f);
        List<Point> points = GeomUtils.linePoints(box).toList();

        // we expect 2 points per line although in a connected box, each vertex is repeated this way
        int expected = 8;
        assertEquals(expected, points.size());
        SimplePather simplePather = new SimplePather(points);
        assertEquals(expected, simplePather.size());
        simulator.makePath(simplePather);
        int width = 100;
        int height = 100;
        Stream<Point> trail = simulator.getTrail(width, height);
        List<Point> trailPoints = trail.toList();
        // TODO need to call update on the simulator to draw trail points and then make assertions about its content
    }

    private BeamTuning mkBeamTuning() {
        return BeamTuning.noInterpolation(30000);
    }
}