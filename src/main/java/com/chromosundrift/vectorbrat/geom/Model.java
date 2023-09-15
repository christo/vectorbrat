package com.chromosundrift.vectorbrat.geom;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chromosundrift.vectorbrat.Config.SAMPLE_MIN;
import static com.chromosundrift.vectorbrat.Config.SAMPLE_RANGE;

/**
 * Immutable vector display model.
 * TODO WIP replace polylines with lines, remove all mutability and lock
 */
public class Model implements Geom {

    private static final boolean DEBUG = false;

    public static Model EMPTY = new Model("");

    private final List<Polyline> polylines;
    private final List<Point> points;
    private final String name;

    private Model(String name) {
        this(name, Collections.emptyList());
    }

    /**
     * Make an identical deep copy.
     */
    public Model deepClone() {
        return modify(Function.identity(), Function.identity());
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
    public Stream<Point> isoPoints() {
        return points.stream();
    }

    public boolean isEmpty() {
        return lines().toList().size() == 0 && points.size() == 0;
    }

    public int countVertices() {
        int pointsInLine = 2;
        return lines().mapToInt(l -> pointsInLine).sum() + points.size();
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
        return modify(polyline -> polyline.scale(factorX, factorY), point -> point.scale(factorX, factorY));
    }

    public Stream<Line> lines() {
        return polylines.stream().flatMap(Polyline::lines);
    }

    public Model merge(Model other) {
        List<Polyline> allPolylines = new ArrayList<>(this.polylines);
        allPolylines.addAll(other.polylines);
        List<Point> allPoints = new ArrayList<>(this.points);
        other.isoPoints().forEach(allPoints::add);
        return new Model(name + other.getName(), allPolylines, allPoints);
    }

    public Model offset(float dx, float dy) {
        return modify(pl -> pl.offset(dx, dy), p -> p.offset(dx, dy));
    }

    public Model coloured(final Rgb c) {
        return modify(pl -> pl.colored(c), p -> p.colored(c));
    }

    @Override
    public Optional<Point> closest(Point other) {
        return Stream.concat(polylines.stream().flatMap(polyline -> isoPoints()), isoPoints()).min(other.dist2Point());
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
     * // TODO WIP implement line cropping at boundary intersection
     *
     * @return cropped Model
     */
    public Model crop() {
        List<Point> inPoints = points.stream().filter(Point::inBounds).toList();
        List<Line> newLines = new ArrayList<>();

        // implement crop and cut for Model https://github.com/christo/vectorbrat/issues/33

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

        // TODO change model from Polylines to Lines
        return new Model(name, newLines.stream().map(Polyline::fromLine).toList(), inPoints);
    }

    public int countLines() {
        return lines().toList().size();
    }

    /**
     * Returns a new model derived from this one with colours modified by the given colour and
     * blend mode.
     *
     * @return
     */
    public Model blend(final Function<Rgb, Rgb> mode) {
        return modify(pl -> pl.blend(mode), p -> p.blend(mode));
    }

    public Model modify(final Function<Polyline, Polyline> plf, final Function<Point, Point> pf) {
        List<Polyline> polylines = this.polylines.stream().map(plf).collect(Collectors.<Polyline>toList());
        List<Point> allPoints = isoPoints().map(pf).collect(Collectors.toList());
        return new Model(this.name, polylines, allPoints);
    }
}
