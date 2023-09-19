package com.chromosundrift.vectorbrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.chromosundrift.vectorbrat.geom.Crash;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.anim.ModelAnimator;
import com.chromosundrift.vectorbrat.anim.StaticAnimator;
import com.chromosundrift.vectorbrat.physics.Clock;

/**
 * Holds a number of {@link ModelAnimator ModelAnimators} of which one is active at a time. Exceptions are handled and
 * their lifecycles are managed. If the active one dies, it's replaced with a {@link Crash} that shows the exception
 * message. The active {@link ModelAnimator} can be changed with {@link #setAnimator(String)}. Model changes can be
 * subscribed to and a clock is also provided to the constructor via parameter which supplies animators with a common
 * time reference.
 */
public class AppMap implements Runnable, AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppMap.class);

    private final TreeMap<String, ModelAnimator> animators;
    private final Consumer<Model> modelConsumer;
    private final Clock clock;
    private String defaultAnimator;
    private String animator;

    public AppMap(
            Map<String, ModelAnimator> animators,
            String defaultAnimator,
            Consumer<Model> modelConsumer,
            Clock clock
    ) {
        this.modelConsumer = modelConsumer;
        this.clock = clock;
        this.defaultAnimator = defaultAnimator;
        this.animators = new TreeMap<>();
        this.animators.putAll(animators);
    }

    public AppMap(Consumer<Model> modelConsumer,
                  Clock clock) {
        this(new TreeMap<>(), "", modelConsumer, clock);
    }


    /**
     * Returns the animator names.
     *
     * @return animator names.
     */
    @Override
    public Set<String> getAnimators() {
        return animators.keySet();
    }

    /**
     * Switches the current {@link ModelAnimator} to that with the given name, unless it's already current,
     * in which case, does nothing.
     *
     * @param name the animator's name to run.
     * @throws VectorBratException if there's an exception starting the animator.
     */
    @Override
    public void setAnimator(String name) throws VectorBratException {
        if (name == null) {
            throw new IllegalArgumentException("animator must not be null");
        } else if (name.isBlank()) {
            logger.warn("asked to set blank animator");
        }
        if (!name.equals(animator)) {
            ModelAnimator newAnimator = animators.get(name);
            if (newAnimator != null) {
                // shut down old animator
                if (animator != null) {
                    logger.info("shutting down %s".formatted(animator));
                    ModelAnimator oldAnimator = animators.get(animator);
                    try {
                        oldAnimator.stop();
                    } catch (Exception e) {
                        logger.warn("exception stopping %s".formatted(animator), e);
                    }
                }
                // start new animator
                try {
                    animator = name;
                    logger.info("starting animator %s".formatted(animator));
                    newAnimator.start();
                } catch (Exception e) {
                    logger.warn("exception starting %s".formatted(animator), e);
                    throw new VectorBratException(e);
                }
            } else {
                logger.warn("ignoring unknown animator: %s".formatted(name));
            }
        }
    }


    @Override
    public void run() {
        if (defaultAnimator == null || defaultAnimator.equals("") || animators.isEmpty()) {
            throw new RuntimeException("cannot start, animators are a mess");
        }
        logger.info("starting");
        boolean running = true;
        ModelAnimator deadAnimator = null;
        while (running) {
            if (deadAnimator == null) {
                ModelAnimator modelAnimator = animators.get(animator);
                try {
                    modelConsumer.accept(modelAnimator.update(clock.getNs()));
                } catch (VectorBratException e) {
                    logger.error("Exception during update", e);
                    deadAnimator = new Crash(e.getMessage());

                } catch (RuntimeException e) {
                    logger.error("RuntimeException during update", e);
                    deadAnimator = new Crash(e.getMessage());
                }
            } else {
                try {
                    modelConsumer.accept(deadAnimator.update(clock.getNs()));
                } catch (VectorBratException e) {
                    logger.error("Crash animator died", e);
                    running = false;
                }
            }

        }
        logger.info("exiting");
    }

    /**
     * Adds the given model with a {@link StaticAnimator}.
     *
     * @param m the model.
     */
    public void add(Model m) {
        this.add(new StaticAnimator(m.getName(), m));
    }

    public void add(ModelAnimator ma) {
        String name = ma.getName();
        if (name.isBlank()) {
            throw new IllegalArgumentException("cannot add an animator with a blank name");
        }
        if (this.animators.isEmpty()) {
            this.defaultAnimator = name;
        }
        this.animators.put(name, ma);

    }

    public void setDefaultAnimator() throws VectorBratException {
        this.setAnimator(this.defaultAnimator);
    }
}
