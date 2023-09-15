package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.anim.BungeeAnimator;
import com.chromosundrift.vectorbrat.asteroids.Asteroid;
import com.chromosundrift.vectorbrat.asteroids.Asteroids;
import com.chromosundrift.vectorbrat.geom.AsteroidsFont;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Pattern;
import com.chromosundrift.vectorbrat.geom.Polyline;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.geom.TextEngine;
import com.chromosundrift.vectorbrat.physics.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Utility methods for creating chunks of models and animations with specific config
 * to make good demos.
 */
public class Demos {
    static Model mkRock() {
        List<Polyline> pls = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pls.add(new Asteroid(Asteroid.Size.LARGE, new Random()).toPolyline());
        }
        return new Model("rock", pls);
    }

    static Model mkTextModel(String text) {
        TextEngine te = new TextEngine(Rgb.CYAN, AsteroidsFont.INSTANCE);
        float yScale = (float) (1.0 / text.length());
        return te.textLine(text).scale(0.6f, yScale);
    }

    static AppMap mkAppMap(Consumer<Model> modelConsumer, String text) {
        AppMap ar = new AppMap(modelConsumer, SystemClock.INSTANCE);
        ar.add(new BungeeAnimator(mkTextModel(text), 900, 0.6f, 0.8f));
        Model aModel = mkTextModel("A");
        ar.add(new BungeeAnimator(aModel, 1500, 0.9f, 0.1f));
        ar.add(new Asteroids());

        ar.add(Pattern.testPattern1().scale(0.8f, 0.8f));
        ar.add(Pattern.sineWaves(Rgb.RED));
        ar.add(Pattern.boxGrid(3, 2, Rgb.CYAN));
        ar.add(mkRock());
        return ar;
    }
}
