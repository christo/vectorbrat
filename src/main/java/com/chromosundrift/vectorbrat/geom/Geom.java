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

    /**
     * Is some part of the Geom inside the normal bounds?
     *
     * @return iff some part is in bounds.
     */
    boolean inBounds();

    /**
     * Is any part of this is withiin the bounds defined by the given ranges?
     *
     * @param minX minimum x
     * @param minY minimum y
     * @param maxX maximum x
     * @param maxY maximum y
     * @return true iff any part of this is in bounds.
     */
    boolean inBounds(float minX, float minY, float maxX, float maxY);

    /**
     * Is any part of this in the given bounds?
     *
     * @param bounds the bounds.
     * @return true iff some part is in bounds.
     */
    boolean inBounds(Box bounds);

    /**
     * Get the isolated points in the model.
     *
     * @return points, does not include any geometric content with connected lines.
     */
    Stream<Point> isoPoints();

    Optional<Box> bounds();

    public Model toModel();
}
