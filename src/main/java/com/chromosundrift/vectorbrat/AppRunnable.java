package com.chromosundrift.vectorbrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.chromosundrift.vectorbrat.geom.Crash;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.ModelAnimator;

public class AppRunnable implements Runnable, AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppRunnable.class);

    private final Map<String, ModelAnimator> animators;
    private final String defaultAnimator;
    private final Consumer<Model> modelConsumer;
    private final Supplier<Long> clock;
    private String animator;

    public AppRunnable(
            Map<String, ModelAnimator> animators,
            String defaultAnimator,
            Consumer<Model> modelConsumer,
            Supplier<Long> clock
    ) {
        this.modelConsumer = modelConsumer;
        this.clock = clock;
        if (animators.get(defaultAnimator) == null) {
            throw new IllegalArgumentException("default animator must be in the animators map");
        }
        this.animators = new TreeMap<>();
        this.animators.putAll(animators);
        this.defaultAnimator = defaultAnimator;
    }

    @Override
    public List<String> getAnimators() {
        return animators.keySet().stream().toList();
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
                    logger.info("starting animator %s".formatted(animator));
                    newAnimator.start();
                    animator = name;
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
        logger.info("starting");
        boolean running = true;
        ModelAnimator deadAnimator = null;
        while (running) {
            if (deadAnimator == null) {
                ModelAnimator modelAnimator = animators.get(animator);
                try {
                    modelConsumer.accept(modelAnimator.update(clock.get()));
                } catch (VectorBratException e) {
                    logger.error("Exception during update", e);
                    deadAnimator = new Crash(e.getMessage());

                } catch (RuntimeException e) {
                    logger.error("RuntimeException during update", e);
                    deadAnimator = new Crash(e.getMessage());
                }
            } else {
                try {
                    modelConsumer.accept(deadAnimator.update(clock.get()));
                } catch (VectorBratException e) {
                    logger.error("Crash animator died", e);
                    running = false;
                }
            }

        }
        logger.info("exiting");
    }
}
