package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.Stream;

public final class Polygon {
    private final Color color; // future: remove dep on java.awt.Color
    private final boolean _closed;
    private final Point[] _points;

    private Polygon(Color color, boolean closed, Point... points) {
        this.color = color;
        this._closed = closed;
        this._points = points;
    }

    public static Polygon closed(Color color, Point... points) {
        return new Polygon(color, true, points); // WART: assuming no retained points reference at call site
    }

    public static Polygon open(Color c, Point... points) {
        return new Polygon(c, false, points); // WART: assuming no retained points reference at call site
    }

    public Stream<Point> points() {
        return Arrays.stream(_points);
    }

    public boolean isClosed() {
        return _closed;
    }

    public int size() {
        return _closed ? _points.length : _points.length - 1;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Converts our {@link Polygon} to a {@link java.awt.Polygon} scaling from normalised using the given
     * factors.
     *
     * @param xScale the x-axis scaling factor
     * @param yScale the y-axis scaling factor
     * @return the awt Polygon
     */
    public java.awt.Polygon awt(final int xScale, final int yScale) {
        final java.awt.Polygon awt = new java.awt.Polygon();
        for (Point pt : _points) {
            awt.addPoint((int) (pt.x() * xScale), (int) (pt.y() * yScale));
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
}
