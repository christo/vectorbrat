package com.chromosundrift.vectorbrat.geom;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
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
    private final List<Polyline> polylines;
    private final List<Point> points;
    private final String name;

    public Model() {
        this("empty", new ArrayList<>(), new ArrayList<>());
    }

    public Model(String name, List<Polyline> polylines, List<Point> points) {
        this.polylines = polylines;
        this.points = points;
        this.name = name;
    }

    public Model(String name) {
        this(name, new ArrayList<>(), new ArrayList<>());
    }

    public static Model testPattern1() {
        Model m = new Model("test pattern 1");
        m.add(Polyline.createMidSquare(Color.ORANGE));
        // centre dots
        for(float i=0; i<0.4; i+= 0.1) {
            m.add(new Point(0.0f, i, Color.MAGENTA));
        }

        // top arrow
        Color c = Color.CYAN;
        m.add(Polyline.open(c, new Point(-0.15f, -0.35f, c), new Point(0.0f, -0.5f, c), new Point(0.15f, -0.35f, c)));
        // bottom right handle
        c = Color.PINK;
        m.add(Polyline.open(c, new Point(0.5f, 0.5f, c), new Point(0.75f, 0.75f, c)));

        // top right box
        m.add(Polyline.box(0.9f, -1f, 1f, -0.9f, Color.GREEN));
        // bottom right box
        m.add(Polyline.box(0.9f, 0.9f, 1f, 1f, Color.GREEN));
        // bottom left box
        m.add(Polyline.box(-1f, 0.9f, -0.9f, 1f, Color.GREEN));
        // opt left box
        m.add(Polyline.box(-1f, -1f, -0.9f, -0.9f, Color.GREEN));
        logger.info("created test pattern: " + m);
        return m;
    }

    public static Model midSquare(Color c) {
        Model m = new Model("mid square");
        return m.add(Polyline.createMidSquare(c));
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

    private Model add(Polyline p) {
        try {
            lock.lock();
            polylines.add(p);
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * Stream of just the polygons (not points).
     *
     * @return polygons.
     */
    public Stream<Polyline> polylines() {
        try {
            lock.lock();
            return new ArrayList<>(polylines).stream();
        } finally {
            lock.unlock();
        }
    }

    List<Polyline> _polygons() {
        return this.polylines;
    }

    public List<Point> _points() {
        return this.points;
    }

    /**
     * Stream of just the points (not polygons)
     *
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
            return polylines.size() == 0 && points.size() == 0;
        } finally {
            lock.unlock();
        }
    }

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
                "polygons=" + polylines +
                ", points=" + points +
                '}';
    }

    public String getName() {
        return name;
    }

    public int countPolygons() {
        return polylines.size();
    }

    public int countPoints() {
        return points.size();
    }

    /**
     * Returns the closest model point to the given point.
     * @param ref
     * @return
     */
    public Point closestTo(Point ref) {

        // TODO make polygons startable from any point, not just first

        TreeSet<Point> closestToRef = new TreeSet<>(ref.distToComparator());
        // for now only consider points and the first point of each polygon
        closestToRef.addAll(polylines.stream().map(p -> p._points()[0]).toList());
        closestToRef.addAll(_points());
        return closestToRef.first();
    }

    public Model scale(float factor) {
        Model m = new Model();
        polylines().map(polyline -> polyline.scale(factor)).forEach(m::add);
        points().map(point -> point.scale(factor)).forEach(m::add);
        if (this.countVertices() != m.countVertices()) {
            throw new IllegalStateException("scaled model should have same number of points");
        }
        return m;
    }
}
