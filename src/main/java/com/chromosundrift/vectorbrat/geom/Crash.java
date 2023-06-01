package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;

public class Crash extends StaticAnimator {
    public Crash(String message) {
        super("crash", redX(message));
    }

    private static Model redX(String message) {
        // TODO vector font rendered message with line breaks
        Point.PointFactory red = new Point.PointFactory(Color.RED);
        Polyline bs = Polyline.open("\\", Color.RED, red.p(-0.1f, -0.1f), red.p(0.1f, 0.1f));
        Polyline fs = Polyline.open("/", Color.RED, red.p(0.1f, -0.1f), red.p(-0.1f, 0.1f));
        return new Model("crash", Arrays.asList(bs, fs), Collections.emptyList());
    }
}
