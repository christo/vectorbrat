package com.chromosundrift.vectorbrat.physics;

/**
 * Clock that can be arbitrarily set to any time.
 */
public final class TimeMachine implements Clock {
    private long ns = 0L;

    public void setNs(long ns) {
        this.ns = ns;
    }

    @Override
    public long getNs() {
        return ns;
    }

    @Override
    public float rate() {
        return 1;
    }
}
