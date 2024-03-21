package com.chromosundrift.vectorbrat.laser;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.geom.Interpolator;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.jack.JackLaserDriver;
import com.chromosundrift.vectorbrat.system.PeekableLazySupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * Top level VectorDisplay for laser or scope. Delegates path interpolation to {@link Interpolator} and send signal to
 * to {@link JackLaserDriver}.
 */
public final class LaserDisplay implements VectorDisplay<BeamTuning>, LaserController {

    private static final Logger logger = LoggerFactory.getLogger(LaserDisplay.class);
    private static final long MS_LISTENER_UPDATE = 100;
    private static final int MS_POWER_NAP = 300;

    private final DoubleBufferedVectorDisplay<BeamTuning> vectorDisplay;
    private final PeekableLazySupplier<JackLaserDriver> laserDriver;
    private final ThreadFactory threadFactory;

    /**
     * Listeners registered to receive updates.
     */
    private final Set<Consumer<LaserController>> updateListeners;
    private final Config config;
    private BeamTuning beamTuning;
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
    private final Interpolator interpolator;
    private long msNextListenersUpdate = 0L;

    public LaserDisplay(final Config config) {
        logger.info("initialising LaserDisplay");
        // beam tuning can be modified at runtime, get initial beam tuning from config
        this.beamTuning = config.getBeamTuning();
        this.vectorDisplay = new DoubleBufferedVectorDisplay<>(true, beamTuning);
        this.interpolator = new Interpolator(config.getInterpolation(), beamTuning);
        // only once get is called, driver is instantiated
        this.laserDriver = new PeekableLazySupplier<>(() -> {
            try {
                logger.info("Lazily creating LaserDriver (may throw)");
                return new JackLaserDriver(config);
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
        // TODO maybe we want to rerender models with previous path if model not dirty?
        if (modelDirty && !model.isEmpty()) {
            float xScale = this.getInvertX() ? -1f : 1f;
            float yScale = this.getInvertY() ? -1f : 1f;
            // calculate scan rate
            long startTime = System.nanoTime();
            interpolator.plan(model.scale(xScale, yScale));
            setPathPlanTime(System.nanoTime() - startTime);
            if (laserDriver.peek() && laserDriver.get().isOn()) {
                laserDriver.get().makePath(interpolator);
            }
            modelDirty = false;
        }

        return null;
    }

    /**
     * Renders continually at the configured rate, driver may not be connected, in which case, we do lots of nothing. Runs
     * in the current thread.
     */
    private void run() throws VectorBratException {
        logger.info("running laser display");

        running = true;
        tellListeners();
        while (running) {
            vectorDisplay.withLock(this::render);
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
     * Causes laser driver to be initialised.
     */
    public void connect() {
        try {
            // this will trigger instntiation first time
            laserDriver.get().start();
        } catch (VectorBratException e) {
            logger.error("cannot start LaserDriver", e);
        }
        // make sure we are running
        if (exec == null || exec.isShutdown()) {
            logger.info("starting laser display");

            startup();
        }
    }

    /**
     * Starts running in our own thread, continually planning the path from model updates.
     * Does not start laser driver, for that call connect().
     */
    public void startup() {
        logger.info("startup() called");
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

    /**
     * Called from ui thread.
     *
     * @return true iff we are armed.
     */
    @Override
    public boolean getArmed() {
        return running && laserDriver.peek() && laserDriver.get().isOn();
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


    @Override
    public Optional<Float> getSampleRate() {
        if (running && laserDriver.peek()) {
            return Optional.of(laserDriver.get().getSampleRate());
        } else {
            return Optional.empty();
        }

    }

    @Override
    public Optional<Integer> getBufferSize() {
        // TODO verify we need the laser driver to get a buffer size
        if (running && laserDriver.peek()) {
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
        return this.interpolator;
    }

    @Override
    public boolean supportsBlank() {
        return true;
    }

    @Override
    public BeamTuning getTuning() {
        return beamTuning;
    }

    @Override
    public void setLaserTuning(BeamTuning beamTuning) {
        this.beamTuning = beamTuning;
        tellListeners();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public boolean isConnected() {
        return this.laserDriver.peek();
    }

    @Override
    public boolean getInvertX() {
        return this.config.getInvertX();
    }

    @Override
    public void setInvertX(boolean inverted) {
        this.config.setInvertX(inverted);
    }

    @Override
    public boolean getInvertY() {
        return this.config.getInvertY();
    }

    @Override
    public void setInvertY(boolean inverted) {
        this.config.setInvertY(inverted);
    }
}
