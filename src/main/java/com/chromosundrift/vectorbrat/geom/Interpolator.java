package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.chromosundrift.vectorbrat.Config;

/**
 * Interpolating Pather. Holds the trace path for rendering shapes for renderers with physical acceleration limits.
 *
 */
public final class Interpolator implements Pather {

    /**
     * Initial size of path in total points including interpolation. Big enough to reduce allocations
     * during time-sensitive loop.
     */
    public static final int INITIAL_CAPACITY = 5000;

    /**
     * Number of to replicated path points per given isolated point.
     */
    private final float pointsPerPoint;

    /**
     * The average density of interpolation points along a unit line.
     */
    private final float pointsPerUnit;
    private final float vertexPoints;
    private final Interpolation interpolation;

    private final ArrayList<Float> xs = new ArrayList<>(INITIAL_CAPACITY);
    private final ArrayList<Float> ys = new ArrayList<>(INITIAL_CAPACITY);
    private final ArrayList<Float> rs = new ArrayList<>(INITIAL_CAPACITY);
    private final ArrayList<Float> gs = new ArrayList<>(INITIAL_CAPACITY);
    private final ArrayList<Float> bs = new ArrayList<>(INITIAL_CAPACITY);

    /** Base value */
    private final float pointsPerUnitOffset;

    /**
     * @param config configuration.
     */
    public Interpolator(Config config) {
        this.pointsPerPoint = config.getPointsPerPoint();
        this.pointsPerUnit = config.getPointsPerUnit();
        this.vertexPoints = config.getVertexPoints();
        this.interpolation = config.getInterpolation();
        this.pointsPerUnitOffset = config.getPointsPerUnitOffset();
    }

    /**
     * Input domain 0-1
     *
     * @param x input value
     * @return quintic ease in and out
     */
    public static float quintic(float x) {
        return (float) (x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2);
    }

