package com.chromosundrift.vectorbrat.laser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.PathPlanner;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.swing.LaserController;

/**
 * Handles path planning and vector drawing on a laser or scope. Delegates signal details to {@link LaserDriver}.
 */
public final class LaserDisplay implements VectorDisplay, LaserController {

    private static final Logger logger = LoggerFactory.getLogger(LaserDisplay.class);

    private final DoubleBufferedVectorDisplay vectorDisplay;
    private final LaserDriver laserDriver;

    private int pps;

    /**
     * Time in nanos to dwell on an isolated point.
     */
    private int pointDwellNano;

    /**
     * Time to dwell on a line endpoint.
     */
    private int endPointDwellNano;

    private volatile boolean running;

    /**
     * Controls the thread that continually updates the model.
     */
    private ExecutorService exec;

    /**
     * Tracks whether the model changed and a new path plan must be generated.
     */
    private volatile boolean modelDirty;
    private ThreadFactory threadFactory;

    public LaserDisplay(Config config) throws VectorBratException {
        logger.info("initialising LaserDisplay");
        this.vectorDisplay = new DoubleBufferedVectorDisplay();
        this.laserDriver = new LaserDriver(config);
        this.pps = config.getPps();
        this.threadFactory = r -> {
            Thread t = new Thread(r, "laser display");
            t.setPriority(Thread.MAX_PRIORITY);
            return t;
        };
    }

    /**
     * Renders the model once at the configured rate while holding the lock for model updates, if the laserDriver is
     * off, does nothing.
     *
     * @param model the model to render
     * @return null
     */
    private Void render(Model model) {

        // calculate scan rate
        float laserSpeed = 0.01f;


        if (modelDirty) {
            // TODO figure out how to choose a start point
            Point start = new Point(0f, 0f);
            PathPlanner p = new PathPlanner(model, pps / 0.1f, pps / laserSpeed, start);
            laserDriver.setPath(p);
            modelDirty = false;
        }

        return null;
    }

    /**
     * Renders continually at the configured rate, unless driver is "off", in which case, we do lots of nothing. Runs
     * in the current thread.
     */
    public void run() throws VectorBratException {
        logger.info("running laser display");
        laserDriver.start();
        running = true;
        while (running) {
            if (laserDriver.isOn()) {
                vectorDisplay.withLockAndFlip(this::render);
            } else {
                try {
                    //noinspection BusyWait
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {

                }
            }
        }
    }

    /**
     * Requests stop at earliest convenience.
     */
    public void stop() {
        if (exec == null) {
            logger.warn("stop requested but never started");
        } else if (exec.isShutdown()) {
            logger.info("stop requested but executor was already shut down");
        } else {
            running = false;
            logger.info("shutdown initiated");
            exec.shutdown();
        }
    }


    /**
     * Will block until laser is finished any in-progress model rendering. Depending on current pps and
     * the number of points in the currently rendering model, this may take a number of seconds.
     * Do not call from UI thread.
     *
     * @param model the model to update to
     */
    @Override
    public void setModel(Model model) {
        vectorDisplay.setModel(model);
        modelDirty = true;
    }

    /**
     * Starts in its own thread. Call stop to shutdown.
     *
     * @param model the initial model.
     */
    public void start(Model model) {
        if (exec != null && !exec.isShutdown()) {
            logger.warn("start requested but already running");
        } else {
            logger.info("starting laser display");
            vectorDisplay.setModel(model);
            exec = Executors.newSingleThreadExecutor(threadFactory);
            exec.submit(() -> {
                try {
                    run();
                } catch (VectorBratException e) {
                    logger.error("laser display died", e);
                    stop();
                }
            });

        }
    }

    /**
     * Called from ui thread.
     *
     * @return true iff we are armed.
     */
    @Override
    public boolean getOn() {
        return this.laserDriver.isOn();
    }

    /**
     * Called from ui thread.
     *
     * @param on whether we are armed.
     */
    @Override
    public void setOn(boolean on) {
        this.laserDriver.setOn(on);
    }

    /**
     * Called from ui thread.
     *
     * @return the pps
     */
    @Override
    public int getPps() {
        return pps;
    }

    /**
     * Called from ui thread.
     *
     * @param pps new pps.
     */
    @Override
    public void setPps(int pps) {
        this.pps = pps;
    }

    @Override
    public float getSampleRate() {
        return laserDriver.getSampleRate();
    }

    @Override
    public int getBufferSize() {
        return laserDriver.getBufferSize();
    }
}
