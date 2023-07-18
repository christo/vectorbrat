package com.chromosundrift.vectorbrat.physics;


/**
 * Returns a unit time clock backed by System.nanoTime()
 */
public final class SystemClock implements Clock {

    public static final SystemClock INSTANCE = new SystemClock();

    private SystemClock() {
    }

    @Override
    public long getNs() {
        return System.nanoTime();
    }

    @Override
    public float rate() {
        return 1f;
    }

}
