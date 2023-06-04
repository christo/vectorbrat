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

    @Test
    public void colored() {
        Point p = new Point(0f, 0f, Rgb.WHITE);
        Point green = p.colored(Rgb.GREEN);
        Assert.assertEquals(0f, green.x(), 0.01);
        Assert.assertEquals(0f, green.y(), 0.01);
        TestUtils.assertPointColor("should be green now", Rgb.GREEN, green);
    }
}
