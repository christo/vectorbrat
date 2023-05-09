package com.chromosundrift.vectorbrat.laser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.audio.SoundBridge;
import com.chromosundrift.vectorbrat.audio.jack.LaserDriver;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.swing.LaserController;

/**
 * Handles path planning and vector drawing on a laser or scope. Delegates signal details to {@link LaserDriver}.
 */
public final class LaserDisplay implements VectorDisplay, LaserController {

    private static final Logger logger = LoggerFactory.getLogger(LaserDisplay.class);
    public static final int MAX_PPS = 30000;
    public static final int MIN_PPS = 2;
    private final DoubleBufferedVectorDisplay vectorDisplay;
    private final LaserDriver laserDriver = null;
    private int pps = MAX_PPS;

    /**
     * Time in nanos to dwell on an isolated point.
     */
    private int pointDwellNano;

    /**
     * Time to dwell on a line endpoint.
     */
    private int endPointDwellNano;

    private volatile boolean running;
    private Thread thread;
    private volatile boolean armed;

    public LaserDisplay(Config config) throws VectorBratException {
        logger.info("initialising LaserDisplay");
        this.vectorDisplay = new DoubleBufferedVectorDisplay();
//        this.laserDriver = new LaserDriver(config);
    }

    private void renderConnectedLine(Point from, Point to) {
        // do quintic easing
    }

    private void renderPoint(Point point) {
        // use dwell time
    }

    /**
     * Renders the model once at the configured rate while holding the lock for model updates.
     *
     * @param model the model to render
     * @return null
     */
    private Void render(Model model) {

        // TODO get x and y channels
        // TODO get timer from audio system
        // TODO path planner - nearest unrendered neighbour
        // get all the points from the polygon and sort them to render order
        // TODO render xy coordinates to the audio buffer - keep rendering the same point until time to move
        // TODO quintic easing
        // TODO pen down - colour
        // closed poly for now
        // TODO pen up - black
        return null;
    }

    /**
     * Renders continually at the configured rate.
     */
    public void run() throws VectorBratException {
        logger.info("running laser display");
        laserDriver.start();
        running = true;
        while (running) {
            vectorDisplay.withLockAndFlip(this::render);
        }
    }

    /**
     * Requests stop at earliest convenience.
     */
    public void stop() {
        running = false;
    }



    /**
     * Will block until laser is finished any in-progress model rendering.
     *
     * @param model the model to update to
     */
    @Override
    public void setModel(Model model) {
        vectorDisplay.setModel(model);
    }

    /**
     * Starts in its own thread.
     * @param model the initial model.
     */
    public void start(Model model) {
        logger.info("starting laser display");
        vectorDisplay.setModel(model);
        thread = new Thread(() -> {
            try {
                run();
            } catch (VectorBratException e) {
                logger.error("laser display died", e);
                stop();
            }
        });
    }

    @Override
    public void setOn(boolean on) {
        logger.info("%s laser".formatted(on ? "arming" : "disarming"));
        this.armed = on;
    }

    @Override
    public boolean getOn() {
        return this.armed;
    }

    @Override
    public int getPps() {
        return pps;
    }

    @Override
    public void setPps(int pps) {
        this.pps = pps;
    }
}
