package com.chromosundrift.vectorbrat.geom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.chromosundrift.vectorbrat.Config.SAMPLE_MAX;
import static com.chromosundrift.vectorbrat.Config.SAMPLE_MIN;

public class Pattern {

    private static final Logger logger = LoggerFactory.getLogger(Pattern.class);

    static List<Polyline> sineWaves(Rgb c, int n) {
        List<Polyline> polylines = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Point[] points = new Point[n];
            for (int j = 0; j < n; j++) {
                points[j] = new Point((float) (i * 0.05 - 0.4) + (float) (Math.sin(j * Math.TAU / n) * -0.015f * i), (float) (j * 0.09 - 0.7), c);
            }
            polylines.add(Polyline.open("sin_" + i, c, points));
        }
        return polylines;
    }

    public static Model sineWaves(Rgb color) {
        return new Model("sine waves", sineWaves(color, 14));
    }

    public static Model testPattern1() {
        Model m = new Model("test pattern 1");
        m.add(createMidSquare(Rgb.ORANGE));
        // centre dots
        for (float i = 0; i < 0.4; i += 0.1) {
            m.add(new Point(0.0f, i, Rgb.MAGENTA));
        }

        Rgb c = Rgb.CYAN;
        m.add(Polyline.open("-y arrow", c, new Point(-0.15f, -0.35f, c), new Point(0.0f, -0.5f, c), new Point(0.15f, -0.35f, c)));
        // dot x-aligned with arrow point and y-aligned with wing tips
        m.add(new Point(0.0f, -0.35f, Rgb.RED));
        // bottom right handle
        c = Rgb.BLUE;
        m.add(Polyline.open("++ handle", c, new Point(0.5f, 0.5f, c), new Point(0.75f, 0.75f, c)));

        m.add(Polyline.box("+-box", 0.8f, -1f, 1f, -0.8f, Rgb.GREEN));
        m.add(Polyline.box("++box", 0.8f, 0.8f, 1f, 1f, Rgb.GREEN));
        m.add(Polyline.box("-+box", -1f, 0.8f, -0.8f, 1f, Rgb.GREEN));
        m.add(Polyline.box("--box", -1f, -1f, -0.8f, -0.8f, Rgb.GREEN));
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
    public static Model boxGrid(int nx, int ny, Rgb c) {
        Model m = new Model("BoxGrid%sx%s".formatted(nx, ny));
        float extent = 2f;  // total width or height
        float offset = -1;  // add to extent to get coordinate range
        float w = extent / (nx * 2 + 1);
        float h = extent / (ny * 2 + 1);
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                float x = i * w * extent + offset + w;
                float y = j * h * extent + offset + h;
                m.add(Polyline.box(x, y, x + w, y + h, c));
            }
        }
        return m;
    }

    public static Model midSquare(Rgb c) {
        Model m = new Model("mid square");
        return m.add(createMidSquare(c));
    }

    static Polyline createMidSquare(Rgb c) {
        return Polyline.box("mid square", -0.5f, -0.5f, 0.5f, 0.5f, c);
    }

    public static Model boundingBox(Rgb color) {
        return new Model().add(Polyline.box(SAMPLE_MIN, SAMPLE_MIN, SAMPLE_MAX, SAMPLE_MAX, color));
    }

    public static Model boundingBox() {
        return boundingBox(Rgb.WHITE);
    }
}
