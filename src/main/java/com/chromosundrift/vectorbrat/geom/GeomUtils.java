package com.chromosundrift.vectorbrat.geom;

import java.util.stream.Stream;

public final class GeomUtils {

    private GeomUtils() {
    }

    public static Stream<Point> linePoints(Geom geom) {
        Stream<Line> lines = geom.lines();
        return linePoints(lines);
    }

    public static Stream<Point> linePoints(Stream<Line> lines) {
        return lines.flatMap(l -> Stream.of(l.from(), l.to()));
    }
}
