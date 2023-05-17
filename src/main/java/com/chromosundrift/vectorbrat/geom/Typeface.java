package com.chromosundrift.vectorbrat.geom;

public interface Typeface {
    Model getChar(char c);

    float gap(char c1, char c2);
}
