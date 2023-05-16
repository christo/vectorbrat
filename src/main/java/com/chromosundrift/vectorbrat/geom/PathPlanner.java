package com.chromosundrift.vectorbrat.geom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import com.chromosundrift.vectorbrat.Util;

/**
 * Holds the trace path for rendering shapes.
 * future: implement best-effort scan order.
 */
public final class PathPlanner {

    /**
     * Initial size of path in total points including interpolation. Big enough to reduce allocations
     * during time-sensitive loop.
     */
    public static final int INITIAL_CAPACITY = 10000;

    /**
     * Number of to replicated path points per given isolated point.
     */
    private final float pointsPerPoint;

    /**
     * The linear density of interpolation points along a unit line.
     */
    private final float pointsPerUnit;
    private final float vertexPoints;
    private final Interpolation interpolation;

    // future consider preallocating arrays big enough via configured maximum

    private final ArrayList<Float> xs = new ArrayList<>(INITIAL_CAPACITY);
    private final ArrayList<Float> ys = new ArrayList<>(INITIAL_CAPACITY);
    private final ArrayList<Float> rs = new ArrayList<>(INITIAL_CAPACITY);
    private final ArrayList<Float> gs = new ArrayList<>(INITIAL_CAPACITY);
    private final ArrayList<Float> bs = new ArrayList<>(INITIAL_CAPACITY);

    /**
     * @param pointsPerPoint number of render points to have per model point.
     * @param pointsPerUnit  number of intermediate points per model unit.
     * @param vertexPoints   number of render points to have per polyline vertex.
     */
    public PathPlanner(float pointsPerPoint, float pointsPerUnit, float vertexPoints, Interpolation interpolation) {
        this.pointsPerPoint = pointsPerPoint;
        this.pointsPerUnit = pointsPerUnit;
        this.vertexPoints = vertexPoints;
        this.interpolation = interpolation;
    }

    /**
     * Fill out the model with interpolated intermediate path points based on the scanning speed in units per second.
     * The path will be constructed as a loop with interpolation from the last point to the first, including black
     * steps between gaps.
     *
     * @param m     the model to plan.
     * @param start the start point, will draw the model from the closest point to this.
     */
    public void plan(Model m, Point start) {
        // future: add corner dwell param, maybe as function of angle

        // generate intermediate points along the course of the path to draw the model

        Point prev = m.closestTo(start);

        List<Polyline> polylines = m._polygons();
        for (int i = 0; i < polylines.size(); i++) {
            Polyline polyline = polylines.get(i);
            Point[] points = polyline._points();
            for (Point next : points) {
                // interpolate points along line segment
                interpolate(prev, next);
                prev = next;
            }
            // end of polygon, go to black for interconection to next Polygon
            prev = prev.black();
        }

        // now plan points
        List<Point> points = m._points();
        for (Point point : points) {
            // interpolate black path points to the point
            interpolate(prev.black(), point);
            float x = point.x();
            float y = point.y();
            float r = point.r();
            float g = point.g();
            float b = point.b();
            for (int i = 0; i < pointsPerPoint; i++) {
                xs.add(x);
                ys.add(y);
                rs.add(r);
                gs.add(g);
                bs.add(b);
            }
            prev = point;
        }

        // return to the start point in black
        interpolate(prev.black(), start);


        if (xs.size() != ys.size() && xs.size() != rs.size() && xs.size() != gs.size() && xs.size() != bs.size()) {
            throw new IllegalStateException("BUG! all internal lists should be the same size");
        }
    }

    void interpolate(Point source, Point target) {
        interpolate(source, target, (int) (source.dist(target) * pointsPerUnit), vertexPoints);
    }

    /**
     * Adds nPoints along the line from source to target plus the target point. Interpolated points are the same colour
     * as the source, end point is its own colour.
     *
     * @param source the origin point along the line (not added to the path)
     * @param target the destination point along the line (explicitly added).
     * @param n      the number of interpolation points.
     */
    void interpolate(Point source, Point target, float n, float vertexPoints) {
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
                prevx = sx + Util.quintic(i / n) * xDist;
                prevy = sy + Util.quintic(i / n) * yDist;
            }
            xs.add(prevx);
            ys.add(prevy);
            rs.add(prevr);
            gs.add(prevg);
            bs.add(prevb);
        }
        // now add the end points
        for (int i = 0; i < vertexPoints; i++) {
            xs.add(targetX);
            ys.add(targetY);
            rs.add(targetR);
            gs.add(targetG);
            bs.add(targetB);
        }
    }

    public ArrayList<Float> getXs() {
        return xs;
    }

    public ArrayList<Float> getYs() {
        return ys;
    }

    public ArrayList<Float> getRs() {
        return rs;
    }

    public ArrayList<Float> getGs() {
        return gs;
    }

    public ArrayList<Float> getBs() {
        return bs;
    }
}
