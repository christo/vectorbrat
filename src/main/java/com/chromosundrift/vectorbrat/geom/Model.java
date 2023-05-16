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

    /**
     * Creates a box grid in the given color with boxes the same size as
     * the gaps between them.
     *
     * @param nx number of boxes in x axis
     * @param ny number of boxes in y axis
     * @param c  color
     * @return the Model
     */
    public static Model boxGrid(int nx, int ny, Color c) {
        Model m = new Model();
        float extent = 2f;  // total width or height
        float offset = -1;  // add to extent to get coordinate range
        float w = extent/(nx*2+1);
        float h = extent/(ny*2+1);
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                float x = i * w * extent + offset + w;
                float y = j * h * extent + offset + h;
                m.add(Polyline.box(x, y, x + w, y + h, c));
            }
        }
        return m;
    }

    public static Model testPattern1() {
        Model m = new Model("test pattern 1");
        m.add(Polyline.createMidSquare(Color.ORANGE));
        // centre dots
        for (float i = 0; i < 0.4; i += 0.1) {
            m.add(new Point(0.0f, i, Color.MAGENTA));
        }

        Color c = Color.CYAN;
        m.add(Polyline.open("top arrow", c, new Point(-0.15f, -0.35f, c), new Point(0.0f, -0.5f, c), new Point(0.15f, -0.35f, c)));
        // dot x-aligned with arrow point and y-aligned with wing tips
        m.add(new Point(0.0f, -0.35f, Color.CYAN));
        // bottom right handle
        c = Color.PINK;
        m.add(Polyline.open("pink handle", c, new Point(0.5f, 0.5f, c), new Point(0.75f, 0.75f, c)));

        m.add(Polyline.box("topright", 0.9f, -1f, 1f, -0.9f, Color.GREEN));
        m.add(Polyline.box("bottomright", 0.9f, 0.9f, 1f, 1f, Color.GREEN));
        m.add(Polyline.box("bottomleft", -1f, 0.9f, -0.9f, 1f, Color.GREEN));
        m.add(Polyline.box("topleft", -1f, -1f, -0.9f, -0.9f, Color.GREEN));
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
     * Stream of just the polylines (not points).
     *
     * @return polylines.
     */
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
                "polyliness=" + polylines +
                ", points=" + points +
                '}';
    }

    public String getName() {
        return name;
    }

    public int countPolylines() {
        return polylines.size();
    }

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
