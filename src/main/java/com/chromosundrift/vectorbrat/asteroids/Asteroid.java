package com.chromosundrift.vectorbrat.asteroids;

import java.util.Random;

import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Polyline;

/**
 * Geometric game object.
 */
public class Asteroid {

    private static final float MIN_DX = 0.01f;
    private static final float MIN_DY = 0.01f;
    private static final float MAX_DX = 0.1f;
    private static final float MAX_DY = 0.1f;
    private static final float MIN_DROT = (float) (Math.TAU / 10);
    private static final float MAX_DROT = (float) (Math.TAU / 5);
    private static final int TARGET_FPS = 60;
    private static final double frameTimeMs = 1.0 * Util.THOUSAND / TARGET_FPS;
    private final Size size;
    private final float[] radii;
    private final float dx;
    private final float dy;
    private final float drot;
    private float rot;
    private float x;
    private float y;
    private long lastUpdate = -1;


    public Asteroid(Size size) {
        Random r = new Random();
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

    public void update(long time) {
        if (lastUpdate < 0) {
            lastUpdate = time;
        } else {
            // calculate the number of frames elapsed
            double frames = time - lastUpdate * 1.0 / Util.MILLION;

            // update the model params proportional to number of frames elapsed
            x += dx * frames;
            y += dy * frames;
            rot += drot * frames;
            if (x < Asteroids.MIN_X - Size.LARGE.units) {
                x = Asteroids.MAX_X + Size.LARGE.units;
            }
            if (x > Asteroids.MAX_X + Size.LARGE.units) {
                x = Asteroids.MIN_X - Size.LARGE.units;
            }
            if (y < Asteroids.MIN_Y - Size.LARGE.units) {
                y = Asteroids.MAX_Y + Size.LARGE.units;
            }
            if (y > Asteroids.MAX_Y + Size.LARGE.units) {
                y = Asteroids.MIN_Y - Size.LARGE.units;
            }
        }
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


    enum Size {
        SMALL(0.1f, 40, 8),
        MEDIUM(0.2f, 25, 10),
        LARGE(0.3f, 10, 12);

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
