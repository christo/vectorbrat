package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;


public final class Polyline {
    private final String name;
    private final Color color; // future: remove dep on java.awt.Color
    private final Point[] _points;

    private Polyline(String name, Color color, Point... points) {
        this.name = name;
        this.color = color;
        this._points = points;
    }

    /**
     * Creates a closed polygon from the given points. This includes the join betwen last and first points.
     *
     * @param color  the color.
     * @param points the points.
     * @return the {@link Polyline}.
     */
    public static Polyline closed(String name, Color color, Point... points) {
        Point[] closedPoints = new Point[points.length + 1];
        for (int i = 0; i < points.length; i++) {
            closedPoints[i] = points[i].colored(color);
        }
        closedPoints[points.length] = points[0].colored(color);
        return new Polyline(name, color, closedPoints);
    }

    /**
     * Creaetes a sequence of connected lines with no implicit join from last to first.
     *
     * @param c      the color.
     * @param points the points.
     * @return the {@link Polyline}.
     */
    public static Polyline open(String name, Color c, Point... points) {
        Point[] ps = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            ps[i] = points[i].colored(c);
        }
        return new Polyline(name, c, ps);
    }

    static Polyline box(String name, float x1, float y1, float x2, float y2, Color c) {
        return closed(name, c,
                new Point(x1, y1, c),
                new Point(x2, y1, c),
                new Point(x2, y2, c),
                new Point(x1, y2, c)
        );
    }

    static Polyline box(float x1, float y1, float x2, float y2, Color c) {
        return box("box", x1, y1, x2, y2, c);
    }

    /**
     * The number of points in the polyline, expect one extra if closed.
     *
     * @return the size.
     */
    public int size() {
        return _points.length;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Polyline{'" + name + "' " +
                "color=" + color +
                ", _points=" + Arrays.toString(_points) +
                '}';
    }

    Point[] _points() {
        return this._points;
    }

    /**
     * Provides a new list of lines.
     *
     * @return this polyline as a list of lines.
     */
    public List<Line> lineList() {
        Point previous = null;
        LinkedList<Line> lines = new LinkedList<>();
        for (Point point : _points) {
            if (previous != null) {
                // skip adding the line if the points are the same (i.e. closed polygon)
                if (!previous.equals(point)) {
                    lines.add(new Line(previous, point));
                }
            }
            previous = point;
        }

        return lines;
    }

    /**
     * Returns our point closest to other. Explodes if this Polyline has no points.
     *
     * @param other comparison point.
     * @return closest point to the other.
     */
    public Point closest(Point other) {
        //noinspection OptionalGetWithoutIsPresent
        return Arrays.stream(_points).min(other.dist2Point()).get();
    }

    public Polyline scale(float factorX, float factorY) {
        List<Point> points = Arrays.stream(_points).map(point -> point.scale(factorX, factorY)).toList();
        Point[] newPoints = new Point[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            newPoints[i] = point;
        }
        return new Polyline(this.name, this.color, newPoints);
    }

    public int[] xZeroScaled(float scale) {
        int[] xVals = new int[_points.length];
        for (int i = 0; i < _points.length; i++) {
            xVals[i] = (int) ((_points[i].x() / 2 + 0.5) * scale);
        }
        return xVals;
    }

    public int[] yZeroScaled(float scale) {
        int[] yVals = new int[_points.length];
        for (int i = 0; i < _points.length; i++) {
            yVals[i] = (int) ((_points[i].y() / 2 + 0.5) * scale);
        }
        return yVals;
    }

    public Stream<Line> lines() {
        return lineList().stream();
    }

    public String getName() {
        return name;
    }

    public Polyline offset(float dx, float dy) {
        // this array shit is bugging me
        Point[] points = new Point[this._points.length];
        for (int i = 0; i < this._points.length; i++) {
            points[i] = this._points[i].offset(dx, dy);
        }
        return new Polyline(name, color, points);
    }

    public Polyline colored(Color c) {
        Point[] points = new Point[this._points.length];
        for (int i = 0; i < this._points.length; i++) {
            points[i] = this._points[i].colored(c);
        }
        return new Polyline(this.name, c, points);
    }
}
