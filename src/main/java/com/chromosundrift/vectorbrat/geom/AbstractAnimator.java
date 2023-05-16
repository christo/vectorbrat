package com.chromosundrift.vectorbrat.geom;

/**
 * Convenience base class that does nothing for {@link #start()} or {@link #stop()}.
 */
public abstract class AbstractAnimator implements ModelAnimator {

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public abstract Model update(long time);
}
