package com.chromosundrift.vectorbrat.physics;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * Clock whose rate can be set and changed, based on an underlying clock. This is intended to be useful for analysing
 * {@link LaserSimulator} paths at slower than real time (rates between 0 and 1.0).
 */
public final class BulletClock implements Clock {
    private final Clock real;
    private volatile float rate;

    /**
     * Smaller rate means slower time.
     *
     * @param real underlying clock source.
     * @param rate multiplier for simulating slower (or faster) time. Must be > 0.
     */
    public BulletClock(@Nonnull Clock real, float rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Rate must be > 0");
        }
        this.real = real;
        this.rate = rate;
    }

    /**
     * Backed by system clock.
     *
     * @param rate time multiplier.
     */
    public BulletClock(float rate) {
        this(SystemClock.INSTANCE, rate);
    }

    @Override
    public long getNs() {
        return (long) (real.getNs() * rate);
    }

    public float rate() {
        return rate;
    }

    public void setRate(float rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Rate must be > 0");
        }
        this.rate = rate;
    }
}
