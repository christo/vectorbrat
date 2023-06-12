package com.chromosundrift.vectorbrat.asteroids;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
    private final Composer game;
    private final Random random = new Random(1234L); // fixed seed for profiling

    public Asteroids() {
        game = new Composer(NAME, List.of(new RockAnimator(), mkTitle()));
    }

    private ModelAnimator mkTitle() {
        TextEngine te = new TextEngine(Rgb.CYAN, new AsteroidsFont());
        float yScale = (float) (0.5 / NAME.length());
        Model textModel = te.textLine(NAME).scale(0.25f, yScale);
        return new BungeeAnimator(textModel, 2000, 1.55f, 0.8f);
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
            List<Point> points = new ArrayList<>();
            return new Model(getName(), polyLines, points);
        }
    }
}
