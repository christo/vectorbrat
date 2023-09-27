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
 */
public class Model implements Geom {

    public static final Point CORNER_TOP_LEFT = new Point(-1f, -1f);
    public static final Point CORNER_TOP_RIGHT = new Point(1f, -1f);
    public static final Point CORNER_BOT_LEFT = new Point(-1f, 1f);
    public static final Point CORNER_BOT_RIGHT = new Point(1f, 1f);
    private static final Line BOUNDS_TOP = new Line(CORNER_TOP_LEFT, CORNER_TOP_RIGHT);
    private static final Line BOUNDS_BOT = new Line(CORNER_BOT_LEFT, CORNER_BOT_RIGHT);
    private static final Line BOUNDS_LEFT = new Line(CORNER_TOP_LEFT, CORNER_BOT_LEFT);
    private static final Line BOUNDS_RIGHT = new Line(CORNER_TOP_RIGHT, CORNER_BOT_RIGHT);
    private static final Box BOUNDS = new Box(CORNER_TOP_LEFT, CORNER_BOT_RIGHT);

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
     * Get the zero, one or two points of intersection of the line with bounds. Does
     * not find colinear intersections.
     */
    private static List<Point> boundsIntersect2(Line line) {
        List<Point> l = new ArrayList<>();
        line.npIntersect(Model.BOUNDS_TOP).ifPresent(l::add);
        line.npIntersect(Model.BOUNDS_RIGHT).ifPresent(l::add);
        line.npIntersect(Model.BOUNDS_BOT).ifPresent(l::add);
        line.npIntersect(Model.BOUNDS_LEFT).ifPresent(l::add);
        return l;
    }

    /**
     * Returns single non-colinear intersection of line with bounds.
     */
    private static Optional<Point> boundsIntersect1(Line line) {
        Optional<Point> hit = line.npIntersect(Model.BOUNDS_TOP);
        if (hit.isEmpty()) {
            hit = line.npIntersect(Model.BOUNDS_RIGHT);
        }
        if (hit.isEmpty()) {
            hit = line.npIntersect(Model.BOUNDS_BOT);
        }
        if (hit.isEmpty()) {
            hit = line.npIntersect(Model.BOUNDS_LEFT);
        }
        return hit;
    }

    /**
     * Make an identical deep copy.
     */
    public Model deepClone() {
        return new Model(this.name, this.polylines, this.points);
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

    @Override
    public Optional<Point> closest(Point other) {
        return Stream.concat(polylines.stream().flatMap(polyline -> isoPoints()), isoPoints()).min(other.dist2Point());
    }

    public Model coloured(final Rgb c) {
        List<Polyline> polylines = this.polylines.stream().map(pl -> pl.colored(c)).collect(Collectors.<Polyline>toList());
        List<Point> allPoints = isoPoints().map(p -> p.colored(c)).collect(Collectors.toList());
        return new Model(this.name, polylines, allPoints);
    }

    @Override
    public Stream<Rgb> colours() {
        return Stream.concat(isoPoints().map(Point::getColor), lines().flatMap(Line::colours));
    }

    /**
     * Crops the model at the boundary of the given box.
     *
     * @param bounds the containing box.
     * @return the cropped model.
     */
    public Model crop(Box bounds) {
        List<Point> inPoints = points.stream().filter(Point::inBounds).toList();
        List<Line> newLines = new ArrayList<>();

        // TODO test this https://github.com/christo/vectorbrat/issues/33

        lines().forEach(line -> {
            // check the line for intersections with the bounds
            Point pFrom = line.from();
            Point pTo = line.to();
            if (!pFrom.inBounds() && !pTo.inBounds()) {
                // both ends of line are out of bounds
                // it's possible the line has a segment that is in bounds
                List<Point> newPoints = boundsIntersect2(line);
                if (newPoints.size() == 2) {
                    newLines.add(new Line(newPoints.get(0), newPoints.get(1)));
                }
            } else if (!pFrom.inBounds()) {
                // only from is out of bounds
                Point newFrom = boundsIntersect1(line).orElseThrow();
                newLines.add(new Line(newFrom, pTo));
            } else if (!pTo.inBounds()) {
                // only to is out of bounds
                Point newTo = boundsIntersect1(line).orElseThrow();
                newLines.add(new Line(pFrom, newTo));
            } else {
                // both end points are in bounds, therefore whole line is in bounds
                newLines.add(line);
            }
        });

        return new Model(name, newLines.stream().map(Polyline::fromLine).toList(), inPoints);
    }

    /**
     * Crops the model to the range.
     *
     * @return cropped Model
     */
    public Model crop() {
        return crop(Model.BOUNDS);
    }

    public int countLines() {
        return lines().toList().size();
    }

    /**
     * Returns a new {@link Model} derived from this one with colours modified by the given colour and
     * blend mode.
     *
     * @return new {@link Model}
     */
    public Model blend(final Function<Rgb, Rgb> mode) {
        List<Polyline> polylines = this.polylines.stream().map(pl -> pl.blend(mode)).collect(Collectors.<Polyline>toList());
        List<Point> allPoints = isoPoints().map(p -> p.blend(mode)).collect(Collectors.toList());
        return new Model(this.name, polylines, allPoints);
    }

    @Override
    public boolean inBounds() {
        return isoPoints().anyMatch(Point::inBounds) || lines().anyMatch(Line::inBounds);
    }

    @Override
    public boolean inBounds(float minX, float minY, float maxX, float maxY) {
        return isoPoints().anyMatch(p -> p.inBounds(minX, minY, maxX, maxY))
                || lines().anyMatch(line -> line.inBounds(minX, minY, maxX, maxY));
    }

    @Override
    public boolean inBounds(Box bounds) {
        return inBounds(bounds.minMin.x(), bounds.minMin.y(), bounds.maxMax.x(), bounds.maxMax.y());
    }

    public Model offset(float dx, float dy) {
        List<Polyline> polylines = this.polylines.stream().map(pl -> pl.offset(dx, dy)).collect(Collectors.<Polyline>toList());
        List<Point> allPoints = isoPoints().map(p -> p.offset(dx, dy)).collect(Collectors.toList());
        return new Model(this.name, polylines, allPoints);
    }

    public Model offset(Vec2 v) {
        List<Polyline> polylines = this.polylines.stream().map(pl -> pl.offset((float) v.x(), (float) v.y())).collect(Collectors.<Polyline>toList());
        List<Point> allPoints = isoPoints().map(p -> p.offset((float) v.x(), (float) v.y())).collect(Collectors.toList());
        return new Model(this.name, polylines, allPoints);
    }

    public Model scale(float factorX, float factorY) {
        List<Polyline> polylines = this.polylines.stream().map(polyline -> polyline.scale(factorX, factorY)).collect(Collectors.<Polyline>toList());
        List<Point> allPoints = isoPoints().map(point -> point.scale(factorX, factorY)).collect(Collectors.toList());
        return new Model(this.name, polylines, allPoints);
    }

    @Override
    public Model toModel() {
        return this;
    }
}
