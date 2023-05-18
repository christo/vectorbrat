package com.chromosundrift.vectorbrat.geom;

import java.util.Optional;

/**
 * A coloured line.
 *
 * @param from start point.
 * @param to   end point.
 */
public record Line(Point from, Point to) implements Geom {

    /**
     * Returns one of our two points closest to the other point.
     *
     * @param other reference.
     * @return to or from, whichever is closest to other.
     */
    public Optional<Point> closest(Point other) {
        return Optional.of(other.dist2(from) < other.dist2(to) ? from : to);
    }

    /**
     * Creates a new line in the reverse direction.
     *
     * @return a reversed line.
     */
    public Line reversed() {
        return new Line(to, from);
    }
}
