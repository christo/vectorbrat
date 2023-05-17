package com.chromosundrift.vectorbrat.geom;

import java.util.stream.Stream;

public interface Model {
    Stream<Polyline> polylines();

    Stream<Point> points();

    boolean isEmpty();

    int countVertices();

    String getName();

    int countPolylines();

    int countPoints();

    Model scale(float factorX, float factorY);

    Stream<Line> lines();

    /**
     * Returns a new model with all the content of this and the other model.
     *
     * @param other the other model.
     * @return a new Model.
     */
    Model merge(Model other);
}
