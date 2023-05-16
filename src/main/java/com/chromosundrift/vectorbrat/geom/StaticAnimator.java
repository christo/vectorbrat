package com.chromosundrift.vectorbrat.geom;

/**
 * Doesn't actually animate.
 *
 */
public class StaticAnimator extends AbstractAnimator {

    // TODO make a sinusoidal pulsing animator with a model that is zoomed in and out
    private final Model m;

    public StaticAnimator(Model m) {
        this.m = m;
    }

    @Override
    public Model update(long time) {
        return m;
    }
}
