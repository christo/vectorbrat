package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Stream;


public final class Polyline {
    private final Color color; // future: remove dep on java.awt.Color
    private final boolean _closed;
    private final Point[] _points;

    private Polyline(Color color, boolean closed, Point... points) {
        this.color = color;
        this._closed = closed;
        this._points = points;
    }

    public static Polyline closed(Color color, Point... points) {
        Point[] closedPoints = new Point[points.length + 1];
        System.arraycopy(points, 0, closedPoints, 0, points.length);
        closedPoints[points.length] = points[0];
        Polyline polyline = new Polyline(color, true, closedPoints);

        return polyline; // WART: assuming no retained points reference at call site
    }

    public static Polyline open(Color c, Point... points) {
        return new Polyline(c, false, points); // WART: assuming no retained points reference at call site
    }

    static Polyline box(float x1, float y1, float x2, float y2, Color c) {
        return closed(c,
                new Point(x1, y1, c),
                new Point(x2, y1, c),
                new Point(x2, y2, c),
                new Point(x1, y2, c)
        );
    }

    static Polyline createMidSquare(Color c) {
        return box(-0.5f, -0.5f, 0.5f, 0.5f, c);
    }

    public Stream<Point> points() {
        return Arrays.stream(_points);
    }

    public int size() {
        return _closed ? _points.length : _points.length - 1;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Converts our {@link Polyline} to a {@link java.awt.Polygon} scaling from normalised using the given
     * factors.
     *
     * TODO: fix for open polygons
     *
     * @param xScale the x-axis scaling factor
     * @param yScale the y-axis scaling factor
     * @return the awt Polygon
     */
    public java.awt.Polygon awt(final int xScale, final int yScale) {
        final java.awt.Polygon awt = new java.awt.Polygon();
        for (Point pt : _points) {
            // normalise to 0-1 then multiply by scale
            awt.addPoint((int) ((pt.x() / 2 + 0.5) * xScale), (int) ((pt.y() / 2 + 0.5) * yScale));
        }
        return awt;
    }

    @Override
    public String toString() {
        return "Polygon{" +
                "color=" + color +
                ", _closed=" + _closed +
                ", _points=" + Arrays.toString(_points) +
                '}';
    }

    Point[] _points() {
        return this._points;
    }
}
