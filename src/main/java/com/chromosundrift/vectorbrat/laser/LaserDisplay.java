package com.chromosundrift.vectorbrat.laser;

import com.google.common.base.Suppliers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.geom.Interpolator;
import com.chromosundrift.vectorbrat.geom.Model;

/**
 * Top level VectorDisplay for laser or scope. Delegates path interpolation to {@link Interpolator} and signal details
 * to {@link LaserDriver}.
 */
public final class LaserDisplay implements VectorDisplay<LaserTuning>, LaserController {

    private static final Logger logger = LoggerFactory.getLogger(LaserDisplay.class);
    private static final long MS_LISTENER_UPDATE = 100;
    private static final int MS_POWER_NAP = 100;

    private final DoubleBufferedVectorDisplay<LaserTuning> vectorDisplay;
    private final Supplier<LaserDriver> laserDriver;
    private final ThreadFactory threadFactory;
    private final Set<Consumer<LaserController>> updateListeners;
    private final Config config;
    private LaserTuning laserTuning;
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
    private long msNextListenersUpdate = 0L;

    public LaserDisplay(final Config config) {
        logger.info("initialising LaserDisplay");
        this.laserTuning = config.getLaserTuning();
        this.vectorDisplay = new DoubleBufferedVectorDisplay<>(laserTuning.getMinimumLaserBrightness(), true, laserTuning);
        this.laserDriver = Suppliers.memoize(() -> {
            try {
                logger.info("Lazily creating LaserDriver (may throw)");
                return new LaserDriver(config);
            } catch (VectorBratException e) {
                logger.error("Lazy creation of LaserDriver exploded", e);
                throw new RuntimeException(e);
            }
        });
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

        if (modelDirty && !model.isEmpty()) {

            // calculate scan rate

            pathPlanner = new Interpolator(this.config);
            long startTime = System.nanoTime();
            pathPlanner.plan(model);
            setPathPlanTime((System.nanoTime() - startTime) / 1000);
            laserDriver.get().setPathPlanner(pathPlanner);
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
        laserDriver.get().start();
        running = true;
        tellListeners();
        while (running) {
            if (laserDriver.get().isOn()) {
                vectorDisplay.withLockAndFlip(this::render);
            } else {
                try {
                    //noinspection BusyWait
                    Thread.sleep(MS_POWER_NAP);
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
    public boolean getArmed() {
        return this.running && this.laserDriver.get().isOn();
    }

    /**
     * Called from ui thread. Arms the laser if it is running, otherwise makes no change to the armed state.
     *
     * @param armed true to arm.
     */
    @Override
    public void setArmed(boolean armed) {
        if (running) {
            this.laserDriver.get().setOn(armed);
        }
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
        return getTuning().getPps();
    }

    /**
     * Called from ui thread.
     *
     * @param pps new pps.
     */
    @Override
    public void setPps(int pps) {
        this.getTuning().setPps(pps);
        this.tellListeners();
    }

    @Override
    public Optional<Float> getSampleRate() {
        if (running) {
            return Optional.of(laserDriver.get().getSampleRate());
        } else {
            return Optional.empty();
        }

    }

    @Override
    public Optional<Integer> getBufferSize() {
        if (running) {
            int bufferSize = laserDriver.get().getBufferSize();
            return Optional.of(bufferSize);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public long getPathPlanTime() {
        return lastPathPlanTime;
    }

    @Override
    public void setPathPlanTime(long planTime) {
        this.lastPathPlanTime = planTime;
        long msNow = System.currentTimeMillis();
        if (msNow > msNextListenersUpdate) {
            this.tellListeners();
            msNextListenersUpdate = msNow + MS_LISTENER_UPDATE;
        }
    }

    @Override
    public void addUpdateListener(Consumer<LaserController> clc) {
        this.updateListeners.add(clc);
    }

    @Override
    public Interpolator getInterpolator() {
        return this.pathPlanner;
    }

    @Override
    public float getMinimumBrightness() {
        return laserTuning.getMinimumLaserBrightness();
    }

    @Override
    public boolean supportsBlank() {
        return true;
    }

    @Override
    public LaserTuning getTuning() {
        return laserTuning;
    }

    @Override
    public void setLaserTuning(LaserTuning laserTuning) {
        this.laserTuning = laserTuning;
        tellListeners();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
