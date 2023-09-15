package com.chromosundrift.vectorbrat.anim;

import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.anim.ModelAnimator;
import com.chromosundrift.vectorbrat.geom.Model;

/**
 * Convenience base class that does nothing for {@link #start()} or {@link #stop()}.
 */
public abstract class AbstractAnimator implements ModelAnimator {

    private final String name;

    public AbstractAnimator(String name) {
        this.name = name;
    }

    /**
     * Convenience property.
     * @return name suitable for UI.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Does nothing, subclasses that must perform initialisation do it here.
     */
    @Override
    public void start() {

    }

    /**
     * Does nothing, subclasses that must perform cleanup do it here.
     */
    @Override
    public void stop() {

    }

    /**
     * Returns the Model for the given time in nanoseconds.
     *
     * @param nsTime reference time in nanoseconds.
     * @return the Model representing the animation state.
     * @throws VectorBratException if you're fatally busted, forcing caller to decide what to do.
     */
    @Override
    public abstract Model update(long nsTime) throws VectorBratException;
}
