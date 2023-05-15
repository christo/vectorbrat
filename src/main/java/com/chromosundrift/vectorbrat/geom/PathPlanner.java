package com.chromosundrift.vectorbrat.geom;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the trace path for rendering shapes.
 * future: implement best-effort scan order.
 */
public class PathPlanner {

    // future consider preallocating arrays big enough via configured maximum

    private ArrayList<Float> xs = new ArrayList<>();
    private ArrayList<Float> ys = new ArrayList<>();
    private ArrayList<Float> rs = new ArrayList<>();
    private ArrayList<Float> gs = new ArrayList<>();
    private ArrayList<Float> bs = new ArrayList<>();

    /**
     * Fill out the model with interpolated intermediate path points based on the scanning speed in units per second.
     * The path must be a loop with interpolation from the last point to the first.
     *
     * @param m             the model.
     * @param nPoints       number of render points to have per model point
     * @param pointsPerUnit number of intermediate points per model unit.
     * @param start
     */
    public PathPlanner(Model m, float nPoints, float pointsPerUnit, Point start) {
        // future: add corner dwell param, maybe as function of angle

        // generate intermediate points along the course of the path to draw the model

        Point prev = new Point(start);

        float prevx = start.x();
        float prevy = start.y();
        float prevr = start.r();
        float prevg = start.g();
        float prevb = start.b();
        List<Polygon> polygons = m._polygons();
        for (Polygon polygon : polygons) {
            float[] rgb = polygon.getColor().getRGBComponents(null);
            prevr = rgb[0];
            prevg = rgb[1];
            prevb = rgb[2];


            Point[] points = polygon._points();
            for (Point next : points) {
                // calculate number of points along line segment
                int n = (int) (prev.dist(next) * pointsPerUnit);
                // calculate x and y steps per interpolated point
                float dx = (next.x() - prevx) / n;
                float dy = (next.y() - prevy) / n;

                // intepolate n intermediate points
                for (int i = 0; i < n; i++) {
                    xs.add(prevx);
                    ys.add(prevy);
                    rs.add(prevr);
                    gs.add(prevg);
                    bs.add(prevb);
                    prevx += dx;
                    prevy += dy;
                }
                prev = next;
            }
            // finished the polygon, change colour to black
            prevr = 0f;
            prevg = 0f;
            prevb = 0f;
        }

        // now do the same thing back to the start point

        int n = (int) (start.dist(prev) * pointsPerUnit);
        float dx = (start.x() - prevx) / n;
        float dy = (start.y() - prevy) / n;

        // intepolate n intermediate points
        for (int i = 0; i < n; i++) {
            xs.add(start.x());
            ys.add(start.y());
            rs.add(start.r());
            gs.add(start.g());
            bs.add(start.b());
            prevx += dx;
            prevy += dy;
        }

        //addPoints(m, nPoints);
        if (xs.size() != ys.size() && xs.size() != rs.size() && xs.size() != gs.size() && xs.size() != bs.size()) {
            throw new IllegalStateException("BUG! all internal lists should be the same size");
        }
    }

    private void addPoints(Model m, float nPoints) {
        // TODO add black points from prev to next point.

        // for isolated points, dwell on them for pointDwell seconds
        // by adding the same point nPoints times

        for (int i = 0; i < m._points().size(); i++) {
            Point next = m._points().get(i);

            float x = next.x();
            float y = next.y();
            float r = next.r();
            float g = next.g();
            float b = next.b();
            for (int j = 0; j < nPoints; j++) {
                xs.add(x);
                ys.add(y);
                rs.add(r);
                gs.add(g);
                bs.add(b);
            }
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
