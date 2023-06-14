package com.chromosundrift.vectorbrat.geom;

/**
 * Can create an instance of T and also produce a Model for T at a given nanosecond time.
 * @param <T>
 */
public interface Updater<T> {

    T create();

    Model update(T item, long nsTime);
}
