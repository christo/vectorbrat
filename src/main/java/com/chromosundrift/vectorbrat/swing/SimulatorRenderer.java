package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.physics.BeamState;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

/**
 * Helper for drawing the simulator beam with the swing api.
 */
class SimulatorRenderer {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorRenderer.class);

    public static final BasicStroke STROKE_HARD_CODED = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final Boolean DEBUG_ALL_POINTS = false;
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
            if (DEBUG_ALL_POINTS || firstPoint) {
                // indicate first point
                drawLocator(g2, screenX, screenY, width, height);
                drawDebugPoint(g2, screenX, screenY, Color.RED);
            }
            //drawVelocityVectors(g2, laserSimulator.getBeamState(), width, height);
        }
        if (prev != null) {
            // indicate last point
            int lastScreenX = (int) ((prev.x() + 1) * width / 2);
            int lastScreenY = (int) ((prev.y() + 1) * height / 2);
            drawDebugPoint(g2, lastScreenX, lastScreenY, Color.GREEN);
        }

        return nPoints;
    }

    private void drawVelocityVectors(Graphics2D g2, BeamState beamState, int width, int height) {
        g2.setColor(Color.ORANGE);
        int vectorScale = width / 50;
        int x = (int) ((beamState.xPos + 1) * width/2);
        int y = (int) ((beamState.yPos + 1) * height/2);
        int x2 = (int) (x + beamState.xVel * vectorScale);
        int y2 = (int) (y + beamState.yVel * vectorScale);
        g2.drawLine(x, y, x2, y2);
    }

    private void drawLocator(Graphics2D g2,int screenX, int screenY, int width, int height) {
        g2.setColor(Color.PINK);
        g2.setStroke(DEBUG_POINT_STROKE);
        // draw locator ticks on the edges of the screen furthest from the point

        int len = 20;
        // height indicator is horizontal
        if (screenX > width/2) {
            // point is in right half
            g2.drawLine(0, screenY, len, screenY);
        } else {
            // point is in left half
            g2.drawLine(width, screenY, width - len, screenY);
        }
        // left-right indicator is vertical
        if (screenY > height/2) {
            // point is in bottom half
            g2.drawLine(screenX, 0, screenX, len);
        } else {
            // point is in top half
            g2.drawLine(screenX, height, screenX, height - len);
        }
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
