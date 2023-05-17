package com.chromosundrift.vectorbrat.asteroids;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.chromosundrift.vectorbrat.geom.GlobalModel;
import com.chromosundrift.vectorbrat.geom.ModelAnimator;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Polyline;

public class Asteroids implements ModelAnimator {
    public static final float MIN_X = -1.0f;
    public static final float MAX_X = 1.0f;
    public static final float MIN_Y = -1.0f;
    public static final float MAX_Y = 1.0f;
    public static final Color COL_ASTEROID = Color.YELLOW.darker();
    public static final int NUM_ASTEROIDS = 10;
    private LinkedList<Asteroid> asteroids = new LinkedList<>();


    @Override
    public void start() {
        asteroids = new LinkedList<>();
        for (int i = 0; i < NUM_ASTEROIDS; i++) {
            asteroids.add(new Asteroid(Asteroid.Size.LARGE));
        }

    }

    @Override
    public void stop() {
        asteroids = null;
    }

    @Override
    public GlobalModel update(long time) {
        List<Polyline> polyLines = new ArrayList<>();
        for (Asteroid asteroid : asteroids) {
            asteroid.update(time);
            polyLines.add(asteroid.toPolyline());
        }
        List<Point> points  = new ArrayList<>();
        return new GlobalModel("Asteroids", polyLines, points);
    }
}
