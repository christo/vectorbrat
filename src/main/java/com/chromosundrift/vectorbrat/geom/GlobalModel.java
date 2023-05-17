package com.chromosundrift.vectorbrat.geom;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Vector display model with coordinates from (-1.0,-1.0) (top left) to 1.0, 1.0 (bottom right)
 * TODO WIP threadsafety - fix races and deadlocks and consider immutable updates
 */
public class GlobalModel implements Model {

    private static final Logger logger = LoggerFactory.getLogger(GlobalModel.class);

    private final ReentrantLock lock = new ReentrantLock();
    private final List<Polyline> polylines;
    private final List<Point> points;
    private final String name;

    public GlobalModel() {
        this("untitled");
    }

    public GlobalModel(String name) {
        this(name, new ArrayList<>());
    }

    public GlobalModel(String name, List<Polyline> polylines) {
        this(name, polylines, new ArrayList<>());
    }

    public GlobalModel(String name, List<Polyline> polylines, List<Point> points) {
        this.polylines = polylines;
        this.points = points;
        this.name = name;
    }

    GlobalModel add(Point point) {
        try {
            lock.lock();
            points.add(point);
        } finally {
            lock.unlock();
        }
        return this;
    }

    GlobalModel add(Polyline p) {
        try {
            lock.lock();
            polylines.add(p);
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * Stream of just the polylines (not points).
     *
     * @return polylines.
     */
    @Override
    public Stream<Polyline> polylines() {
        try {
            lock.lock();
            return new ArrayList<>(polylines).stream();
        } finally {
            lock.unlock();
        }
    }

    List<Polyline> _polylines() {
        return this.polylines;
    }

    public List<Point> _points() {
        return this.points;
    }

    /**
     * Stream of just the points (not polylines)
     *
     * @return points.
     */
    @Override
    public Stream<Point> points() {
        try {
            lock.lock();
            return new ArrayList<>(points).stream();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            lock.lock();
            return polylines.size() == 0 && points.size() == 0;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int countVertices() {
        try {
            lock.lock();
            return polylines.stream().mapToInt(Polyline::size).sum() + points.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Model{" +
                "polyliness=" + polylines +
                ", points=" + points +
                '}';
    }

    public String getName() {
        return name;
    }

    @Override
    public int countPolylines() {
        return polylines.size();
    }

    @Override
    public int countPoints() {
        return points.size();
    }

    /**
     * Returns the closest model point to the given point.
     */
    public Point closestTo(Point other) {
        // TODO make polylines startable from any point, not just first
        TreeSet<Point> closestToRef = new TreeSet<>(other.dist2Point());
        // for now only consider points and the first point of each polyline
        closestToRef.addAll(polylines.stream().map(p -> p._points()[0]).toList());
        closestToRef.addAll(_points());
        return closestToRef.first();
    }

    @Override
    public Model scale(float factor) {
        GlobalModel m = new GlobalModel();
        polylines().map(polyline -> polyline.scale(factor)).forEach(m::add);
        points().map(point -> point.scale(factor)).forEach(m::add);
        if (this.countVertices() != m.countVertices()) {
            throw new IllegalStateException("scaled model should have same number of points");
        }
        return m;
    }

    @Override
    public Stream<Line> lines() {
        return this.polylines().flatMap(Polyline::lines);
    }
}
