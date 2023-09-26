package com.chromosundrift.vectorbrat.asteroids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.chromosundrift.vectorbrat.anim.AbstractAnimator;
import com.chromosundrift.vectorbrat.data.Maths;
import com.chromosundrift.vectorbrat.physics.Clock;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.geom.AsteroidsFont;
import com.chromosundrift.vectorbrat.anim.BatchAnimator;
import com.chromosundrift.vectorbrat.anim.BungeeAnimator;
import com.chromosundrift.vectorbrat.anim.Composer;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.anim.ModelAnimator;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.geom.TextEngine;
import com.chromosundrift.vectorbrat.geom.Updater;

public final class Asteroids extends AbstractAnimator {
    public static final float MIN_X = -1.0f;
    public static final float MAX_X = 1.0f;
    public static final float MIN_Y = -1.0f;
    public static final float MAX_Y = 1.0f;
    public static final Rgb COL_ASTEROID = Rgb.ORANGE;
    public static final int NUM_ASTEROIDS = 4;
    public static final String NAME = "ASTEROIDS";

    private static final int TARGET_FPS = 30;

    /**
     * Calculate how many nanoseconds per frame so time variation skips frames instead of changing speed.
     */
    static final double NS_PER_FRAME = 1.0 * Maths.BILLION / TARGET_FPS;

    /**
     * Calculate screen edge MARGIN big enough to contain a large Asteroid to disappear off the edge until we can split
     * them.
     */
    static final float MARGIN = Asteroid.Size.LARGE.units / 1.5f;

    private final ModelAnimator game;
    private final Random random = new Random(1234L); // fixed seed to make successive profiling runs comparable

    public Asteroids() {
        super(NAME);
        List<ModelAnimator> animators = new ArrayList<>();
        animators.add(new BatchAnimator<>("rocks", NUM_ASTEROIDS, new AsteroidUpdater()));
        animators.add(mkTitle());
        // bullets is no good for laser
//        animators.add(new BatchAnimator<>("bullets", 2, new ParticleUpdater(Rgb.MAGENTA)));

        game = new Composer(NAME, animators);
    }

    private ModelAnimator mkTitle() {
        TextEngine te = new TextEngine(Rgb.CYAN, AsteroidsFont.INSTANCE);
        float yScale = (float) (0.5 / NAME.length());
        Model textModel = te.textLine(NAME).scale(0.25f, yScale);
        return new BungeeAnimator(textModel, 5000, 1.05f, 1.1f);
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

    private final class ParticleUpdater implements Updater<Particle> {

        private final Clock clock;
        private final Rgb colour;

        public ParticleUpdater(Clock clock, Rgb colour) {
            this.clock = clock;
            this.colour = colour;
        }

        @Override
        public Particle get() {
            return new Particle(randomX(), randomX(), randomVel(), randomVel(), clock.getNs());
        }

        @Override
        public Model update(Particle p, long nsTime) {
            p.update(nsTime);
            return new Point(p.x, p.y, colour).toModel();
        }
    }

    static final class Particle {
        public Particle(float x, float y, float dx, float dy, long lastNanos) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
            this.nsPrev = lastNanos;
        }

        private float x;
        private float y;
        private float dx;
        private float dy;
        private long nsPrev;

        /**
         * Update location based on linear extrapolation of current velocity
         * and previous update time.
         *
         * @param nsTime current time in nanoseconds.
         */
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


    private class AsteroidUpdater implements Updater<Asteroid> {

        @Override
        public Asteroid get() {
            return new Asteroid(Asteroid.Size.LARGE, random);
        }

        @Override
        public Model update(Asteroid item, long nsTime) {
            return item.update(nsTime).toModel();
        }
    }
}
