package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;

import java.awt.Color;
import java.util.LinkedList;

public class ModelTest {
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
}
