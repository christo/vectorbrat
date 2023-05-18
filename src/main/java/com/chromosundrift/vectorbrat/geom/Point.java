package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable float-precision point.
 */
public final class Point implements Geom {
    private final float x;
    private final float y;
    private final float r;
    private final float g;
    private final float b;
    private final Color color;

    /**
     * Constructs a new point with given x and y coordinates and red, green, blue color components.
     */
    public Point(float x, float y, float r, float g, float b) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.g = g;
        this.b = b;
        this.color = new Color(r, g, b);
    }

    /**
     * Returns a white point at the given location.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     */
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

    public Color getColor() {
        return this.color;
    }

    /**
     * Returns the distance to the other point. See {@link #dist2(Point)}.
     *
     * @param other the other point.
     * @return the distance.
     */
    public float dist(Point other) {
        // pythagoras
        return (float) Math.sqrt(dist2(other));
    }

    /**
     * Returns the square of the distance to the other point. See {@link #dist(Point)}.
     *
     * @param other the other point.
     * @return the square of the distance.
     */
    public float dist2(Point other) {
        final float xx = other.x - x;
        final float yy = other.y - y;
        return xx * xx + yy * yy;
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
        return "Point[" + x + "," + y + " rgb(" + r + "," + g + "," + b + ")]";
    }

    /**
     * Returns a new black point at the same location.
     *
     * @return a black copy of this.
     */
    public Point black() {
        return this.colored(Color.BLACK);
    }

    /**
     * Actually uses the square of the distance.
     *
     * @return a comparator that orders by distance to this point.
     */
    public Comparator<Point> dist2Point() {
        return (o1, o2) -> Float.compare(dist2(o1), dist2(o2));
    }


    /**
     * Returns a comparator that orders lines by which one's end point is closest to this.
     *
     * @return a {@link Comparator}
     */
    public Comparator<Line> minDist2End() {
        return (l1, l2) -> {
            float d2l1 = Math.min(dist2(l1.from()), dist2(l1.to()));
            float d2l2 = Math.min(dist2(l2.from()), dist2(l2.to()));
            return Float.compare(d2l1, d2l2);
        };
    }

    /**
     * Creates a new point with the given colour.
     */
    public Point colored(Color color) {
        return new Point(this.x, this.y, color);
    }

    /**
     * Returns a new point in a space scaled by the given factor.
     *
     * @param factorX scaling factor in x axis.
     * @param factorY scaling factor in y axis.
     * @return the new point.
     */
    public Point scale(float factorX, float factorY) {
        return new Point(x * factorX, y * factorY, r, g, b);
    }

    public Point offset(float dx, float dy) {
        return new Point(x + dx, y + dy, this.color);
    }

    @Override
    public Optional<Point> closest(Point other) {
        return Optional.of(this);
    }

    static class PointFactory {
        private final Color color;

        public PointFactory(Color color) {
            this.color = color;
        }

        Point p(float x, float y) {
            return new Point(x, y, color);
        }
    }
}
