package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PathPlannerTest {

    private static final Logger logger = LoggerFactory.getLogger(PathPlannerTest.class);
    public static final double FLOAT_DELTA = 0.01;

    @Test
    public void testMaxPointDistance() {
        int pointsPerUnit = 5;
        Model model = Model.testPattern1();
        Point start = new Point(0f, 0f);
        PathPlanner pp = new PathPlanner(5, pointsPerUnit);
        pp.plan(model, start);
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
            int index = (i + 1) % xs.size();
            Point neighbour = new Point(xs.get(index), ys.get(index));
            float dist = p.dist(neighbour);

            if (dist > maxDist) {
                float diff = dist - maxDist;
                String mesg = i + " dist " + dist + " (diff " + diff + ")" + " : " + p + " <-> " + neighbour;
                logger.info(mesg);
                tooBigs.add(mesg);
            }
        }
        Point first = new Point(xs.get(0), ys.get(0));
        Point last = new Point(xs.get(xs.size() - 1), ys.get(ys.size() - 1));
        assertEquals(0f, first.dist(last), FLOAT_DELTA);
        assertTrue("%s / %s pairs are too distant".formatted(tooBigs.size(), xs.size()), tooBigs.isEmpty());
    }

    @Test
    public void testInterpolateTo() {
        PathPlanner pp = new PathPlanner(1, 5);
        pp.interpolate(new Point(0,0), new Point(1, 1), 5);
        ArrayList<Float> xs = pp.getXs();
        ArrayList<Float> ys = pp.getYs();
        assertTrue(xs.size() == ys.size());
        assertTrue(xs.size() > 5);


    }
}
