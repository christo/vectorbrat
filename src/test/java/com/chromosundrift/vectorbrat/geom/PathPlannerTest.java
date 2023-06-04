package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

import com.chromosundrift.vectorbrat.Config;

public class PathPlannerTest {

    private static final Logger logger = LoggerFactory.getLogger(PathPlannerTest.class);
    public static final double FLOAT_DELTA = 0.01;

    @Ignore("not sure what's wrong with this one right now")
    @Test
    public void testMaxPointDistance() {
        int pointsPerUnit = 15;
        Model model = Pattern.testPattern1();
        Point start = new Point(0f, 0f);
        Config c = getTestConfig();
        Interpolator pp = new Interpolator(c);
        pp.planNaive(model, start);
        ArrayList<Float> xs = pp.getXs();
        ArrayList<Float> ys = pp.getYs();
        ArrayList<Float> rs = pp.getRs();
        ArrayList<Float> gs = pp.getGs();
        ArrayList<Float> bs = pp.getBs();
        // calculcate the maximum permitted distance between interpolated points
        float maxDist = (float) (1.0 / pointsPerUnit) + 0.04f;
        logger.info("maxDist = " + maxDist);
        ArrayList<String> tooBigs = new ArrayList<>();
        for (int i = 0; i < xs.size(); i++) {
            Point p = new Point(xs.get(i), ys.get(i), rs.get(i), gs.get(i), bs.get(i));
            int index = (i + 1);
            if (index < xs.size()) {
                Point neighbour = new Point(xs.get(index), ys.get(index));
                float dist = p.dist(neighbour);

                if (dist > maxDist) {
                    float diff = dist - maxDist;
                    String mesg = i + " dist " + dist + " (diff " + diff + ")" + " : " + p + " <-> " + neighbour;
                    logger.info(mesg);
                    tooBigs.add(mesg);
                }
            }
        }
        assertTrue("%s / %s pairs are too distant".formatted(tooBigs.size(), xs.size()), tooBigs.isEmpty());
    }

    @Test
    public void testInterpolateTo() {
        Config c = getTestConfig();
        Interpolator pp = new Interpolator(c);
        pp.interpolate(new Point(0, 0), new Point(1, 1), 1, 5);
        ArrayList<Float> xs = pp.getXs();
        ArrayList<Float> ys = pp.getYs();
        assertTrue(xs.size() == ys.size());
        assertTrue(xs.size() > 5);
    }

    private static Config getTestConfig() {
        Config c = new Config();
        c.setBlackPoints(0);
        c.setInterpolation(Interpolation.LINEAR);
        c.setPointsPerPoint(1);
        c.setPointsPerUnit(5);
        c.setPointsPerUnitOffset(0);
        c.setVertexPoints(1f);
        return c;
    }

    @Test
    public void testPlanNextNearest() {
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
