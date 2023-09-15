package com.chromosundrift.vectorbrat.geom;

import org.junit.Assert;
import org.junit.Test;

public class AsteroidsFontTest {
    @Test
    public void oneLetter() {
        Model a = AsteroidsFont.INSTANCE.getChar('A');
        Assert.assertEquals(0, a.countPoints());
        a.lines().forEach(line -> {
            TestUtils.assertInRange("from", line.from());
            TestUtils.assertInRange("to", line.to());
        });
    }

}
