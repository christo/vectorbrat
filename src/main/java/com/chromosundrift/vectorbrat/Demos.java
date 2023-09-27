package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.anim.BungeeAnimator;
import com.chromosundrift.vectorbrat.anim.Impulse;
import com.chromosundrift.vectorbrat.anim.ModelAnimator;
import com.chromosundrift.vectorbrat.anim.Mover;
import com.chromosundrift.vectorbrat.anim.ParticleSystem;
import com.chromosundrift.vectorbrat.asteroids.Asteroid;
import com.chromosundrift.vectorbrat.asteroids.Asteroids;
import com.chromosundrift.vectorbrat.data.Maths;
import com.chromosundrift.vectorbrat.geom.AsteroidsFont;
import com.chromosundrift.vectorbrat.geom.Diamond;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Pattern;
import com.chromosundrift.vectorbrat.geom.Polyline;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.geom.TextEngine;
import com.chromosundrift.vectorbrat.geom.Vec2;
import com.chromosundrift.vectorbrat.laser.LaserSpec;
import com.chromosundrift.vectorbrat.physics.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.chromosundrift.vectorbrat.data.Maths.clamp;

/**
 * Utility methods for creating chunks of models and animations with specific config
 * to make good demos. This contains a lot of content and configuration that we want
 * to keep {@link VectorBrat} clean from.
 */
public class Demos {

    private static final Logger logger = LoggerFactory.getLogger(Demos.class);

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

    static AppMap mkAppMap(Consumer<Model> modelConsumer, String text, LaserSpec laserSpec) {
        AppMap appMap = new AppMap(modelConsumer, SystemClock.INSTANCE);
        appMap.add(mkBungeeText(text));
        appMap.add(new BungeeAnimator(mkTextModel("A"), 1500, 0.9f, 0.1f));
        appMap.add(new Asteroids());

        appMap.add(Pattern.testPattern1().scale(0.8f, 0.8f));
        appMap.add(Pattern.sineWaves(Rgb.RED));
        appMap.add(Pattern.boxGrid(3, 2, Rgb.CYAN));
        appMap.add(mkRock());
        appMap.add(mkFire(laserSpec));
        return appMap;
    }

    private static ModelAnimator mkFire(LaserSpec laserSpec) {
        // bad way to do it
        Random r = new Random();
        final Model flame = new Diamond(0.03f, 0.04f, Rgb.WHITE).toModel().offset(0f, 0.7f);
        final Supplier<Mover<Model>> ignition = () -> new Mover<>(flame.deepClone(), new Vec2(0.0, -0.3));
        // impulse function repeatedly applies inner function at this approximate rate
        float functionRate = Maths.msToNanos(40);
        final Impulse<Mover<Model>> flicker = (nsDelta, subject) -> {

            double repeats = (functionRate / nsDelta);

            // fade non-red a lot, fade red less
            Function<Rgb, Rgb> flameFade = rgb -> new Rgb(
                    (float) (rgb.red() * Math.pow(r.nextDouble(0.998, 1.0), repeats)),
                    (float) (rgb.green() * Math.pow(r.nextDouble(0.995, 1.0), repeats)),
                    (float) (rgb.blue() * Math.pow(r.nextDouble(0.95, 1.0), repeats)));
            // scale up, slightly elongating vertically
            Model f = subject.object()
                    .offset(subject.velocity())
                    .scale((float) Math.pow(1.002, repeats), (float) Math.pow(1.003, repeats));
            // apply random drift in x axis
            double xForce = r.nextDouble(-0.00001, 0.00001);
            // apply rising force to terminal velocity
            double yForce = r.nextDouble(-0.0002, -0.0000001);
            Vec2 newV = subject.velocity();
            for (int i = 1; i < repeats; i++) {
                newV = newV.add(xForce, yForce);
            }
            return new Mover<>(f.blend(flameFade), newV.absClamp(0.01));
        };

        Predicate<Mover<Model>> heatDeath = modelMover -> {
            Model model = modelMover.object();
            return !model.bounds().get().inBounds() || model.colours().noneMatch(laserSpec::visible);
        };
        return new ParticleSystem("fire", ignition, Maths.msToNanos(500), 10, flicker, heatDeath);
    }

    private static BungeeAnimator mkBungeeText(String text) {
        return new BungeeAnimator(mkTextModel(text), 1500, 0.6f, 0.8f);
    }
}
