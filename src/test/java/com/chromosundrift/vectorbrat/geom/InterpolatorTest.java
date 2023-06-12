package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.laser.LaserTuning;

public class InterpolatorTest {

    private static final Logger logger = LoggerFactory.getLogger(InterpolatorTest.class);

    private static Config getTestConfig() {
        Config c = new Config();
        LaserTuning lt = new LaserTuning(30000, 1, 5, 1f, 0, 0);
        c.setInterpolation(Interpolation.LINEAR);
        c.setLaserTuning(lt);
        return c;
    }

    @Test
    public void testInterpolateTo() {
        Config c = getTestConfig();
        Interpolator pp = new Interpolator(c);
        pp.interpolate(new Point(0, 0), new Point(1, 1), 1, 5);
        ArrayList<Float> xs = pp.getXs();
        ArrayList<Float> ys = pp.getYs();
        assertEquals(xs.size(), ys.size());
        assertTrue(xs.size() > 5);
    }

    @Test
    public void plan() {
        Config c = getTestConfig();
        c.setInterpolation(Interpolation.QUINTIC);
        Interpolator pp = new Interpolator(c);
        Model m = Pattern.boxGrid(5, 5, Rgb.YELLOW);
        pp.plan(m);
        ArrayList<Float> xs = pp.getXs();
        ArrayList<Float> ys = pp.getYs();
        ArrayList<Float> rs = pp.getRs();
        ArrayList<Float> gs = pp.getGs();
        ArrayList<Float> bs = pp.getBs();
        Assert.assertEquals(xs.size(), ys.size());
        Assert.assertEquals(xs.size(), rs.size());
        Assert.assertEquals(xs.size(), gs.size());
        Assert.assertEquals(xs.size(), bs.size());
        logger.info("number of path points: {}", xs.size());
        Assert.assertTrue(xs.size() > 0);
    }
}
