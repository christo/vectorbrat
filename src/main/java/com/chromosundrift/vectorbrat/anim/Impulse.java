package com.chromosundrift.vectorbrat.anim;

import com.chromosundrift.vectorbrat.geom.Geom;
import com.chromosundrift.vectorbrat.geom.Model;

import java.util.function.Function;

/**
 * Transforms a {@link com.chromosundrift.vectorbrat.geom.Model} over time. Represents
 * the effect of a force acting on a T for a number of nanoseconds and producing a new
 * T, perhaps translated, scaled or tinted.
 * @param <T>
 */
@FunctionalInterface
public interface Impulse<T> {
    T apply(long nsDelta, T model);
}
