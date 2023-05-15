package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.Comparator;
import java.util.Objects;

/**
 * Immutable float-precision point.
 */
public final class Point {
    private final float x;
    private final float y;
    private final float r;
    private final float g;
    private final float b;
    private final Color color;

    /**
     *
     */
    public Point(float x, float y, float r, float g, float b) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.g = g;
        this.b = b;
        this.color = new Color(r, g, b);
    }

    public Point(float x, float y) {
        this(x, y, 1f, 1f, 1f);
    }

    public Point(float x, float y, Color c) {
        float[] rgb = c.getRGBComponents(null);
        this.x = x;
        this.y = y;
        this.r = rgb[0];
        this.g = rgb[1];
        this.b = rgb[2];
        this.color = c;
    }

    public Point(Point copyMe) {
        this.x = copyMe.x;
        this.y = copyMe.y;
        this.r = copyMe.r;
        this.g = copyMe.g;
        this.b = copyMe.b;
        this.color = copyMe.color;
    }

    public Color color() {
        return this.color;
    }

    public float dist(Point other) {
        // pythagoras
        return (float) Math.sqrt(distSquared(other));
    }

    private float distSquared(Point other) {
        final float xx = other.x - x;
        final float yy = other.y - y;
        final float d2 = xx * xx + yy * yy;
        return d2;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float r() {
        return r;
    }

    public float g() {
        return g;
    }

    public float b() {
        return b;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Point) obj;
        return Float.floatToIntBits(this.x) == Float.floatToIntBits(that.x) &&
                Float.floatToIntBits(this.y) == Float.floatToIntBits(that.y) &&
                Float.floatToIntBits(this.r) == Float.floatToIntBits(that.r) &&
                Float.floatToIntBits(this.g) == Float.floatToIntBits(that.g) &&
                Float.floatToIntBits(this.b) == Float.floatToIntBits(that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, r, g, b);
    }

    @Override
    public String toString() {
        return "Point[" +
                "x=" + x + ", " +
                "y=" + y + ", " +
                "r=" + r + ", " +
                "g=" + g + ", " +
                "b=" + b + ']';
    }

    /**
     * Returns a new point at the same location which is black.
     *
     * @return a black copy of this.
     */
    public Point black() {
        return new Point(x, y, 0f, 0f, 0f);
    }

    public Comparator<Point> distToComparator() {
        return (o1, o2) -> (int) (this.distSquared(o1) - this.distSquared(o2));
    }
}
