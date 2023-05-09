package com.chromosundrift.vectorbrat.geom;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Vector display model with coordinates from (-1.0,-1.0) (top left) to 1.0, 1.0 (bottom right)
 * TODO WIP threadsafety - fix races and deadlocks and consider immutable updates
 */
public class Model {

    private static final Logger logger = LoggerFactory.getLogger(Model.class);

    // TODO get all model shapes as a set of vector segments
    private final ReentrantLock lock = new ReentrantLock();
    private final List<Polygon> polygons;
    private final List<Point> points;

    public Model() {
        polygons = new ArrayList<>();
        points = new ArrayList<>();
    }

    public static Model testPattern1() {
        Model m = new Model();
        m.add(createMidSquare(Color.ORANGE));
        // centre dot
        m.add(new Point(0.0d, 0.0d, Color.MAGENTA));

        // top arrow
        Color c = Color.CYAN;
        m.add(Polygon.open(c, new Point(-0.15, -0.35, c), new Point(0.0, -0.5, c), new Point(0.15, -0.35, c)));
        // bottom right handle
        c = Color.PINK;
        m.add(Polygon.open(c, new Point(0.5, 0.5, c), new Point(0.75, 0.75, c)));

        // bounding box
        m.add(box(-1f, -1f, 1f, 1f, Color.GREEN));
        logger.info("created test pattern: " + m);
        return m;
    }

    public static Model midSquare(Color c) {
        Model m = new Model();
        return m.add(createMidSquare(c));
    }

    private static Polygon box(float x1, float y1, float x2, float y2, Color c) {
        return Polygon.closed(c,
                new Point(x1, y1, c),
                new Point(x2, y1, c),
                new Point(x2, y2, c),
                new Point(x1, y2, c)
        );
    }
    
    private static Polygon createMidSquare(Color c) {
        return box(-0.5f, -0.5f, 0.5f, 0.5f, c);
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

    /**
     * Stream of just the polygons (not points).
     * @return polygons.
     */
    public Stream<Polygon> polygons() {
        try {
            lock.lock();
            return new ArrayList<>(polygons).stream();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stream of just the points (not polygons)
     * @return points.
     */
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

    public int countVertices() {
        try {
            lock.lock();
            return polygons.stream().mapToInt(Polygon::size).sum() + points.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Model{" +
                "polygons=" + polygons +
                ", points=" + points +
                '}';
    }
}
