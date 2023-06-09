package com.chromosundrift.vectorbrat.geom;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chromosundrift.vectorbrat.Config.SAMPLE_MIN;
import static com.chromosundrift.vectorbrat.Config.SAMPLE_RANGE;

/**
 * Immutable vector display model.
 * TODO WIP replace polylines with lines, remove all mutability and lock
 */
public class Model implements Geom {

    public static Model EMPTY = new Model("");

    private final List<Polyline> polylines;
    private final List<Point> points;
    private final String name;

    private Model(String name) {
        this(name, Collections.emptyList());
    }

    public Model(String name, List<Polyline> polylines) {
        this(name, polylines, Collections.emptyList());
    }

    public Model(String name, List<Polyline> polylines, List<Point> points) {
        this.polylines = Collections.unmodifiableList(polylines);
        this.points = Collections.unmodifiableList(points);
        this.name = name;
    }

    /**
     * Scales and translates model space between 0-1. Also see {@link #denormalise()}.
     *
     * @return a new Model
     */
    public Model normalise() {
        return scale(1 / SAMPLE_RANGE, 1 / SAMPLE_RANGE)
                .offset(-SAMPLE_MIN / SAMPLE_RANGE, -SAMPLE_MIN / SAMPLE_RANGE);
    }

    /**
     * Scales and translates model space across SAMPLE_RANGE. Also see {@link #normalise()}.
     *
     * @return a new Model
     */
    public Model denormalise() {
        return scale(SAMPLE_RANGE, SAMPLE_RANGE)
                .offset(SAMPLE_MIN, SAMPLE_MIN);
    }

    /**
     * Stream of just the points (not polylines)
     *
     * @return points.
     */
    public Stream<Point> points() {
        return points.stream();
    }

    public boolean isEmpty() {
        return lines().toList().size() == 0 && points.size() == 0;
    }

    public int countVertices() {
        return lines().mapToInt(l -> 2).sum() + points.size();
    }

    @Override
    public String toString() {
        return "Model{polylines=" + polylines + ", points=" + points + '}';
    }

    public String getName() {
        return name;
    }

    public int countPoints() {
        return points.size();
    }

    public Model scale(float factorX, float factorY) {
        List<Polyline> newPolylines = polylines.stream().map(polyline -> polyline.scale(factorX, factorY)).toList();
        List<Point> newPoints = points().map(point -> point.scale(factorX, factorY)).toList();
        Model m = new Model(this.name, newPolylines, newPoints);
        if (this.countVertices() != m.countVertices()) {
            throw new IllegalStateException("scaled model should have same number of points");
        }
        return m;
    }

    public Stream<Line> lines() {
        return polylines.stream().flatMap(Polyline::lines);
    }

    public Model merge(Model other) {
        List<Polyline> allPolylines = new ArrayList<>(this.polylines);
        other.polylines.stream().forEach(allPolylines::add);
        List<Point> allPoints = new ArrayList<>(this.points);
        other.points().forEach(allPoints::add);
        return new Model(name + other.getName(), allPolylines, allPoints);
    }

    public Model offset(float dx, float dy) {
        List<Polyline> allPolylines = this.polylines.stream().map(pl -> pl.offset(dx, dy)).collect(Collectors.toList());
        List<Point> allPoints = this.points.stream().map(p -> p.offset(dx, dy)).collect(Collectors.toList());
        return new Model(name, allPolylines, allPoints);
    }

    public Model colored(Rgb c) {
        List<Polyline> polylines = this.polylines.stream().map(pl -> pl.colored(c)).collect(Collectors.<Polyline>toList());
        List<Point> allPoints = points().map(p -> p.colored(c)).collect(Collectors.toList());
        return new Model(this.name, polylines, allPoints);
    }

    @Override
    public Optional<Point> closest(Point other) {
        return Stream.concat(polylines.stream().flatMap(polyline -> points()), points()).min(other.dist2Point());
    }

    @Override
    public Optional<Box> bounds() {
        if (!this.isEmpty()) {
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
            return Optional.of(new Box(minX, minY, maxX, maxY));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Crops the model to the range.
     *
     * @return cropped Model
     */
    public Model crop() {
        List<Point> inPoints = points.stream().filter(Point::inBounds).toList();
        List<Line> newLines = new ArrayList<>();

        lines().forEach(line -> {
            // check the line for intersections with the bounds
            if (!line.from().inBounds() && !line.to().inBounds()) {
                // both ends of line are out of bounds
                // it's possible the line has a segment that is in bounds
                // TODO find intersection points with bounds
            } else if (!line.from().inBounds()) {
                // only from is out of bounds
                // TODO construct new line from "from" to  intersection with bounds
            } else if (!line.to().inBounds()) {
                // only to is out of bounds
                // TODO construct new line from "to" to intersection with bounds
            } else {
                // both end points are in bounds, therefore whole line is in bounds
                // this won't always be true for other cut intersection shapes
                newLines.add(line);
            }
        });

        return new Model(name, newLines.stream().map(Polyline::fromLine).toList(), inPoints);
    }

    public int countLines() {
        return lines().toList().size();
    }
}
