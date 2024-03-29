package com.chromosundrift.vectorbrat.anim;

import java.util.List;

import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.anim.AbstractAnimator;
import com.chromosundrift.vectorbrat.anim.ModelAnimator;
import com.chromosundrift.vectorbrat.geom.Model;

/**
 * {@link ModelAnimator} that composes a list of animators together by merging the models.
 * Lifecycle methods start, stop and update are called in list order without exception
 * handling. Any component methods that throw exceptions will result in an incomplete
 * composite call graph and the caller will get the exception.
 */
public class Composer extends AbstractAnimator implements ModelAnimator {

    private final List<ModelAnimator> animators;

    public Composer(String name, List<ModelAnimator> animators) {
        super(name);
        this.animators = animators;
    }

    @Override
    public void start() {
        for (ModelAnimator modelAnimator : animators) {
            modelAnimator.start();
        }
    }

    @Override
    public void stop() {
        for (ModelAnimator modelAnimator : animators) {
            modelAnimator.stop();
        }
    }

    @Override
    public Model update(long nsTime) throws VectorBratException {
        Model m = Model.EMPTY;
        for (ModelAnimator animator : animators) {
            m = m.merge(animator.update(nsTime));
        }
        return m;
    }
}
