package com.chromosundrift.vectorbrat.physics;

public interface Clock {
    /**
     * Gets the current time in nanoseconds.
     * @return nanos.
     */
    long getNs();

    /**
     * Returns the current rate of time. For true clocks, this should always be 1.0. Negative values are illegal.
     * @return clock rate.
     */
    float rate();
}
