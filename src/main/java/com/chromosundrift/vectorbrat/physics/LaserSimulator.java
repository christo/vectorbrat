package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.data.Maths;
import com.chromosundrift.vectorbrat.data.SignalBuffer;
import com.chromosundrift.vectorbrat.geom.Pather;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserDriver;
import com.chromosundrift.vectorbrat.laser.LaserSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static java.lang.Math.floorMod;

/**
 * Physical simulation of vector display replicating real-world laser projector with scanner galvanometers and
 * brightness changes over time. Configuration is intended to produce equivalent output as a real laser.
 * <p>
 * The maximum points per second defined by the laserSpec is ignored by this simulation. Point rates
 * set in the tuning are simulated as-is.
 * <p>
 * Display of the simulator on a conventional raster display needs to simulate the effect of intense light in the eye,
 * so rendition of the state at time t shows the current beam position at the head of a trail of previous positions t-1,
 * t-2 ... that fade to black, in effect simulating the eye-laser as a unified system.
 */
public final class LaserSimulator implements LaserDriver {

    private static final Logger logger = LoggerFactory.getLogger(LaserSimulator.class);

    /**
     * Size of signal buffers in numbers of samples (aka frames).
     */
    private static final int INITIAL_BUFFER_SIZE = 20000;

    /**
     * Frames per second that make in-eye persistence appear continuous. In other words, what fraction of a second does
     * apparent afterimage vision persist.
     */
    public static final int FPS_POV = 25; // canonical value from movies

    /**
     * Nanoseconds for full bright beam spot to fade to black, aka afterimage latency. This may depend on
     * brightness but for now, we are ignoring reactive pupil dilation due to changes in brightness.
     */
    private static final long NS_POV = 1_000_000_000 / FPS_POV;
    private static final String THREAD_SIMULATOR = "simulator";

    /*
      IDEAS FOR THE FUTURE:

       * bloom effect
       * GPU acceleration
       * investigate the physical basis of unwanted resonance in scanners
       * a pony

     */

    private final LaserSpec laserSpec;
    private final BeamTuning tuning;
    private final Clock clock;
    private final ExecutorService executorService;
    private final BeamPhysics physics;

    /**
     * Nanosecond time of previous frame.
     */
    private long nsPrev;

    /**
     * History of past actual beam position and colour values in sample space. This is used to draw a persistence of vision in the
     * simulation display.
     */
    private SignalBuffer trail;

    /**
     * Ring index for trail. Previous value is at lower index, wrapping so that the highest index is the previous to
     * the zero index.
     */
    private int trailIndex = 0;


    /**
     * Reentrant lock for double buffered updates to the demand signal.
     */
    private final ReentrantLock bufferLock = new ReentrantLock();

    /**
     * Buffer for demand signal being rendered.
     */
    private SignalBuffer demandFront;

    /**
     * Buffer for demand signal being updated.
     */
    private SignalBuffer demandBack;

    /**
     * Cursor for the front buffer.
     */
    private int frontIndex;

    /**
     * Difference between time on clock and last simulation update due to no new sample being due at
     * the current sample rate.
     */
    private long nsIncomplete;

    /**
     * Current state of the scanning hardware.
     */
    private BeamState beamState;

    /**
     * Current sample rate. Can change over time.
     */
    private float sampleRate;
    private long nsNextPoint;
    private volatile boolean running = false;

    public LaserSimulator(LaserSpec laserSpec, BeamTuning tuning, BeamPhysics physics, Clock clock) {
        this.physics = physics;
        logger.info("initialising LaserSimulator");
        this.laserSpec = laserSpec;
        this.tuning = tuning;
        this.clock = clock;

        this.frontIndex = 0;
        this.demandFront = new SignalBuffer(INITIAL_BUFFER_SIZE);
        this.demandBack = new SignalBuffer(INITIAL_BUFFER_SIZE);
        this.nsPrev = -1L;
        this.trail = new SignalBuffer(5); // only initial trail size
        this.trail.reset();
        this.executorService = Executors.newSingleThreadExecutor(r -> new Thread(r, THREAD_SIMULATOR));
        this.beamState = new BeamState();
        this.nsNextPoint = clock.getNs(); // deadline for next demand buffer point
    }

    /**
     * Eats a new path from the provided {@link Pather} and flips the buffers.
     *
     * @param p source of new demand signal.
     */
    @Override
    public void makePath(Pather p) {
        // pather gives us a complete set of coloured points model to work through at the configured sample rate
        try {
            bufferLock.lock();
            int size = demandBack.fillPath(p);
            // now flip buffer
            SignalBuffer temp = demandBack;
            demandBack = demandFront;
            demandFront = temp;
            // may create a glitch moment - better logic belongs in path planning / double buffered vector display
            frontIndex = 0;
        } finally {
            bufferLock.unlock();
        }
    }

