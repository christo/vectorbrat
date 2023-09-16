package com.chromosundrift.vectorbrat.geom;

/**
 * 2D float vector.
 **/
public record Vec2(float x, float y) {
    public static final Vec2 ZERO = new Vec2(0f, 0f);

    public Vec2(Point p) {
        this(p.x(), p.y());
    }
}
