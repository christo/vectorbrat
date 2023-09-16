package com.chromosundrift.vectorbrat.geom;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Abstract common interface for geometric elements.
 */
public interface Geom {

    /**
     * If the geometry is empty, return {@link Optional#empty()}.
     *
     * @param other comparison point.
     * @return the closest point to other in our geometry or if empty, return other.
     */
    Optional<Point> closest(Point other);


    /**
     * Gets all the (possibly zero) lines in this {@link Geom}.
     *
     * @return a stream of lines.
     */
    Stream<Line> lines();

    Stream<Rgb> colours();

    boolean inBounds();

    boolean inBounds(float minX, float minY, float maxX, float maxY);

    /**
     * Get the isolated points in the model.
     *
     * @return points, does not include any geometric content with connected lines.
     */
    Stream<Point> isoPoints();

    Optional<Box> bounds();

    public Model toModel();
}
