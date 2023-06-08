package com.chromosundrift.vectorbrat.geom;

import com.chromosundrift.vectorbrat.VectorBratException;

/**
 * SPI for things that animate by model updates over time.
 */
public interface ModelAnimator {

    String getName();

    /**
     * Will be called to initialise everything.
     */
    void start();

    /**
     * Will be called to cleanup everything.
     */
    void stop();

    /**
     * Implementors must perform model updates here.
     *
     * @param nsTime reference time in nanoseconds.
     * @return updated model.
     */
    Model update(long nsTime) throws VectorBratException;
}
