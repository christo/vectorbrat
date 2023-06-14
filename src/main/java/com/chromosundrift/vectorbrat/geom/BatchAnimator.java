package com.chromosundrift.vectorbrat.geom;

import java.util.ArrayList;
import java.util.List;

import com.chromosundrift.vectorbrat.VectorBratException;

public class BatchAnimator<T> implements ModelAnimator {
    private final String name;
    private final int count;
    private final Updater<T> updater;
    private List<T> items;

    public BatchAnimator(String name, int count, Updater<T> updater) {
        this.name = name;
        this.count = count;
        this.updater = updater;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() {
        items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            items.add(updater.create());
        }
    }

    @Override
    public void stop() {
        items = null;
    }

    @Override
    public Model update(long nsTime) throws VectorBratException {
        Model m = Model.EMPTY;
        for (T item : items) {
            m = m.merge(updater.update(item, nsTime));
        }
        return m;
    }
}
