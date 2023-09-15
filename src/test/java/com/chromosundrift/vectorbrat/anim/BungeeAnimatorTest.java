package com.chromosundrift.vectorbrat.anim;

import com.chromosundrift.vectorbrat.anim.BungeeAnimator;
import com.chromosundrift.vectorbrat.data.Maths;
import com.chromosundrift.vectorbrat.geom.Pattern;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BungeeAnimatorTest {

    private static final double PRECISION = 0.01;

    @Test
    public void scale() {
        // precalculate key points in sine for our period
        int msPeriod = 1000;
        int msPeriodHalf = 500;
        int msPeriodQuarter = 250;
        int msPeriodThreeQuarters = 750;
        float offset = 0.2f;
        BungeeAnimator b = new BungeeAnimator(Pattern.boundingBox(), msPeriod, 1f, offset);
        // check the "zero crossings" (normalised to 0.5) occur at zero, period and half period
        assertEquals(0.5 + offset, b.calculateScale(0), PRECISION);
        assertEquals(0.5 + offset, b.calculateScale(Maths.millisToNanos(msPeriod)), PRECISION);
        assertEquals(0.5 + offset, b.calculateScale(Maths.millisToNanos(msPeriodHalf)), PRECISION);

        // check the peak (1) and trough (0)
        assertEquals(1f + offset, b.calculateScale(Maths.millisToNanos(msPeriodQuarter)), PRECISION);
        assertEquals(0f + offset, b.calculateScale(Maths.millisToNanos(msPeriodThreeQuarters)), PRECISION);
    }

}
