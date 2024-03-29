package com.chromosundrift.vectorbrat.physics;

/**
 * Clock that can be arbitrarily set to any time.
 */
public final class TimeMachine implements Clock {
    private long ns = 0L;

    @Override
    public long getNs() {
        return ns;
    }

    public void setNs(long ns) {
        this.ns = ns;
    }

    public void add(long nsDelta) {
        this.ns += nsDelta;
    }
}
