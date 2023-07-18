package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * Helper for drawing the simulator beam with the swing api.
 */
class SimulatorRenderer {

    public static final BasicStroke STROKE_HARD_CODED = new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private final LaserSimulator laserSimulator;
    private float sampleRate;
    private Stroke beamStroke;

    SimulatorRenderer(LaserSimulator laserSimulator) {
        this.laserSimulator = laserSimulator;
        this.beamStroke = STROKE_HARD_CODED;
    }

    /**
     * Draw the simulation on the given image. Called from UI thread.
     *
     * @param image draw simulation on this image.
     */
    void draw(final BufferedImage image, final Graphics2D g2) {

        int width = image.getWidth();
        int height = image.getHeight();
        g2.setStroke(getBeamStroke(width, height));
        Stream<Point> trail = laserSimulator.getTrail(width, height);
        final AtomicReference<Point> prev = new AtomicReference<>(null);
        trail.forEach(point -> {
            boolean firstPoint = prev.compareAndSet(null, point);
            if (!firstPoint) {
                Point prevPoint = prev.get();
                Color colour = new Color(prevPoint.r(), prevPoint.g(), prevPoint.b());
                g2.setColor(colour);
                int x1 = (int) prevPoint.x();
                int y1 = (int) prevPoint.y();
                int x2 = (int) point.x();
                int y2 = (int) point.y();
                g2.drawLine(x1, y1, x2, y2);
            }
        });
    }

    private Stroke getBeamStroke(int width, int height) {
        // laser specifications include beam divergence, so a full physically-based
        // simulation would determine the beam spot diameter from the divergence and the
        // simulated distance to the projected image. We don't have any projection distance right now.
        return beamStroke;
    }

    void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }


}
