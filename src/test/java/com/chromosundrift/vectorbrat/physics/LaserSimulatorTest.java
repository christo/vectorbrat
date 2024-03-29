package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.*;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserSpec;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link LaserSimulator}.
 */
public class LaserSimulatorTest {
    private static final Logger logger = LoggerFactory.getLogger(LaserSimulatorTest.class);

    /**
     * Test for the simulator which has no trail and no physics.
     */
    @Test
    public void justDot() {
        BeamTuning tuning = BeamTuning.noInterpolation(1_000_000);
        TimeMachine clock = new TimeMachine();
        BeamPhysics physics = new TeleportBeamPhysics();
        LaserSimulator simulator = new LaserSimulator(LaserSpec.laserWorld1600Pro(), tuning, physics, clock);

        Box box = new Box(-0.5f, -0.5f, 0.5f, 0.5f);
        List<Point> points = GeomUtils.linePoints(box).toList();

        // we expect 2 points per line although in a connected box, each vertex is repeated this way
        int expected = 8;
        assertEquals(expected, points.size());
        SimplePather simplePather = new SimplePather(points);
        assertEquals(expected, simplePather.size());
        simulator.makePath(simplePather);

        // with a sample rate of 1 mil, each sample takes 1000ns
        float oneSamplePerMicro = 1_000_000f;
        long nsOneSample = 1000L;
        simulator.setSampleRate(oneSamplePerMicro);
        for (int i = 0; i < 100; i++) {
            simulator.update();
            List<Point> trail = simulator.getTrail().toList();
            clock.add(nsOneSample);
            //logger.info("t={} trail points: {}", clock.getNs(), trail);
        }

        // TODO need to call update on the simulator to draw trail points and then make assertions about its content
    }


    @Test
    public void getTrail() {
        int pps = 100;
        BeamTuning tuning = BeamTuning.noInterpolation(pps);
        TimeMachine clock = new TimeMachine();
        BeamPhysics physics = new TeleportBeamPhysics();
        LaserSimulator ls = new LaserSimulator(LaserSpec.laserWorld1600Pro(), tuning, physics, clock);
        // 100 samples per point
        int sampleRate = 100 * pps;
        int nsPerSample = 1_000_000_000 / sampleRate;
        logger.info("nsPerSample: {}", nsPerSample);
        ls.setSampleRate(sampleRate);

        // now make a trail
        Model model = Pattern.testPattern1();
        List<Point> modelPoints = GeomUtils.linePoints(model).toList();
        SimplePather p = new SimplePather(modelPoints);
        ls.makePath(p);
        // front buffer should now contain all the points from the model

        // this just sanity checks that SimplePather has done the expected
        assertEquals(46, modelPoints.size());
        assertEquals(46, ls.getFrontSize());
        int samplesPerPov = sampleRate / LaserSimulator.FPS_POV;

        // run the simulation to fill up the trail
        for (int i = 0; i < samplesPerPov; i++) {
            ls.update();
            clock.add(nsPerSample);
        }
        // assert that trail is as long as it takes to get to black
        assertEquals(samplesPerPov, ls.getTrail().count());

    }
}