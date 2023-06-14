package com.chromosundrift.vectorbrat.asteroids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.geom.AsteroidsFont;
import com.chromosundrift.vectorbrat.geom.BungeeAnimator;
import com.chromosundrift.vectorbrat.geom.Composer;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.ModelAnimator;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Polyline;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.geom.TextEngine;

public final class Asteroids implements ModelAnimator {
    public static final float MIN_X = -1.0f;
    public static final float MAX_X = 1.0f;
    public static final float MIN_Y = -1.0f;
    public static final float MAX_Y = 1.0f;
    public static final Rgb COL_ASTEROID = Rgb.ORANGE;
    public static final int NUM_ASTEROIDS = 5;
    public static final String NAME = "ASTEROIDS";

    private static final int TARGET_FPS = 30;

    /**
     * Calculate how many nanoseconds per frame so animation skips frames instead of changing speed.
     */
    static final double NS_PER_FRAME = 1.0 * Util.BILLION / TARGET_FPS;

    /**
     * Calculate screen edge MARGIN big enough to contain a large Asteroid to disappear off the edge until we can split
     * them.
     */
    static final float MARGIN = Asteroid.Size.LARGE.units / 1.5f;

    private final Composer game;
    private final Random random = new Random(1234L); // fixed seed to make successive profiling runs comparable

    public Asteroids() {
        ParticleAnimator pa = new ParticleAnimator(0L, Rgb.MAGENTA, 200);
        game = new Composer(NAME, List.of(new RockAnimator(), mkTitle(), pa));
    }

    private ModelAnimator mkTitle() {
        TextEngine te = new TextEngine(Rgb.CYAN, new AsteroidsFont());
        float yScale = (float) (0.5 / NAME.length());
        Model textModel = te.textLine(NAME).scale(0.25f, yScale);
        return new BungeeAnimator(textModel, 5000, 1.55f, 1.6f);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void start() {
        game.start();
    }

    @Override
    public void stop() {
        game.stop();
    }

    @Override
    public Model update(long nsTime) throws VectorBratException {
        return game.update(nsTime);
    }

    private float randomVel() {
        return random.nextFloat(-0.01f, 0.01f);
    }

    private float randomX() {
        return random.nextFloat(MIN_X, MAX_X);
    }

    private static final class Particle {

        public Particle(float x, float y, float dx, float dy, long lastNanos) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.nsPrev = lastNanos;
        }

        private float x;
        private float y;
        private final float dx;
        private final float dy;
        private long nsPrev;

        public void update(long nsTime) {
            if (nsPrev >= 0) {
                // calculate the number of frames elapsed
                long nsElapsed = nsTime - nsPrev;
                float frames = (float) (nsElapsed / NS_PER_FRAME);

                // update the model params proportional to number of frames elapsed
                x += dx * frames;
                y += dy * frames;
                if (x < MIN_X - MARGIN) {
                    x = MAX_X + MARGIN;
                }
                if (x > MAX_X + MARGIN) {
                    x = MIN_X - MARGIN;
                }
                if (y < MIN_Y - MARGIN) {
                    y = MAX_Y + MARGIN;
                }
                if (y > MAX_Y + MARGIN) {
                    y = MIN_Y - MARGIN;
                }
            }
            nsPrev = nsTime;
        }
    }

    private class ParticleAnimator implements ModelAnimator {
        private final long lastNanos;
        private final Rgb colour;
        private final int count;
        private LinkedList<Particle> particles;

        public ParticleAnimator(long lastNanos, Rgb colour, int count) {
            this.lastNanos = lastNanos;
            this.colour = colour;
            this.count = count;
        }

        @Override
        public String getName() {
            return "bullets";
        }

        @Override
        public void start() {
            particles = new LinkedList<>();
            for (int i = 0; i < count; i++) {
                particles.add(new Particle(randomX(), randomX(), randomVel(), randomVel(), lastNanos));
            }
        }

        @Override
        public void stop() {
            this.particles = null;
        }

        @Override
        public Model update(long nsTime) throws VectorBratException {
            List<Point> points = new ArrayList<>();
            for (Particle particle : particles) {
                particle.update(nsTime);
                points.add(new Point(particle.x, particle.y, colour));
            }
            return new Model(getName(), Collections.emptyList(), points);
        }
    }

    /**
     * Handles the floating rocks part of the game.
     */
    private class RockAnimator implements ModelAnimator {
        private LinkedList<Asteroid> asteroids = new LinkedList<>();

        @Override
        public String getName() {
            return "rocks";
        }

        @Override
        public void start() {
            asteroids = new LinkedList<>();
            for (int i = 0; i < NUM_ASTEROIDS; i++) {
                asteroids.add(new Asteroid(Asteroid.Size.LARGE, random));
            }

        }

        @Override
        public void stop() {
            asteroids = null;
        }

        @Override
        public Model update(long nsTime) {
            List<Polyline> polyLines = new ArrayList<>();
            for (Asteroid asteroid : asteroids) {
                asteroid.update(nsTime);
                polyLines.add(asteroid.toPolyline());
            }
            return new Model(getName(), polyLines);
        }
    }
}
