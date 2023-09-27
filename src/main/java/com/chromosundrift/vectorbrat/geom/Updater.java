package com.chromosundrift.vectorbrat.geom;

import java.util.function.Supplier;

/**
 * Can create an instance of T and also produce a Model for T at a given nanosecond time.
 *
 * @param <T>
 */
public interface Updater<T> extends Supplier<T> {

    Model update(T item, long nsTime);
}
