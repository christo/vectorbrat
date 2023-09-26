package com.chromosundrift.vectorbrat.swing;

/**
 * Value holder for UI. Implementations may add interactive controls.
 */
public interface Stat {
    void setValue(long v);

    void setValue(float v);

    void setValue(int v);
}
