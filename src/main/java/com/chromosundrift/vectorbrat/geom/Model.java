package com.chromosundrift.vectorbrat.geom;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Vector display model with coordinates from (0.0,0.0) (top left) to 1.0, 1.0 (bottom right)
 * TODO needs to be threadsafe
 */
public class Model {

    private final ReentrantLock lock = new ReentrantLock();
    private final List<Polygon> polygons;
    private final List<Point> points;

    public Model() {
        polygons = new ArrayList<>();
        points = new ArrayList<>();
    }

    public static Model testPattern1() {
        Model m = new Model();
        m.add(createMidSquare());
        m.add(new Point(0.5d, 0.5d));

        m.add(Polygon.open(new Point(0.45, 0.30), new Point(0.5, 0.25), new Point(0.55, 0.3)));
        m.add(Polygon.open(new Point(0.75, 0.75), new Point(0.85, 0.85)));
        return m;
    }

    public static Model midSquare() {
        Model m = new Model();
        return m.add(createMidSquare());
    }

    private static Polygon createMidSquare() {
        Polygon square = Polygon.closed(
                new Point(0.25, 0.25),
                new Point(0.75, 0.25),
                new Point(0.75, 0.75),
                new Point(0.25, 0.75)
        );
        return square;
    }

    private Model add(Point point) {
        try {
            lock.lock();
            points.add(point);
        } finally {
            lock.unlock();
        }
        return this;
    }

    private Model add(Polygon p) {
        try {
            lock.lock();
            polygons.add(p);
        } finally {
            lock.unlock();
        }
        return this;
    }

    public Stream<Polygon> polygons() {
        try {
            lock.lock();
            return new ArrayList<>(polygons).stream();
        } finally {
            lock.unlock();
        }
    }

    public Stream<Point> points() {
        try {
            lock.lock();
            return new ArrayList<>(points).stream();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        try {
            lock.lock();
            return polygons.size() == 0 && points.size() == 0;
        } finally {
            lock.unlock();
        }
    }
}
