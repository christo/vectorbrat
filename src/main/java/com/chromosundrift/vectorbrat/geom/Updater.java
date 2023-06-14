package com.chromosundrift.vectorbrat.geom;

public interface Updater<T> {

    T create();

    Model update(T item, long nsTime);
}
