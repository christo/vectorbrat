package com.chromosundrift.vectorbrat.laser;

import com.chromosundrift.vectorbrat.geom.Box;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.SimplePather;
import com.chromosundrift.vectorbrat.physics.BeamPhysics;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import com.chromosundrift.vectorbrat.physics.LinearBeamPhysics;
import com.chromosundrift.vectorbrat.physics.TimeMachine;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

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
        LaserSimulator basic = new LaserSimulator(LaserSpec.laserWorld1600Pro(), tuning, physics, clock);

        List<Point> points = new Box(-1f, -1f, 1f, 1f).points().toList();
        basic.makePath(new SimplePather(points));
        int width = 100;
        int height = 100;
        Stream<Point> trail = basic.getTrail(width, height);
        // check the state of the raster
        trail.forEach(p -> logger.info(p.toString()));
    }

    private BeamTuning mkBeamTuning() {
        return BeamTuning.noInterpolation(30000);
    }
}