package com.chromosundrift.vectorbrat.geom;

import java.util.Optional;
import java.util.stream.Stream;

public interface Geom {

    /**
     * If the geometry is empty, return {@link Optional#empty()}.
     *
     * @param other comparison point.
     * @return the closeest point to other in our geometry or if empty, return other.
     */
    Optional<Point> closest(Point other);


    Stream<Line> lines();

    /**
     * Return a bounding box that minimally contains all geometric elements.
     * If there are no elements in the geometry, returns empty.
     *
     * @return the bounding box.
     */
    Optional<Box> bounds();
}
