package com.chromosundrift.vectorbrat.laser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.chromosundrift.vectorbrat.Clock;
import com.chromosundrift.vectorbrat.geom.Pather;

/**
 * Physical simulation of vector display replicating real-world laser projector with scanner galvanometers and
 * brightness changes over time. Configuration is intended to produce equivalent output as a real laser.
 * <p>
 * Display of the simulator on a conventional raster display needs to simulate the effect of intense light in the eye,
 * so rendition of the state at time t shows the current beam position at the head of a trail of previous positions
 * t-1, t-2 ... that fade to black, in effect simulating the eye-laser as a unified system.
 */
public class LaserSimulator implements LaserDriver {

    private static final Logger logger = LoggerFactory.getLogger(LaserSimulator.class);

    /*
      IDEAS FOR THE FUTURE:

       * bloom effect
       * GPU acceleration
       * a pony

       TODO: maybe introduce an XyrgbBuffer data class to reuse
     */

    private final ReentrantLock lock = new ReentrantLock();

    private final LaserSpec laserSpec;
    private final LaserTuning tuning;
    private final Clock clock;
    private static final int LENGTH_TRAIL = 100;

    /**
     * Ring buffer of x values
     */
    private float[] y;

    /**
     * Ring buffer of y values
     */
    private float[] x;

    /**
     * Ring buffer of red values
     */
    private float[] r;

    /**
     * Ring buffer of green values
     */
    private float[] g;

    /**
     * Ring buffer of blue values
     */
    private float[] b;

    /**
     * Cursor for the front buffer.
     */
    private int index;

    /**
     * Back buffer for x
     */
    private float[] x2;

    /**
     * Back buffer for y
     */
    private float[] y2;

    /**
     * Back buffer for red.
     */
    private float[] r2;

    /**
     * Back buffer for red.
     */
    private float[] g2;

    /**
     * Back buffer for red.
     */
    private float[] b2;

    public LaserSimulator(LaserSpec laserSpec, LaserTuning tuning, Clock clock) {
        logger.info("initialising LaserSimulator");
        this.laserSpec = laserSpec;
        this.tuning = tuning;
        this.clock = clock;
        // Ideally the length of the trail should be based on brightness, colour fade latency and time
        // because for a given bright point, the eye will retain a persistent effect. Also, the
        // faster the laser point moves, the longer the trail and the dimmer the apparent brightness
        // of the trail would be.
        this.x = new float[LENGTH_TRAIL];
        this.y = new float[LENGTH_TRAIL];
        this.index = 0;
    }

    @Override
    public void setPather(Pather p) {
        // pather gives us a complete set of coloured points model to work through at the configured speed

        ArrayList<Float> xs = p.getXs();
        ArrayList<Float> ys = p.getYs();
        ArrayList<Float> rs = p.getRs();
        ArrayList<Float> gs = p.getGs();
        ArrayList<Float> bs = p.getBs();

        // copy sample values into back buffers
        int s = xs.size();
        if (s != ys.size() || s != rs.size() || s != gs.size() || s != bs.size()) {
            throw new IllegalArgumentException("Pather gave us float buffers of nonuniform size");
        }

        try {
            lock.lock();
            for (int i = 0; i < s; i++) {
                x2[i] = xs.get(i);
                y2[i] = ys.get(i);
                r2[i] = rs.get(i);
                g2[i] = gs.get(i);
                b2[i] = bs.get(i);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Renders the display area as integer format rgb data in the provided array. Pixels are in 32 bit ARGB integer
     * format.
     */
    public void render(int[] argb) {
        try {
            lock.lock();
            // draw fading trail based on nsTime

        } finally {
            lock.unlock();
        }
    }
}
