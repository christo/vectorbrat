package com.chromosundrift.vectorbrat.geom;


import com.google.common.collect.Collections2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chromosundrift.vectorbrat.Config.SAMPLE_MIN;
import static com.chromosundrift.vectorbrat.Config.SAMPLE_RANGE;

/**
 * Vector display model with coordinates from (-1.0,-1.0) (top left) to 1.0, 1.0 (bottom right)
 * TODO WIP threadsafety - fix races and deadlocks and consider immutable updates
 */
public class Model implements Geom {

    private final ReentrantLock lock = new ReentrantLock();
    private final List<Polyline> polylines;
    private final List<Point> points;
    private final String name;

    public Model() {
        this("");
    }

    public Model(String name) {
        this(name, new ArrayList<>());
    }

    public Model(String name, List<Polyline> polylines) {
        this(name, polylines, new ArrayList<>());
    }

    public Model(String name, List<Polyline> polylines, List<Point> points) {
        this.polylines = polylines;
        this.points = points;
        this.name = name;
    }

    /**
     * Scales and translates model space between 0-1. Also see {@link #denormalise()}.
     * @return a new Model
     */
    public Model normalise() {
        return scale(1 / SAMPLE_RANGE, 1 / SAMPLE_RANGE)
                .offset(-SAMPLE_MIN/SAMPLE_RANGE, -SAMPLE_MIN/SAMPLE_RANGE);
    }

    /**
     * Scales and translates model space across SAMPLE_RANGE. Also see {@link #normalise()}.
     * @return a new Model
     */
    public Model denormalise() {
        return scale(SAMPLE_RANGE, SAMPLE_RANGE)
                .offset(SAMPLE_MIN, SAMPLE_MIN);
    }

    Model add(Point point) {
        try {
            lock.lock();
            points.add(point);
        } finally {
            lock.unlock();
        }
        return this;
    }

    Model add(Polyline p) {
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
        return "GlobalModel{" +
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
     * Returns the closest model point to the given point - only considers isolated Points and Polyline start points.
     * This exists from when polylines could only be drawn from their start point
     *
     * @deprecated reconsider drawing polylines only from their start point
     */
    @Deprecated
    public Point closeish(Point other) {
        TreeSet<Point> closestToRef = new TreeSet<>(other.dist2Point());
        // for now only consider points and the first point of each polyline
        closestToRef.addAll(polylines.stream().map(p -> p._points()[0]).toList());
        closestToRef.addAll(_points());
        return closestToRef.first();
    }

    public Model scale(float factorX, float factorY) {
        Model m = new Model(this.name);
        polylines().map(polyline -> polyline.scale(factorX, factorY)).forEach(m::add);
        points().map(point -> point.scale(factorX, factorY)).forEach(m::add);
        if (this.countVertices() != m.countVertices()) {
            throw new IllegalStateException("scaled model should have same number of points");
        }
        return m;
    }

    public Stream<Line> lines() {
        return this.polylines().flatMap(Polyline::lines);
    }

    public Model merge(Model other) {
        List<Polyline> allPolylines = new ArrayList<>(this.polylines);
        other.polylines().forEach(allPolylines::add);
        List<Point> allPoints = new ArrayList<>(this.points);
        other.points().forEach(allPoints::add);
        return new Model(name + other.getName(), allPolylines, allPoints);
    }

    public Model offset(float dx, float dy) {
        List<Polyline> allPolylines = this.polylines.stream().map(pl -> pl.offset(dx, dy)).collect(Collectors.toList());
        List<Point> allPoints = this.points.stream().map(p -> p.offset(dx, dy)).collect(Collectors.toList());
        return new Model(name, allPolylines, allPoints);
    }

    public Model colored(Color color) {
        List<Polyline> polylines = this.polylines().map(polyline -> polyline.colored(color)).collect(Collectors.<Polyline>toList());
        List<Point> allPoints = this.points().map(p -> p.colored(color)).collect(Collectors.toList());
        return new Model(this.name, polylines, allPoints);
    }

    @Override
    public Optional<Point> closest(Point other) {
        return Stream.concat(polylines().flatMap(polyline -> points()), points()).min(other.dist2Point());
    }

    @Override
    public Optional<Box> bounds() {
        Optional<Box> box = Optional.empty();
        if (!this.isEmpty()) {
            // boomer way faster and simple enough if not functional / hipster-compliant
            float minX = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float minY = Float.MAX_VALUE;
            float maxY = Float.MIN_VALUE;
            for (Polyline polyline : polylines) {
                for (Point point : polyline._points()) {
                    minX = Math.min(point.x(), minX);
                    minY = Math.min(point.y(), minY);
                    maxX = Math.max(point.x(), maxX);
                    maxY = Math.max(point.y(), maxY);
                }
            }
            for (Point point : points) {
                minX = Math.min(point.x(), minX);
                minY = Math.min(point.y(), minY);
                maxX = Math.max(point.x(), maxX);
                maxY = Math.max(point.y(), maxY);
            }
            box = Optional.of(new Box(minX, minY, maxX, maxY));
        }
        return box;
    }
}
