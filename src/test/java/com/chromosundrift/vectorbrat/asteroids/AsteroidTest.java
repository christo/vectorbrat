package com.chromosundrift.vectorbrat.asteroids;

import com.chromosundrift.vectorbrat.data.Maths;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import com.chromosundrift.vectorbrat.geom.Box;
import com.chromosundrift.vectorbrat.geom.TestUtils;

public class AsteroidTest {

    private static final Logger logger = LoggerFactory.getLogger(AsteroidTest.class);

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void update() {
        Asteroid a = new Asteroid(Asteroid.Size.LARGE, new Random());
        for (int i = 0; i < 3000; i++) {
            Box bounds = a.toPolyline().bounds().get().scale(0.7f, 0.7f);
            TestUtils.assertInRange(bounds);
            a.update(Maths.msToNanos(i));
        }
    }

}
