package com.chromosundrift.vectorbrat.geom;

import java.util.stream.Stream;

/**
 * Base class that partly implements Geom with no isolated points.
 */
abstract public class Pointless implements Geom {
    /**
     * Has no isopoints.
     * @return empty stream.
     */
    public final Stream<Point> points() {
        return Stream.empty();
    }
}
