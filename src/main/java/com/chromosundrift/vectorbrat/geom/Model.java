package com.chromosundrift.vectorbrat.geom;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Vector display model with coordinates from (0.0,0.0) (top left) to 1.0, 1.0 (bottom right)
 * // TODO thread safety
 */
public class Model {

    private static Model EMPTY_MODEL = new Model();

    private List<Polygon> polygons;

    private final ReentrantLock lock = new ReentrantLock();

    public static Model midSquare() {
        Model m = new Model();
        Polygon square = Polygon.closed(
                new Point(0.25, 0.25),
                new Point(0.75, 0.25),
                new Point(0.75, 0.75),
                new Point(0.25, 0.75)
        );
        return m.add(square);
    }

    public Model() {
        polygons = new ArrayList<>();
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
