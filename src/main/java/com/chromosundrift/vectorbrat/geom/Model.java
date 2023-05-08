package com.chromosundrift.vectorbrat.geom;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Vector display model with coordinates from (0.0,0.0) (top left) to 1.0, 1.0 (bottom right)
 * not thread safe
 */
public class Model {

    private static final Model EMPTY_MODEL = new Model();
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
        m.add(new Point(0d, 0d));
        return m;
    }

    public void add(Model model) {
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            add(polygon);
        }
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            add(point);
        }
        // remove duplicates?
    }

    private void add(Point point) {
        points.add(point);
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

    public static Model empty() {
        return EMPTY_MODEL;
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

    public int size() {
        return polygons.size();
    }
}
