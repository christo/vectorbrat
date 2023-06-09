package com.chromosundrift.vectorbrat.geom;

import java.util.Optional;
import java.util.stream.Stream;

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

    @Override
    public Stream<Line> lines() {
        return Stream.of(this);
    }

    @Override
    public Optional<Box> bounds() {
        return Optional.of(toBox());
    }

    public Box toBox() {
        return new Box(from, to);
    }

    public Line scale(float xScale, float yScale) {
        return new Line(from.scale(xScale, yScale), to.scale(xScale, yScale));
    }

    public Line offset(float offsetX, float offsetY) {
        return new Line(from.offset(offsetX, offsetY), to.offset(offsetX, offsetY));
    }

    /**
     * Scales then offsets.
     *
     * @param xS xScale
     * @param yS yScale
     * @param xO xOffset
     * @param yO yOffset
     * @return a new Line
     */
    public Line scaleOffset(float xS, float yS, float xO, float yO) {
        return new Line(from.scaleOffset(xS, yS, xO, yO), to.scaleOffset(xS, yS, xO, yO));
    }
}
