package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An immutable rectangle with ordered points.
 */
public final class Box implements Geom {
    public final Point minMin;
    public final Point maxMax;
    public final Point minMax;
    public final Point maxMin;

    /**
     * @param corner   one corner.
     * @param opposite diagonally opposite point.
     */
    public Box(Point corner, Point opposite) {
        if (corner.x() <= opposite.x() && corner.y() <= opposite.y()) {
            // corner is minMin, opposite is maxMax
            minMin = corner;
            maxMax = opposite;
            minMax = new Point(corner.x(), opposite.y());
            maxMin = new Point(opposite.x(), corner.y());
        } else if (opposite.x() <= corner.x() && opposite.y() <= corner.y()) {
            // corner is maxMax, opposite is minMin
            minMin = opposite;
            maxMax = corner;
            minMax = new Point(opposite.x(), corner.y());
            maxMin = new Point(corner.x(), opposite.y());
        } else if (corner.x() <= opposite.x() && opposite.y() <= corner.x()) {
            // corner is minMax, opposite is maxMin
            minMax = corner;
            maxMin = opposite;
            minMin = new Point(corner.x(), opposite.y());
            maxMax = new Point(opposite.x(), corner.y());
        } else {
            // must be corner is maxMin, opposite is minMax
            maxMin = corner;
            minMax = opposite;
            minMin = new Point(opposite.x(), corner.y());
            maxMax = new Point(corner.x(), opposite.y());
        }
    }

    public Box(float p1x, float p1y, float p2x, float p2y) {
        this(new Point(p1x, p1y), new Point(p2x, p2y));
    }

    @Override
    public Optional<Point> closest(Point other) {
        return Stream.of(minMin, maxMax, minMax, maxMin).min(other.dist2Point());
    }

    public Polyline toPolyline(String name, Color color) {
        return Polyline.closed(name, color, minMin, minMax, maxMax, maxMin);
    }

    public Box scale(float xScale, float yScale) {
        return new Box(minMin.scale(xScale, yScale), maxMax.scale(xScale, yScale));
    }

    public Box offset(float offsetX, float offsetY) {
        return new Box(minMin.offset(offsetX, offsetY), maxMax.offset(offsetX, offsetY));
    }

    @Override
    public Stream<Line> lines() {
        return toPolyline("unused", Color.WHITE).lines();    // TODO name and colour not supported by box
    }
}
