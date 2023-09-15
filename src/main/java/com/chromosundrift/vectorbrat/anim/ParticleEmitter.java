package com.chromosundrift.vectorbrat.anim;

import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Vec2;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Work in progress - decide what configuration would be most reusable for fire, smoke, bubble animations.
 * Animator that causes a model to translate and change colour over time.
 */
public class ParticleEmitter extends AbstractAnimator {

    private final Model template;
    private Model[] particles;
    private final Vec2 velocity;
    private final Function<Model, Model> impulse;
    private long nsLastUpdate = -1;

    /**
     * Creates a particle emitter with initial velocity and a transform function over time.
     *
     * @param name       name.
     * @param seed       originating particle.
     * @param initialVel initial velocity.
     * @param impulse    updater.
     */
    public ParticleEmitter(String name, Model seed, Vec2 initialVel, Function<Model, Model> impulse) {
        super(name);
        this.template = seed;
        this.velocity = initialVel;
        this.impulse = impulse;
    }

    @Override
    public Model update(long nsTime) throws VectorBratException {
        // apply forces to all particles, emit new ones and kill old ones
        // TODO use time delta particles, look at Asteroids ParticleUpdater and generalise it

        return Arrays.stream(particles).map(impulse).reduce(Model.EMPTY, Model::merge);
    }
}
