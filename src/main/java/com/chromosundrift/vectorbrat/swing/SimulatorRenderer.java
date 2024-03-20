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
    private static final Stroke DEBUG_POINT_STROKE = new BasicStroke(4f);

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
        Point prev = null;
        int nPoints = 0;
        for (Point point : laserSimulator.getTrail().toList()) {
            int screenX = (int) ((point.x() + 1) * width / 2);
            int screenY = (int) ((point.y() + 1) * height / 2);
            nPoints++;
            boolean firstPoint = prev ==null;
            // if no previous point, use current point for both ends of "line", otherwise use prev
            Point fromPoint = firstPoint ? point : prev;
            prev = point;
            int fromScreenX = (int) ((fromPoint.x() + 1) * width / 2);
            int fromScreenY = (int) ((fromPoint.y() + 1) * height / 2);
            // should colour always be set to from point?
            Color colour = new Color(fromPoint.r(), fromPoint.g(), fromPoint.b());
            g2.setColor(colour);
            g2.setStroke(getBeamStroke(width, height));
            g2.drawLine(fromScreenX, fromScreenY, screenX, screenY);
            if (firstPoint) {
                drawDebugPoint(g2, screenX, screenY, Color.RED);
            }
        }
        int lastScreenX = (int) ((prev.x() + 1) * width / 2);
        int lastScreenY = (int) ((prev.y() + 1) * height / 2);

        drawDebugPoint(g2, lastScreenX, lastScreenY, Color.GREEN);
        prev = null;
        return nPoints;
    }

    private void drawDebugPoint(Graphics2D g2, int x2, int y2, Color color) {
        g2.setColor(color);
        g2.setStroke(DEBUG_POINT_STROKE);
        g2.drawOval(x2-5, y2-5, 10, 10);
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
