package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;

/**
 * A coloured line.
 *
 * @param from  start point.
 * @param to    end point.
 * @param color the color of the line.
 */
public record Line(Point from, Point to) {

    /**
     * Returns one of our two points closest to the other point.
     *
     * @param other reference.
     * @return to or from, whichever is closest to other.
     */
    public Point closest(Point other) {
        return other.dist2(from) < other.dist2(to) ? from : to;
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
