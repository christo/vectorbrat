package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;

public class PointTest {
    @Test
    public void testComparator() {
        Point o = new Point(0, 0);
        Point pythagoras = new Point(3, 4);
        Assert.assertEquals(o.dist(pythagoras), 5f, 0.01);
    }

    @Test
    public void colored() {
        Point p = new Point(0f, 0f, Color.WHITE);
        Point green = p.colored(Color.GREEN);
        Assert.assertEquals(0f, green.x(), 0.01);
        Assert.assertEquals(0f, green.y(), 0.01);
        TestUtils.assertPointColor("should be green now", Color.GREEN, green);
    }
}
