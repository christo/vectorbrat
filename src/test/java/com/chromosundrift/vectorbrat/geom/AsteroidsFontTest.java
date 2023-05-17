package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;

public class AsteroidsFontTest {
    @Test
    public void oneLetter() {
        AsteroidsFont af = new AsteroidsFont();

        Model a = af.getChar('A');
        Assert.assertEquals(0, a.countPoints());
        a.polylines().forEach(polyline -> polyline.lines().forEach(line -> {
            TestUtils.assertInRange("from", line.from());
            TestUtils.assertInRange("to", line.to());
        }));
    }

}