    /**
     * Input domain 0-1
     *
     * @param x input value
     * @return quintic ease in and out
     */
    public static double quintic(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

    /**
     * Plan the path based on next nearest unvisited model point. Polylines may be rendered in parts. Fill out the
     * model with interpolated intermediate path points based on the scanning speed in units per second. The path will
     * be constructed as a loop with interpolation from the last point to the first, including black steps between gaps.
     *
     * @param m the model to plan.
     */
    @Override
    public void plan(Model m) {

        LinkedList<Line> lines = new LinkedList<>();
        for (Polyline pl : m.polylines().toList()) {
            lines.addAll(pl.lineList());
        }
        List<Point> points = new LinkedList<>(m.points().toList());
        Point prev;
        if (!lines.isEmpty()) {
            prev = lines.get(0).from();
        } else if (!points.isEmpty()) {
            prev = points.get(0);
        } else {
            // model is empty, just draw origin with pen up
            prev = new Point(0, 0, Color.BLACK);
        }
        while (!lines.isEmpty() || !points.isEmpty()) {
            // if a line is closest, this will contain it, otherwise null
            Line closestLine = null;
            // if a point is closest, this contains it, otherwise null
            Point closestPoint = null;
            // true if a line is closest by its "to" point rather than its "from" point, thus must be reversed in plan
            boolean reverseLine = false;
            float closestD2 = Float.MAX_VALUE;
            // using square distance throughout
            // do lines first, spidey guess for average case
            int s = lines.size();
            for (int i = 0; i < s; i++) {
                Line line = lines.get(i);
                float fromDist = line.from().dist2(prev);
                float toDist = line.to().dist2(prev);
                if (fromDist < closestD2) {
                    closestD2 = fromDist;
                    closestLine = line;
                    reverseLine = false;
                }
                if (toDist < closestD2) {
                    closestD2 = toDist;
                    closestLine = line;
                    reverseLine = true;
                }
                if (closestD2 == 0f) {
                    break;
                }
            }
            // got the closest line

            // find point closest to prev point
            s = points.size();
            for (int i = 0; i < s; i++) {
                Point point = points.get(i);
                float dist = point.dist2(prev);
                if (dist < closestD2) {
                    closestD2 = dist;
                    closestLine = null;
                    closestPoint = point;
                }
                if (closestD2 == 0f) {
                    break;
                }
            }
            if (closestLine == null && closestPoint == null) {
                throw new IllegalStateException("no line or point closest! model:%s".formatted(m));
            }
            // now add the line or point to the path plan
            if (closestLine != null) {
                // remove the line from the lines list and add it to the path plan
                lines.remove(closestLine);
                if (reverseLine) {
                    closestLine = closestLine.reversed();
                }
                // if point is not the same as prev, interpolate to it first
                if (!prev.equals(closestLine.from())) {
                    penUp(pointsPerPoint);
                    interpolate(prev.black(), closestLine.from());
                }
                // interpolate the line
                interpolate(closestLine.from(), closestLine.to());
                prev = closestLine.to();
            } else {
                // remove from the points list and add the point to the path plan
                points.remove(closestPoint);
                // interpolate to the new point, dwelling on arrival
                penUp(pointsPerPoint);
                interpolate(prev.black(), closestPoint, pointsPerPoint);
                prev = closestPoint;
            }
        }
        // now interpolate back to the beginning
        penUp(pointsPerPoint);
        interpolate(prev.black(), new Point(xs.get(0), ys.get(0)));
        if (xs.size() != ys.size() || xs.size() != rs.size() || xs.size() != gs.size() || xs.size() != bs.size()) {
            throw new IllegalStateException("BUG! all internal lists should be the same size");
        }
    }

    /**
     * Fill out the model with interpolated intermediate path points based on the scanning speed in units per second.
     * The path will be constructed as a loop with interpolation from the last point to the first, including black
     * steps between gaps.
     *
     * @param m     the model to plan.
     * @param start the start point, will draw the model from its closest point to this.
     */
    public void planNaive(Model m, Point start) {
        // generate intermediate points along the course of the path to draw the model
        Point prev = m.closeish(start);

        List<Polyline> polylines = m._polylines();
        for (int i = 0; i < polylines.size(); i++) {
            Polyline polyline = polylines.get(i);
            Point[] points = polyline._points();
            for (Point next : points) {
                // interpolate points along line segment
                interpolate(prev, next);
                prev = next;
            }
            // end of polyline, go to black for interconection to next Polyline
            prev = prev.black();
            penUp(pointsPerPoint);
        }

        // now plan points
        List<Point> points = m._points();
        for (Point point : points) {
            // interpolate black path points to the point
            penUp(pointsPerPoint);
            interpolate(prev.black(), point, pointsPerPoint);
            prev = point;
        }

        // return to the start point in black
        penUp(pointsPerPoint);
        interpolate(prev.black(), start); // BUG: we always dwell assuming start was a vertex

        if (xs.size() != ys.size() || xs.size() != rs.size() || xs.size() != gs.size() || xs.size() != bs.size()) {
            throw new IllegalStateException("BUG! all internal lists should be the same size");
        }
    }

    void interpolate(Point source, Point target) {
        interpolate(source, target, vertexPoints);
    }

    /**
     * Add a number of interpolated points (derived from pointsPerUnit and the distance) along the line
     * from source to target and dwell at the target with vertexPoints extra points.
     */
    void interpolate(Point source, Point target, float vertexPoints) {
        interpolate(source, target, vertexPoints, (int) (source.dist(target) * pointsPerUnit + pointsPerUnitOffset));
    }

    /**
     * Adds n Points along the line from source to target plus the target point. Interpolated points are the same colour
     * as the source, end point is its own colour.
     *
     * @param source the origin point along the line (not added to the path)
     * @param target the destination point along the line (explicitly added).
     * @param n      the number of interpolation points.
     */
    void interpolate(Point source, Point target, float vertexPoints, float n) {
        float prevx = source.x();
        float prevy = source.y();
        float prevr = source.r();
        float prevg = source.g();
        float prevb = source.b();

        final float targetX = target.x();
        final float targetY = target.y();
        final float targetR = target.r();
        final float targetG = target.g();
        final float targetB = target.b();

        float sx = prevx;
        float sy = prevy;
        float xDist = targetX - prevx;
        float yDist = targetY - prevy;

        // intepolate n intermediate points
        for (int i = 0; i < n; i++) {
            if (interpolation == Interpolation.LINEAR) {
                prevx = sx + i * xDist / n;
                prevy = sy + i * yDist / n;
            } else if (interpolation == Interpolation.QUINTIC) {
                prevx = sx + quintic(i / n) * xDist;
                prevy = sy + quintic(i / n) * yDist;
            }
            xs.add(prevx);
            ys.add(prevy);
            rs.add(prevr);
            gs.add(prevg);
            bs.add(prevb);
        }
        // now add the end points in the end point colour
        for (int i = 0; i < vertexPoints; i++) {
            xs.add(targetX);
            ys.add(targetY);
            rs.add(targetR);
            gs.add(targetG);
            bs.add(targetB);
        }
    }

    void penUp(float n) {
        int last = xs.size() - 1;
        if (last >= 0) {
            for (int i = 0; i < vertexPoints; i++) {
                xs.add(xs.get(last));
                ys.add(ys.get(last));
                rs.add(0f);
                gs.add(0f);
                bs.add(0f);
            }
        }
    }

    @Override
    public ArrayList<Float> getXs() {
        return xs;
    }

    @Override
    public ArrayList<Float> getYs() {
        return ys;
    }

    @Override
    public ArrayList<Float> getRs() {
        return rs;
    }

    @Override
    public ArrayList<Float> getGs() {
        return gs;
    }

    @Override
    public ArrayList<Float> getBs() {
        return bs;
    }
}
