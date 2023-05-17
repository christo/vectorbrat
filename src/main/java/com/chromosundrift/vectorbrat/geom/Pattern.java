package com.chromosundrift.vectorbrat.geom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Pattern {

    private static final Logger logger = LoggerFactory.getLogger(Pattern.class);

    static List<Polyline> sineWaves(Color c, int n) {
        List<Polyline> polylines = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Point[] points = new Point[n];
            for (int j = 0; j < n; j++) {
                points[j] = new Point((float) (i*0.05 - 0.4) + (float) (Math.sin(j * Math.TAU/n) * -0.015f*i), (float) (j*0.09-0.7), c);
            }
            polylines.add(Polyline.open("sin_" + i, c, points));
        }
        return polylines;
    }

    public static Model sineWaves(Color color) {
        return new GlobalModel("sine waves", sineWaves(color, 14));
    }

    public static GlobalModel testPattern1() {
        GlobalModel m = new GlobalModel("test pattern 1");
        m.add(createMidSquare(Color.ORANGE));
        // centre dots
        for (float i = 0; i < 0.4; i += 0.1) {
            m.add(new Point(0.0f, i, Color.MAGENTA));
        }

        Color c = Color.CYAN;
        m.add(Polyline.open("-y arrow", c, new Point(-0.15f, -0.35f, c), new Point(0.0f, -0.5f, c), new Point(0.15f, -0.35f, c)));
        // dot x-aligned with arrow point and y-aligned with wing tips
        m.add(new Point(0.0f, -0.35f, Color.RED));
        // bottom right handle
        c = Color.BLUE;
        m.add(Polyline.open("++ handle", c, new Point(0.5f, 0.5f, c), new Point(0.75f, 0.75f, c)));

        m.add(Polyline.box("+-box", 0.8f, -1f, 1f, -0.8f, Color.GREEN));
        m.add(Polyline.box("++box", 0.8f, 0.8f, 1f, 1f, Color.GREEN));
        m.add(Polyline.box("-+box", -1f, 0.8f, -0.8f, 1f, Color.GREEN));
        m.add(Polyline.box("--box", -1f, -1f, -0.8f, -0.8f, Color.GREEN));
        logger.info("created test pattern: " + m);
        return m;
    }

    /**
     * Creates a box grid in the given color with boxes the same size as
     * the gaps between them.
     *
     * @param nx number of boxes in x axis
     * @param ny number of boxes in y axis
     * @param c  color
     * @return the Model
     */
    public static GlobalModel boxGrid(int nx, int ny, Color c) {
        GlobalModel m = new GlobalModel();
        float extent = 2f;  // total width or height
        float offset = -1;  // add to extent to get coordinate range
        float w = extent/(nx*2+1);
        float h = extent/(ny*2+1);
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                float x = i * w * extent + offset + w;
                float y = j * h * extent + offset + h;
                m.add(Polyline.box(x, y, x + w, y + h, c));
            }
        }
        return m;
    }

    public static GlobalModel midSquare(Color c) {
        GlobalModel m = new GlobalModel("mid square");
        return m.add(createMidSquare(c));
    }

    static Polyline createMidSquare(Color c) {
        return Polyline.box("mid square", -0.5f, -0.5f, 0.5f, 0.5f, c);
    }
}
