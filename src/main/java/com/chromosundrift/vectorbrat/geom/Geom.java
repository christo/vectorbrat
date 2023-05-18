package com.chromosundrift.vectorbrat.geom;

import java.util.Optional;

public interface Geom {

    /**
     * If the geometry is empty, return {@link Optional#empty()}.
     *
     * @param other comparison point.
     * @return the closeest point to other in our geometry or if empty, return other.
     */
    Optional<Point> closest(Point other);


}
