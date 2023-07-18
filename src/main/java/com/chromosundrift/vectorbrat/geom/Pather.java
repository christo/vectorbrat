package com.chromosundrift.vectorbrat.geom;

import java.util.ArrayList;

/**
 * Holds a planned signal path for 5 channel vector control: x, y, r, g, b.
 * The number of samples in each dimension should always be the same.
 */
public interface Pather {

    ArrayList<Float> getXs();

    ArrayList<Float> getYs();

    ArrayList<Float> getRs();

    ArrayList<Float> getGs();

    ArrayList<Float> getBs();

    int size();
}
