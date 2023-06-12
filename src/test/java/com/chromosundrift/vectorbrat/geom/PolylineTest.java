package com.chromosundrift.vectorbrat.geom;

import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static com.chromosundrift.vectorbrat.geom.TestUtils.assertPointColor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PolylineTest {
    private static Polyline getOrangeTriangle() {
        return Polyline.closed("orange triangle", Rgb.ORANGE, new Point(0, 0), new Point(1, 1), new Point(1, 0));
    }

    @Test
    public void testPointColorInherited() {
        Polyline p = Polyline.open("test line", Rgb.GREEN, new Point(0, 0), new Point(1, 1));
        assertEquals(2, p.size());
        assertEquals(Rgb.GREEN, p._points()[0].getColor());
        assertEquals(Rgb.GREEN, p._points()[1].getColor());
    }

    @Test
    public void testClosedLines() {
        Polyline p = getOrangeTriangle();
        assertEquals(p.size(), 4); // 3 points plus start point as end
        List<Line> lines = p.lineList();
        int nLines = lines.size();
        assertEquals(nLines, 3); // 3 sides
        for (int i = 0; i < nLines; i++) {
            Line line = lines.get(i);
            assertEquals(Rgb.ORANGE, line.from().getColor());
            assertEquals(Rgb.ORANGE, line.to().getColor());
        }
    }

    @Test
    public void colored() {
        Polyline green = getOrangeTriangle().colored(Rgb.GREEN);
        Stream<Line> lines = green.lines();
        lines.forEach(line -> {
            assertPointColor("from should be green", Rgb.GREEN, line.from());
            assertPointColor("to should be green", Rgb.GREEN, line.to());
        });
    }

    @Test
    public void testLinePoints() {
        Polyline orangeTriangle = getOrangeTriangle();
        orangeTriangle.lines().flatMap(line -> Stream.of(line.from(), line.to())).forEach(point -> {
            boolean found = false;
            Point[] points = orangeTriangle._points();
            for (Point value : points) {
                if (value.equals(point)) {
                    found = true;
                    break;
                }
            }
            assertTrue("I don't get the point", found);
        });
    }
}
