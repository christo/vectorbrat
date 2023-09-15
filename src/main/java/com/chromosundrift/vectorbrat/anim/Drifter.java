package com.chromosundrift.vectorbrat.anim;

import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Work in progress - decide what configuration would be most reusable for fire, smoke, bubble animations.
 * Animator that causes a model to translate and change colour over time.
 *
 */
public class Drifter extends AbstractAnimator {

    private final Model template;
    private Model previous;
    private final float dx;
    private final float dy;
    private final Rgb dc;

    public Drifter(String name, Model template, float dx, float dy, Rgb dc) {
        super(name);
        this.template = template;
        this.previous = template.deepClone();
        this.dx = dx;
        this.dy = dy;
        this.dc = dc;
    }

    @Override
    public Model update(long nsTime) throws VectorBratException {
        previous = previous.offset(dx, dy).blend(dc::multiply);
        return previous;
    }
}
