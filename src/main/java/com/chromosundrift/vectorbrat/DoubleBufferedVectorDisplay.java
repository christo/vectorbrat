package com.chromosundrift.vectorbrat;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import com.chromosundrift.vectorbrat.geom.Model;

/**
 * Threadsafe, double-buffered display. Call {@link DoubleBufferedVectorDisplay#withLockAndFlip(Function)} to render
 * the current model.
 */
public final class DoubleBufferedVectorDisplay implements VectorDisplay {
    private final ReentrantLock lock = new ReentrantLock();
    private Model frontModel;
    private Model backModel;
    private final float minimumBrightness;
    private final boolean blanking;


    public DoubleBufferedVectorDisplay(Model initialModel, float minimumBrightness, boolean blanking) {
        frontModel = initialModel;
        backModel = initialModel;
        this.minimumBrightness = minimumBrightness;
        this.blanking = blanking;
    }

    public DoubleBufferedVectorDisplay(float minimumBrightness, boolean blanking) {
        this(new Model(), minimumBrightness, blanking);
    }

    /**
     * Applies the function to the model using double-buffering and thread safety.
     */
    public void withLockAndFlip(Function<Model, Void> withLockAndFlip) {
        try {
            lock.lock();
            withLockAndFlip.apply(frontModel);
            flip();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sets the back buffer model only. Next render, this will be flipped to the front model.
     *
     * @param model the new model.
     * @return this.
     */
    @Override
    public void setModel(Model model) {
        try {
            lock.lock();
            this.backModel = model;
            flip();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Flips the back model to the front.
     */
    public void flip() {
        try {
            lock.lock();
            this.frontModel = this.backModel;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public float getMinimumBrightness() {
        return minimumBrightness;
    }


    @Override
    public boolean supportsBlank() {
        return blanking;
    }
}
