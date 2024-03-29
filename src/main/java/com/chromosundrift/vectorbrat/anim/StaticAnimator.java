package com.chromosundrift.vectorbrat.anim;

import com.chromosundrift.vectorbrat.geom.Model;

/**
 * Doesn't actually animate.
 */
public class StaticAnimator extends AbstractAnimator {

    private final Model m;

    public StaticAnimator(Model m) {
        this(m.getName(), m);
    }

    public StaticAnimator(String name, Model m) {
        super(name);
        this.m = m;
    }

    @Override
    public Model update(long nsTime) {
        return m;
    }
}
