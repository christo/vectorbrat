package com.chromosundrift.vectorbrat.audio;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.PathPlanner;
import com.chromosundrift.vectorbrat.geom.Point;

public class PathPlannerTest {

    @Test
    public void testBasic() {
        int pointsPerUnit = 5;
        PathPlanner pp = new PathPlanner(Model.testPattern1(), 5, pointsPerUnit, new Point(0f, 0f));
        ArrayList<Float> xs = pp.getXs();
        ArrayList<Float> ys = pp.getYs();
        ArrayList<Float> rs = pp.getRs();
        ArrayList<Float> gs = pp.getGs();
        ArrayList<Float> bs = pp.getBs();
        float maxDist = (float) (1.0 / pointsPerUnit) + 0.04f;
        System.out.println("maxDist = " + maxDist);
        ArrayList<String> tooBigs = new ArrayList<>();
        for (int i = 0; i < xs.size(); i++) {
            Point p = new Point(xs.get(i), ys.get(i), rs.get(i), gs.get(i), bs.get(i));
            int index = (i + 1) % xs.size();
            Point neighbour = new Point(xs.get(index), ys.get(index));
            float dist = p.dist(neighbour);

            if (dist > maxDist) {
                float diff = dist - maxDist;
                String mesg = i + " dist " + dist + " (diff " + diff + ")" + " : " + p + " <-> " + neighbour;
                System.out.println(mesg);
                tooBigs.add(mesg);
            }
        }

        Assert.assertTrue("%s / %s pairs are too distant".formatted(tooBigs.size(), xs.size()), tooBigs.isEmpty());
    }
}
