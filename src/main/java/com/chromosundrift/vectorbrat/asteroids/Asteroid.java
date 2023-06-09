package com.chromosundrift.vectorbrat.asteroids;

import java.util.List;
import java.util.Random;

import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Polyline;

/**
 * Geometric game object.
 */
public class Asteroid {

    private static final float MIN_DX = -0.005f;
    private static final float MIN_DY = -0.005f;
    private static final float MAX_DX = 0.005f;
    private static final float MAX_DY = 0.005f;
    private static final float MIN_DROT = (float) (Math.TAU / -20);
    private static final float MAX_DROT = (float) (Math.TAU / 20);
    private static final int TARGET_FPS = 30;
    private static final double NS_PER_FRAME = 1.0 * Util.BILLION / TARGET_FPS;
    private static final float MARGIN = Asteroid.Size.LARGE.units / 1.5f;

    private final Size size;
    private final float[] radii;
    private final float dx;
    private final float dy;
    private final float drot;
    private float rot;
    private float x;
    private float y;
    private long lastNanos = -1;

    public Asteroid(Size size) {
        this(size, new Random());
    }

    public Asteroid(Size size, Random r) {

        this.size = size;
        this.x = r.nextFloat(Asteroids.MIN_X, Asteroids.MAX_X);
        this.y = r.nextFloat(Asteroids.MIN_Y, Asteroids.MAX_Y);
        this.rot = 0;
        this.dx = r.nextFloat(Asteroid.MIN_DX, Asteroid.MAX_DX);
        this.dy = r.nextFloat(Asteroid.MIN_DY, Asteroid.MAX_DY);
        this.drot = r.nextFloat(Asteroid.MIN_DROT, Asteroid.MAX_DROT) / size.sides;

        // create random points
        radii = new float[size.sides];
        for (int i = 0; i < size.sides; i++) {
            radii[i] = r.nextFloat(size.units / 2, size.units);
        }
    }

    public void update(long nsTime) {
        if (lastNanos >= 0) {
            // calculate the number of frames elapsed
            long nsElapsed = nsTime - lastNanos;
            float frames = (float) (nsElapsed / NS_PER_FRAME);

            // update the model params proportional to number of frames elapsed
            x += dx * frames;
            y += dy * frames;
            rot += drot * frames;
            if (x < Asteroids.MIN_X - MARGIN) {
                x = Asteroids.MAX_X + MARGIN;
            }
            if (x > Asteroids.MAX_X + MARGIN) {
                x = Asteroids.MIN_X - MARGIN;
            }
            if (y < Asteroids.MIN_Y - MARGIN) {
                y = Asteroids.MAX_Y + MARGIN;
            }
            if (y > Asteroids.MAX_Y + MARGIN) {
                y = Asteroids.MIN_Y - MARGIN;
            }
        }
        lastNanos = nsTime;
    }

    public Polyline toPolyline() {
        Point[] points = new Point[size.sides];
        // convert polar to cartesian
        for (int i = 0; i < size.sides; i++) {
            float theta = (float) (i * Math.TAU / size.sides) + this.rot;
            float lx = (float) (radii[i] * Math.cos(theta));
            float ly = (float) (radii[i] * Math.sin(theta));
            points[i] = new Point(x + lx, y + ly, Asteroids.COL_ASTEROID);
        }
        return Polyline.closed(size.name() + " asteroid", Asteroids.COL_ASTEROID, points);
    }

    public Model toModel() {
        Polyline polyline = toPolyline();
        return new Model(polyline.getName(), List.of(polyline));
    }

    public enum Size {
        SMALL(0.05f, 40, 8),
        MEDIUM(0.1f, 25, 10),
        LARGE(0.17f, 10, 12);

        private final float units;
        private final int score;
        private final int sides;

        Size(float units, int score, int sides) {
            this.units = units;
            this.score = score;
            this.sides = sides;
        }
    }
}
