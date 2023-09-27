package com.chromosundrift.vectorbrat.geom;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base class that partly implements Geom with no isolated points.
 */
abstract public class Pointless implements Geom {

    /**
     * Has no isopoints.
     *
     * @return empty stream.
     */
    public final Stream<Point> isoPoints() {
        return Stream.empty();
    }

    /**
     * Convenience implementation that compares all points of all lines. This won't be the most efficient
     * for subclasses who know which points are shared in lines but it should be correct. Override if you
     * know better.
     *
     * @param other comparison point.
     * @return the closest point to other.
     */
    @Override
    public Optional<Point> closest(Point other) {
        // we know line always has a closest and if we are empty, min returns Empty
        return lines().map(line -> line.closest(other).get()).min(other.dist2Point());
    }
}
