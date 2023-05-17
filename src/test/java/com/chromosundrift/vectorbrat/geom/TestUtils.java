package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;

import static com.chromosundrift.vectorbrat.Config.inSampleRange;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.chromosundrift.vectorbrat.Config;

public class TestUtils {

    /**
     * Asserts the point lies within the bounds of the sample range specified in {@link Config}.
     *
     * @param pointName a name for the point when reporting assertion failures
     * @param point     that's the point.
     */
    public static void assertInRange(String pointName, Point point) {
        assertTrue(pointName + " x was out of range %s".formatted(point.x()), inSampleRange(point.x()));
        assertTrue(pointName + " y was out of range %s".formatted(point.y()), inSampleRange(point.y()));
    }

    static void assertPointColor(String message, Color expected, Point point) {
        assertEquals(message, expected, point.getColor());
    }
}
