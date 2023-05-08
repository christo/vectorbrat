package com.chromosundrift.vectorbrat.geom;

import org.jaudiolibs.jnajack.lowlevel.JackLibrary;

import java.util.Arrays;
import java.util.stream.Stream;

public final class Polygon {
    private final boolean _closed;
    private final Point[] _points;

    private Polygon(boolean closed, Point... points) {
        this._closed = closed;
        this._points = points;
    }

    public static Polygon closed(Point... points) {
        return new Polygon(true, points); // WART: assuming no retained points reference at call site
    }

    public static Polygon open(Point... points) {
        return new Polygon(false, points); // WART: assuming no retained points reference at call site
    }

    public Stream<Point> points() {
        return Arrays.stream(_points);
    }

    public boolean isClosed() {
        return _closed;
    }
}
