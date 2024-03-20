package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * Helper for drawing the simulator beam with the swing api.
 */
class SimulatorRenderer {

    public static final BasicStroke STROKE_HARD_CODED = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    private static final Logger logger = LoggerFactory.getLogger(SimulatorRenderer.class);

    private final LaserSimulator laserSimulator;
    private final Stroke beamStroke;
    private float sampleRate;

    SimulatorRenderer(LaserSimulator laserSimulator) {
        this.laserSimulator = laserSimulator;
        this.beamStroke = STROKE_HARD_CODED;
    }

    /**
     * Draw the simulation on the given image. Called from UI thread.
     *
     * @param image draw simulation on this image.
     * @return number of points drawn.
     */
    int draw(final BufferedImage image, final Graphics2D g2) {
        int width = image.getWidth();
        int height = image.getHeight();
        g2.setStroke(getBeamStroke(width, height));
        Stream<Point> trail = laserSimulator.getTrail()
                // scale out of sample space
                .map(p -> p.offset(1f, 1f).scale((float) width / 2, (float) height / 2));
        final AtomicReference<Point> prev = new AtomicReference<>(null);
        AtomicInteger nPoints = new AtomicInteger(0);
        trail.forEachOrdered(point -> {
            nPoints.incrementAndGet();
            boolean firstPoint = prev.compareAndSet(null, point);
            // if no previous point, use current point for both ends of "line", otherwise use prev
            Point fromPoint = firstPoint ? point : prev.get();
            prev.set(point);
            // should colour always be set to from point?
            Color colour = new Color(fromPoint.r(), fromPoint.g(), fromPoint.b());
            g2.setColor(colour);
            int x1 = (int) fromPoint.x();
            int y1 = (int) fromPoint.y();
            int x2 = (int) point.x();
            int y2 = (int) point.y();
            g2.drawLine(x1, y1, x2, y2);
        });
        prev.set(null);
        return nPoints.get();
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
