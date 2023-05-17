package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class GlobalModelTest {

    @Test
    public void lines() {
        Color c = Color.BLUE;
        int n = 10; // n polylines each with n points
        List<Polyline> polylines = Pattern.sineWaves(c, n);
        GlobalModel gm = new GlobalModel("foo", polylines);
        long nLines = gm.lines().count();
        Assert.assertEquals(n * (n - 1), nLines);
    }

    @Test
    public void testGridLines() {
        int cols = 4;
        int rows = 4;
        GlobalModel model = Pattern.boxGrid(cols, rows, Color.CYAN);
        LinkedList<Line> lines = new LinkedList<>();
        for (Polyline pl : model._polylines()) {
            lines.addAll(pl.lineList());
        }
        int boxSides = 4;
        lines.forEach(line -> System.out.println("line = " + line));
        Assert.assertEquals(cols * rows * boxSides, lines.size());
    }

    @Test
    public void colored() {
        Point greenOrigin = new Point(0f, 0f, Color.GREEN);
        Point blueMax = new Point(1f, 1f, Color.BLUE);
        Polyline foo1 = Polyline.open("foo", Color.RED, greenOrigin, blueMax);
        Point orangeMin = new Point(-1f, -1f, Color.ORANGE);
        Model foo = new GlobalModel("foo", List.of(foo1), List.of(orangeMin));
        Model colored = foo.colored(Color.MAGENTA);
        List<Polyline> offColorPolylines = colored.polylines()
                .filter(polyline -> !polyline.getColor().equals(Color.MAGENTA))
                .toList();
        Assert.assertEquals("polylines should have all been green", emptyList(), offColorPolylines);
        List<Point> offColorPoints = colored.points()
                .filter(polyline -> !polyline.getColor().equals(Color.MAGENTA))
                .toList();
        Assert.assertEquals("points should have all been green", emptyList(), offColorPoints);

    }

}