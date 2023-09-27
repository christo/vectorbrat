package com.chromosundrift.vectorbrat.geom;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A line connecting two points.
 */
public final class Line extends Pointless implements Geom {
    private final Point from;
    private final Point to;

    /**
     * Non-parallel intersection. Calculate the intersection point of this line with the other line or empty
     * if they are parallel. If the lines are colinear and overlap, mathematically there are infinite intersection
     * points. In this case empty is also returned!
     *
     * @param other the other line.
     * @return maybe the intersection point.
     */
    public Optional<Point> npIntersect(Line other) {
        // intersection point using first degree bezier parameters
        // https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection
        float x1 = this.from.x();
        float y1 = this.from.y();
        float x2 = this.to.x();
        float y2 = this.to.y();
        float x3 = other.from.x();
        float y3 = other.from.y();
        float x4 = other.to.x();
        float y4 = other.to.y();
        // denominator
        float td = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (td == 0) {
            return Optional.empty();
        } else {
            // numerator
            float tn = (x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4);
            float t = tn / td;
            return Optional.of(new Point((x1 + t * (x2 - x1)), (y1 + t * (y2 - y1))));
        }
    }

    /**
     * @param from start point.
     * @param to   end point.
     */
    public Line(Point from, Point to) {
        this.from = from;
        this.to = to;
    }

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

    public Point from() {
        return from;
    }

    public Point to() {
        return to;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Line) obj;
        return Objects.equals(this.from, that.from) &&
                Objects.equals(this.to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "Line[" + from + " -> " + to + ']';
    }

    public Stream<Rgb> colours() {
        return Stream.of(from.getColor(), to.getColor());
    }

    @Override
    public boolean inBounds() {
        return from.inBounds() || to().inBounds();
    }

    @Override
    public boolean inBounds(float minX, float minY, float maxX, float maxY) {
        return from.inBounds(minX, minY, maxX, maxY);
    }

    @Override
    public boolean inBounds(Box bounds) {
        return inBounds(bounds.minMin.x(), bounds.minMin.y(), bounds.maxMax.x(), bounds.maxMax.y());
    }

    @Override
    public Model toModel() {
        return new Model("", List.of(Polyline.fromLine(this)), Collections.emptyList());
    }
}
