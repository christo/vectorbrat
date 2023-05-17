package com.chromosundrift.vectorbrat.geom;

import java.util.stream.Stream;

public interface Model {
    Stream<Polyline> polylines();

    Stream<Point> points();

    boolean isEmpty();

    int countVertices();

    int countPolylines();

    int countPoints();

    Model scale(float factor);

    Stream<Line> lines();
}
