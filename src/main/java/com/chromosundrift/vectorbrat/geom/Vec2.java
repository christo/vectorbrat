package com.chromosundrift.vectorbrat.geom;

/**
 * 2D float vector.
 **/
public record Vec2(float x, float y) {
    public Vec2(Point p) {
        this(p.x(), p.y());
    }
}
