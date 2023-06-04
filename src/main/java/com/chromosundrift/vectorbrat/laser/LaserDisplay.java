package com.chromosundrift.vectorbrat.laser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Interpolator;

/**
 * Top level VectorDisplay for laser or scope. Delegates path interpolation to {@link Interpolator} and signal details
 * to {@link LaserDriver}.
 */
public final class LaserDisplay implements VectorDisplay, LaserController {

    private static final Logger logger = LoggerFactory.getLogger(LaserDisplay.class);

    private final DoubleBufferedVectorDisplay vectorDisplay;
    private final LaserDriver laserDriver;
    private final ThreadFactory threadFactory;
    private final Set<Consumer<LaserController>> updateListeners;
    private final Config config;
    private int pps;
    private volatile boolean running;
    /**
     * Controls the thread that continually updates the model.
     */
    private ExecutorService exec;
    /**
     * Tracks whether the model changed and a new path plan must be generated.
     */
    private volatile boolean modelDirty;
    private long lastPathPlanTime;
    private Interpolator pathPlanner;

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
        this.lastPathPlanTime = -1;
        this.updateListeners = new LinkedHashSet<>();
        this.modelDirty = true;
        this.config = config;
    }

    /**
     * Renders the model once at the configured rate while holding the lock for model updates, if the laserDriver is
     * off, does nothing. Called by VectorDisplay using its model.
     *
     * @param model the model to render
     * @return null
     */
    private Void render(Model model) {

        if (modelDirty) {

            // calculate scan rate
            pathPlanner = new Interpolator(this.config);
            pathPlanner.plan(model);
            laserDriver.setPathPlanner(pathPlanner);
            modelDirty = false;
        }

        return null;
    }

    /**
     * Renders continually at the configured rate, unless driver is "off", in which case, we do lots of nothing. Runs
     * in the current thread.
     */
    private void run() throws VectorBratException {
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
     */
    public void start() {
        if (exec != null && !exec.isShutdown()) {
            logger.warn("start requested but already running");
        } else {
            logger.info("starting laser display");

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
        this.tellListeners();
    }

    private void tellListeners() {
        for (Consumer<LaserController> updateListener : this.updateListeners) {
            updateListener.accept(this);
        }
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
        this.tellListeners();
    }

    @Override
    public float getSampleRate() {
        return laserDriver.getSampleRate();
    }

    @Override
    public int getBufferSize() {
        return laserDriver.getBufferSize();
    }

    @Override
    public long getPathPlanTime() {
        return lastPathPlanTime;
    }

    @Override
    public void setPathPlanTime(long planTime) {
        this.lastPathPlanTime = planTime;
        this.tellListeners();   // TODO replace this mechanism with canonical listenable properties library
    }

    @Override
    public void addUpdateListener(Consumer<LaserController> clc) {
        this.updateListeners.add(clc);
    }

    @Override
    public Interpolator getPathPlanner() {
        return this.pathPlanner;
    }
}
