package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
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

    /**
     * Returns a new Model offset by the given x and y. They're just added to all coordinates.
     */
    Model offset(float dx, float dy);

    /**
     * Returns a new model with everything this colour.
     *
     * @param color the color.
     * @return the new Model.
     */
    Model colored(Color color);
}
