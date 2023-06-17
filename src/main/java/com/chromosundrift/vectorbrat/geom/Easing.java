package com.chromosundrift.vectorbrat.geom;

/**
 * Representation of polarity for interpolation and easing functions.
 */
public enum Easing {
    BIPOLAR("ease in and out"),
    POSITIVE("ease out"),
    NEGATIVE("ease in");

    public final String description;

    Easing(String description) {
        this.description = description;
    }
}
