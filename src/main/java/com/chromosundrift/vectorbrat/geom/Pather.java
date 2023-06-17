package com.chromosundrift.vectorbrat.geom;

import java.util.ArrayList;

/**
 * Holds a planned signal path for 5 channel vector control: x, y, r, g, b.
 */
public interface Pather {

    ArrayList<Float> getXs();

    ArrayList<Float> getYs();

    ArrayList<Float> getRs();

    ArrayList<Float> getGs();

    ArrayList<Float> getBs();
}
