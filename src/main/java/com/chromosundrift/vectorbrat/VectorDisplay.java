package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.geom.Model;

public interface VectorDisplay<T> {
    void setModel(Model model);

    T getTuning();

    boolean supportsBlank();
}
