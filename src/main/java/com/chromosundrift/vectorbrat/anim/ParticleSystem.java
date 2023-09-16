package com.chromosundrift.vectorbrat.anim;

import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.chromosundrift.vectorbrat.geom.Model.EMPTY;

/**
 * Generic particle system using Models intended for particle effects such as fire, smoke, bubble animations.
 * Generated models are merged from a particle elements, each of which has velocity, a creation function, an
 * {@link Impulse} which modifies the moving particle over a timestep, maximum number of particles and a
 * predicate for despawning particles, such as when they have faded out, left the scene or died of old age.
 */
public class ParticleSystem extends AbstractAnimator {

    private static final int MAX_MAX = 100;
    private final Supplier<Mover<Model>> seed;
    private final List<Mover<Model>> particles;
    private final Impulse<Mover<Model>> impulse;
    private final long nsPerSpawn;
    private final int max;
    private final Predicate<Mover<Model>> kill;
    private long nsLastUpdate = -1;
    private long nsLastSpawn = -1;

    /**
     * Creates a particle emitter with spawner, a transform function over time, a kill condition etc.
     *
     * @param name       name.
     * @param seed       particle spawner.
     * @param nsPerSpawn number of nanoseconds between each spawned
     * @param impulse    function that takes a ns time delta and produces a function that updates a Model.
     * @param kill       predicate to decide whether to kill a particle
     */
    public ParticleSystem(
            String name,
            Supplier<Mover<Model>> seed,
            long nsPerSpawn,
            int max,
            Impulse<Mover<Model>> impulse,
            Predicate<Mover<Model>> kill) {
        super(name);
        if (nsPerSpawn < 0) {
            throw new IllegalArgumentException("nsPerSpawn must be positive");
        }
        this.nsPerSpawn = nsPerSpawn;
        if (max < 1 || max > MAX_MAX) {
            throw new IllegalArgumentException("sensible maximum must be 1-" + MAX_MAX);
        }
        this.max = max;
        this.kill = kill;
        this.particles = new ArrayList<>();
        this.seed = seed;
        this.impulse = impulse;
    }

    @Override
    public Model update(long nsTime) throws VectorBratException {
        // emit new particles
        if (nsLastSpawn < 0) {
            nsLastSpawn = nsTime;
        } else {
            long nsSinceLastSpawn = nsTime - nsLastSpawn;
            // spawns all particles due at the configured rate
            long dueSpawns = nsSinceLastSpawn / nsPerSpawn;
            // don't spawn beyond the maximum configured amount
            long toSpawn = Math.min(max - particles.size(), dueSpawns);
            for (int i = 0; i < toSpawn; i++) {
                particles.add(seed.get());
            }
            if (toSpawn > 0) {
                // derive effective spawn epoch from requested amount, accounting for fractional gestation
                // but spawn requests that exceed maximum are discarded so the backlog does not grow too large
                // this balances behaviour when spawn rates are extremely high or low
                nsLastSpawn = dueSpawns * nsPerSpawn;
            }
        }
        // despawn dead particles
        particles.removeIf(kill);
        if (nsLastUpdate >= 0) {
            // update particles using impulse
            particles.replaceAll(model -> impulse.apply(nsTime - nsLastUpdate, model));
        }
        nsLastUpdate = nsTime;
        // compose all particles into a single model
        Model composite = EMPTY;
        for (Mover<Model> particle : particles) {
            composite = composite.merge(particle.object());
        }
        return composite;

    }


}
