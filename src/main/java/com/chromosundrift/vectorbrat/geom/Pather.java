package com.chromosundrift.vectorbrat.geom;

import java.util.ArrayList;

public interface Pather {
    void plan(Model m);

    ArrayList<Float> getXs();

    ArrayList<Float> getYs();

    ArrayList<Float> getRs();

    ArrayList<Float> getGs();

    ArrayList<Float> getBs();
}
