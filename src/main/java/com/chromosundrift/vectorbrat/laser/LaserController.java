package com.chromosundrift.vectorbrat.laser;

import java.util.Optional;
import java.util.function.Consumer;

import com.chromosundrift.vectorbrat.geom.Interpolator;

/**
 * Abstract model for controlling the laser.
 */
public interface LaserController {

    void addUpdateListener(Consumer<LaserController> clc);

    /**
     * Is the laser on or off?
     *
     * @return true iff the laser is on.
     */
    boolean getArmed();

    /**
     * Turn the laser on or off.
     *
     * @param armed if true, turns laser on, otherwise turns it off.
     */
    void setArmed(boolean armed);

    /**
     * Connects the LaserController through the LaserDriver - this does not arm the laser.
     */
    void connect();

    /**
     * Returns true only if the laser subsystem is running (not the same as if it is armed).
     * @return true iff laser driver is running.
     */
    boolean isRunning();

    /**
     * Gets the current sample rate in Hz if we are running. Negative number means unknown / not applicable.
     *
     * @return the sample rate.
     */
    Optional<Float> getSampleRate();

    /**
     * Gets the current buffer size if applicable. Can be updated by the driver implementation.
     *
     * @return the number of samples in the buffer.
     */
    Optional<Integer> getBufferSize();

    /**
     * Return the time spent planning the path in nanoseconds.
     * @return nanoseconds.
     */
    long getPathPlanTime();

    /**
     * Sets the time spent planning the path in nanoseconds.
     * @param planTime nanoseconds.
     */
    void setPathPlanTime(long planTime);

    BeamTuning getTuning();

    void setLaserTuning(BeamTuning beamTuning);

    Interpolator getInterpolator();

    boolean getInvertX();

    void setInvertX(boolean inverted);

    boolean getInvertY();

    void setInvertY(boolean inverted);
}