    /**
     * Update the simulation using the current sample rate and point rate from {@link BeamTuning}.
     */
    public void update() {

        // if we have done a previous update, calculate the simulation updates based on the delta
        if (nsPrev >= 0) {
            // determine the now time for this update
            long nsNow = clock.getNs();
            long nsElapsed = nsNow - nsPrev;

            // calculate how many samples to update for
            long nsPerSample = 1_000_000_000 / ((long) sampleRate);
            int samplesThisUpdate = (int) (nsElapsed / nsPerSample);
            // simTime will be the time for each discrete simulation step calculation
            long simTime = nsPrev;
            long nsPp = tuning.getNsPerPoint();
            // send some number of samples and advance the simulation for each sample
            try {
                bufferLock.lock();
                for (int i = 0; i < samplesThisUpdate; i++) {
                    simTime += nsPerSample;

                    // look up demand location / current point being drawn
                    float demandX = demandFront.getX(frontIndex);
                    float demandY = demandFront.getY(frontIndex);

                    // now use the demand signal for each corresponding time step to generate simulation updates
                    physics.timeStep(demandX, demandY, beamState, nsPerSample);

                    // depending on the physics, the beamState will be updated for the demand point

                    // update the trail history for this sample
                    trail.set(beamState.xPos, beamState.yPos, 1f, 1f, 1f, trailIndex);

                    {
                        // advance the trail, wrapping the trail ring buffer index if necessary
                        trailIndex++;
                        int size = trail.getMaxSize();
                        if (trailIndex >= size) {
                            trailIndex -= size;
                        }
                    }

                    // is it time for the next demand point in the buffer?
                    if (nsNextPoint <= simTime) {
                        // we are at or past the deadline to advance to the next point in the buffer
                        frontIndex++;
                        int size = demandFront.getActualSize();
                        if (frontIndex >= size) {
                            // wrap the ring buffer
                            frontIndex -= size;
                        }
                        // set new next point deadline
                        nsNextPoint += nsPp;
                    }

                }
            } finally {
                bufferLock.unlock();
            }

            nsPrev = simTime;
            // calculate the time delta to the now of this call. We only do whole sample simulation steps
            nsIncomplete = nsNow - nsPrev;
        } else {
            // we haven't done a previous update, just set the previous clock time for next update
            nsPrev = clock.getNs();
        }

    }

    public void setSampleRate(float sampleRate) {
        try {
            bufferLock.lock();
            this.sampleRate = sampleRate;
            // need to update the trail size based on pps and sample rate
            // samples per POV
            int newTrailSize = (int) Math.ceil(sampleRate / FPS_POV);
            if (newTrailSize != trail.getActualSize()) {
                trail.setActualSize(newTrailSize);
            }
        } finally {
            bufferLock.unlock();
        }
    }

    /**
     * Returns a stream of {@link Point Points} scaled to the given width and height. Each point
     * has a colour for drawing composed of its past beam locations. For rendering points on
     * conventional raster, they will need to be translated and scaled out of sample range.
     *
     * @return the points in sample range.
     */
    @Nonnull
    public Stream<Point> getTrail() {
        int s = trail.getActualSize();
        if (s == 0) {
            return Stream.empty();
        } else {
            return Maths.decRing(s, trailIndex).map(i -> trail.toPoint(i));
        }
    }

    public void start() {
        logger.info("starting LaserSimulator");
        executorService.submit(this::run);
    }

    private void run() {
        logger.info("run()");
        running = true;
        long nsPerLog = 1_000_000_00;
        long logUpdateDeadline = System.nanoTime() + nsPerLog;
        long nsUpdateDuration;
        while (running) {
            try {
                long nsPreUpdateTime = System.nanoTime();
                try {
                    update();
                } catch (Exception e) {
                    logger.error("update spewed", e);
                    // no point rethrowing; executor swallows exception anyway?
                }
                long nsPostUpdateTime = System.nanoTime();
                nsUpdateDuration = nsPostUpdateTime - nsPreUpdateTime;
                if (nsPostUpdateTime >= logUpdateDeadline) {
                    logger.info("updated (%s ns)".formatted(nsUpdateDuration));
                }
                logUpdateDeadline = nsPostUpdateTime + nsPerLog;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("finished run()");
    }

    public void stop() {
        executorService.shutdown();
    }

    /**
     * Using the current sample rate and point rate, calculate the samples per point.
     *
     * @return number of samples per point.
     */
    public float getSamplesPerPoint() {
        return sampleRate / tuning.getPps();
    }


    /// TEMPORARY TEST METHODS:

    public long getTime() {
        return nsPrev;
    }

    public int getTrailIndex() {
        return this.trailIndex;
    }

    public int getFrontIndex() {
        return this.frontIndex;
    }

    public int getFrontSize() {
        return this.demandFront.getActualSize();
    }
}