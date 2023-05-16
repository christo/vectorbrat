package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;

public class PointTest {
    @Test
    public void testComparator() {
        Point o = new Point(0, 0);
        Point pythagoras = new Point(3, 4);
        Assert.assertEquals(o.dist(pythagoras), 5f, 0.01);
    }
}
