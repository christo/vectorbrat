package com.chromosundrift.vectorbrat.physics;

/**
 * Supplies current time. Implementations may provide ways to manipulate the time.
 * Epoch is arbitrary starting point that must remain fixed during runtime.
 */
public interface Clock {

    /**
     * Gets the current time in nanoseconds.
     *
     * @return nanos since epoch.
     */
    long getNs();

}
