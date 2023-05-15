package com.chromosundrift.vectorbrat.geom;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the trace path for rendering shapes.
 * future: implement best-effort scan order.
 */
public final class PathPlanner {

    /**
     * Initial size of path in total points including interpolation. Big enough to reduce allocations
     * during time-sensitive loop.
     */
    public static final int INITIAL_CAPACITY = 1000;

    /**
     * Number of to replicated path points per given isolated point.
     */
    private final float pointsPerPoint;

    /**
     * The linear density of interpolation points along a unit line.
     */
    private final float pointsPerUnit;

    // future consider preallocating arrays big enough via configured maximum

    private ArrayList<Float> xs = new ArrayList<>(INITIAL_CAPACITY);
    private ArrayList<Float> ys = new ArrayList<>(INITIAL_CAPACITY);
    private ArrayList<Float> rs = new ArrayList<>(INITIAL_CAPACITY);
    private ArrayList<Float> gs = new ArrayList<>(INITIAL_CAPACITY);
    private ArrayList<Float> bs = new ArrayList<>(INITIAL_CAPACITY);

    /**
     * Constructs with
     *
     * @param pointsPerPoint       number of render points to have per model point
     * @param pointsPerUnit number of intermediate points per model unit.
     */
    public PathPlanner(float pointsPerPoint, float pointsPerUnit) {
        this.pointsPerPoint = pointsPerPoint;
        this.pointsPerUnit = pointsPerUnit;
    }

    /**
     * Fill out the model with interpolated intermediate path points based on the scanning speed in units per second.
     * The path must be a loop with interpolation from the last point to the first, including black steps between
     * gaps.
     *
     * @param m the model to plan.
     * @param start the start point, not included in the planned path.
     */
    public void plan(Model m, Point start) {
        // future: add corner dwell param, maybe as function of angle

        // generate intermediate points along the course of the path to draw the model

        Point prev = new Point(start);

        List<Polygon> polygons = m._polygons();
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            // for polygons, use the colour of the whole polygon
            float[] rgb = polygon.getColor().getRGBComponents(null);

            Point[] points = polygon._points();
            for (Point next : points) {
                // interpolate points along line segment
                interpolate(prev, next);
                prev = next;
            }
            // TODO end of polygon, go to black for interconection to next Polygon
            prev = prev.black();
        }

        // return to the start point in black
        interpolate(prev.black(), start);


        if (xs.size() != ys.size() && xs.size() != rs.size() && xs.size() != gs.size() && xs.size() != bs.size()) {
            throw new IllegalStateException("BUG! all internal lists should be the same size");
        }
    }

    void interpolate(Point source, Point target) {
        interpolate(source, target, (int) (source.dist(target) * pointsPerUnit));
    }

    /**
     * Adds nPoints along the line from source to target plus the target point. Interpolated points are the same colour
     * as the source, end point is its own colour.
     *
     * @param source the origin point along the line (not added to the path)
     * @param target the destination point along the line (explicitly added).
     * @param n the number of interpolation points.
     */
    void interpolate(Point source, Point target, float n) {
        float prevx = source.x();
        float prevy = source.y();
        float prevr = source.r();
        float prevg = source.g();
        float prevb = source.b();

        final float targetX = target.x();
        final float targetY = target.y();

        final float dx = (targetX - prevx) / n;
        final float dy = (targetY - prevy) / n;

        // intepolate n intermediate points
        for (int i = 0; i < n; i++) {
            prevx += dx;
            prevy += dy;
            xs.add(prevx);
            ys.add(prevy);
            rs.add(prevr);
            gs.add(prevg);
            bs.add(prevb);
        }
        // now add the end point
        xs.add(targetX);
        ys.add(targetY);
        rs.add(target.r());
        gs.add(target.g());
        bs.add(target.b());
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
