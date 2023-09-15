package com.chromosundrift.vectorbrat.geom;

import java.util.Arrays;
import java.util.Collections;

import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.anim.StaticAnimator;

/**
 * Animation that renders an exception message.
 */
public class Crash extends StaticAnimator {

    /**
     * Only uses small, fixed number of chars for now until line splitting is implemented and more efficient text is
     * feasible.
     */
    private static final int MAX_TEXT_LEN = 16;

    public Crash(String message) {
        super("crash", redX(message));
    }

    private static Model redX(String message) {
        // Support formatting text with line breaks: https://github.com/christo/vectorbrat/issues/35
        Point.PointFactory red = new Point.PointFactory(Rgb.RED);
        Polyline bs = Polyline.open("\\", Rgb.RED, red.p(-0.1f, -0.1f), red.p(0.1f, 0.1f));
        Polyline fs = Polyline.open("/", Rgb.RED, red.p(0.1f, -0.1f), red.p(-0.1f, 0.1f));
        TextEngine te = new TextEngine(Rgb.ORANGE, AsteroidsFont.INSTANCE);
        Model textModel = te.textLine(Util.truncate(message, MAX_TEXT_LEN));
        return new Model("crash", Arrays.asList(bs, fs), Collections.emptyList()).merge(textModel);
    }
}
