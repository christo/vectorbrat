package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Rgb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import com.chromosundrift.vectorbrat.data.SignalBuffer;
import com.chromosundrift.vectorbrat.geom.Pather;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserDriver;
import com.chromosundrift.vectorbrat.laser.LaserSpec;

import javax.annotation.Nonnull;

/**
 * Physical simulation of vector display replicating real-world laser projector with scanner galvanometers and
 * brightness changes over time. Configuration is intended to produce equivalent output as a real laser.
 * <p>
 * Display of the simulator on a conventional raster display needs to simulate the effect of intense light in the eye,
 * so rendition of the state at time t shows the current beam position at the head of a trail of previous positions t-1,
 * t-2 ... that fade to black, in effect simulating the eye-laser as a unified system.
 */
public final class LaserSimulator implements LaserDriver {

    private static final Logger logger = LoggerFactory.getLogger(LaserSimulator.class);
    private static final int INITIAL_BUFFER_SIZE = 20000;

    /**
     * Frames per second that make in-eye persistence appear continuous. In other words, what fraction of a second does
     * apparent afterimage vision persist.
     */
    public static final int FPS_POV = 25;

    /**
     * Nanoseconds for full bright beam spot to fade to black. Rule of thumb is 1/25s afterimage. This may depend on
     * brightness but for now, we are ignoring reactive pupil dilation due to changes in brightness.
     */
    private static final long NS_POV = 1_000_000_000 / FPS_POV;

    /*
      IDEAS FOR THE FUTURE:

       * bloom effect
       * GPU acceleration
       * a pony

     */

    private final LaserSpec laserSpec;
    private final BeamTuning tuning;
    private final Clock clock;

    /**
     * Nanosecond time of previous frame.
     */
    private long nsPrev;

    /**
     * History of past actual beam position and colour values. This is used to draw a persistence of vision in the
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
    private long nsIncomplete;


    public LaserSimulator(LaserSpec laserSpec, BeamTuning tuning, BeamPhysics physics, Clock clock) {
        logger.info("initialising LaserSimulator");
        this.laserSpec = laserSpec;
        this.tuning = tuning;
        this.clock = clock;

        this.frontIndex = 0;
        this.demandFront = new SignalBuffer(INITIAL_BUFFER_SIZE);
        this.demandBack = new SignalBuffer(INITIAL_BUFFER_SIZE);
        this.nsPrev = -1L;
    }

    @Override
    public void makePath(Pather p) {
        // pather gives us a complete set of coloured points model to work through at the configured speed


        // copy sample values into back buffers

        try {
            bufferLock.lock();
            int size = demandBack.fillPath(p);
            // now flip buffer
            SignalBuffer temp = demandBack;
            demandBack = demandFront;
            demandFront = temp;
            // TODO what to do with index
        } finally {
            bufferLock.unlock();
        }
    }

    /**
     * Update the physics simulation for the configured clock's current time, calculating the new vector position.
     * The sampleRate param enables sample rate changes to occur on simulation updates.
     *
     * @param sampleRate in samples per second (Hz)
     */
    void update(float sampleRate) {
        long nsNow = clock.getNs();
        if (nsPrev >= 0) {
            long nsDelta = nsNow - nsPrev;
            // TODO figure out the interpolation for each sample based on frame rate

            // calculate how many samples to update for
            float sPerSample = 1f / sampleRate;
            float nsPerSample = 1_000_000_000f * sPerSample;
            int samplesThisUpdate = (int) (nsPerSample / nsDelta);
            // simTime will be the time for each discrete simulation step calculation
            long simTime = nsPrev;
            for (int i = 0; i < samplesThisUpdate; i++) {
                simTime += nsPerSample;

                // now use the demand signal for each corresponding time step based on laser speed / tuning
                // to generate simulation updates


            }

            nsPrev = (long) (samplesThisUpdate * nsPerSample);
            nsIncomplete = nsNow - nsPrev;
        }
    }

    /**
     * Returns a stream of {@link Point Points} scaled to the given width and height. Each point
     * has a colour for drawing composed of its past beam locations.
     * @param width horizontal scale.
     * @param height vertical scale.
     * @return the points.
     */
    @Nonnull
    public Stream<Point> getTrail(int width, int height) {
        // for now just use beam position, ignoring the trail tail
        Rgb rgb = trail.getRgb(trailIndex);
        float x = trail.getX(trailIndex) * width;
        float h = trail.getY(trailIndex);
        Point beam = new Point(x, h * height, rgb);
        return Stream.of(beam);
    }

}