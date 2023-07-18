package com.chromosundrift.vectorbrat.laser;

import java.util.ArrayList;
import java.util.List;

import com.chromosundrift.vectorbrat.geom.Point;

public class DemoPoints {
    public static List<Point> triangle() {
        ArrayList<Point> points = new ArrayList<>();
        for (float x = -1f; x <1f ; x+=0.1) {
            points.add(new Point(x, -1f, 1f, 0.5f, 0.5f));
        }
        for (float x = 1f; x >-1f ; x-=0.1) {
            points.add(new Point(x, -x, 0.5f, 1f, 0.5f));
        }
        for (float y = -1f; y <1f ; y+=0.1) {
            points.add(new Point(-1f, y, 0.5f, 0.5f, 1f));
        }
        return points;
    }
}
