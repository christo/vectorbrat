package com.chromosundrift.vectorbrat.geom;

import java.util.ArrayList;
import java.util.List;

/**
 * Path planner that renders sample points without interpolation of any kind. One primary use case is rendering
 * calibration ILDA files whose format spec states that they should be passed directly to a laser with no
 * interpretation or optimisation. For test tuning, this requirement is essential to maintain pessimality, even if some
 * ILDA files may be authored implicitly expecting optimisation to be added by display software.
 */
public class SimplePather implements Pather {

    private final ArrayList<Float> xs;
    private final ArrayList<Float> ys;
    private final ArrayList<Float> rs;
    private final ArrayList<Float> gs;
    private final ArrayList<Float> bs;

    /**
     * Construct paths from only points.
     *
     * @param points the points to use for the path.
     */
    public SimplePather(List<Point> points) {
        int size = points.size();
        xs = new ArrayList<>(size);
        ys = new ArrayList<>(size);
        rs = new ArrayList<>(size);
        gs = new ArrayList<>(size);
        bs = new ArrayList<>(size);
        for (Point point : points) {
            xs.add(point.x());
            ys.add(point.y());
            rs.add(point.r());
            gs.add(point.g());
            bs.add(point.b());
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
