package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.assertEquals;

public class PolylineTest {
    @Test
    public void testPointColorInherited() {
        Polyline p = Polyline.open("test line", Color.GREEN, new Point(0, 0), new Point(1, 1));
        assertEquals(2, p.size());
        assertEquals(Color.GREEN, p._points()[0].getColor());
        assertEquals(Color.GREEN, p._points()[1].getColor());
    }

    @Test
    public void testClosedLines() {
        Polyline p = Polyline.closed("orange triangle", Color.ORANGE, new Point(0, 0), new Point(1, 1), new Point(1, 0));
        assertEquals(p.size(), 4); // 3 points plus start point as end
        int nLines = p.lines().size();
        assertEquals(nLines, 3); // 3 sides
        for (int i = 0; i < nLines; i++) {
            Line line = p.lines().get(i);
            assertEquals(Color.ORANGE, line.from().getColor());
            assertEquals(Color.ORANGE, line.to().getColor());
        }
    }
}
