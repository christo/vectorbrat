package com.chromosundrift.vectorbrat.geom;

import com.chromosundrift.vectorbrat.data.Maths;

/**
 * 2D double vector.
 **/
public record Vec2(double x, double y) {
    public static final Vec2 ZERO = new Vec2(0.0, 0.0);

    public Vec2(Point p) {
        this(p.x(), p.y());
    }

    public Vec2 add(double x, double y) {
        return new Vec2(this.x + x, this.y + y);
    }

    /**
     * Returns a Vec2 with clamped absolute values of components compared to the given absMax.
     *
     * @param absMax the comparison absolute value.
     * @return a clamped Vec2, possibly this one.
     */
    public Vec2 absClamp(double absMax) {
        if (this.x() > absMax || this.x() < -absMax || this.y() > absMax || this.y() < -absMax) {
            return new Vec2(Maths.clamp(this.x(), -absMax, absMax), Maths.clamp(this.y(), -absMax, absMax));
        } else {
            return this;
        }
    }

}
