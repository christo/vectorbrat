package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.geom.Model;

public interface VectorDisplay<T> {
    void setModel(Model model);

    /**
     * The minimum brightness representable before being indistinguishable from black or balanced colour
     * representation breaks down.
     *
     * @return the minimum brightness level between 0f-1f
     */
    float getMinimumBrightness();

    T getTuning();

    boolean supportsBlank();
}
