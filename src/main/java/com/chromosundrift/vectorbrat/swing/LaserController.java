package com.chromosundrift.vectorbrat.swing;

import java.util.function.Consumer;

import com.chromosundrift.vectorbrat.geom.PathPlanner;

public interface LaserController {

    void addUpdateListener(Consumer<LaserController> clc);

    /**
     * Is the laser on or off?
     *
     * @return true iff the laser is on.
     */
    boolean getOn();

    /**
     * Turn the laser on or off.
     *
     * @param on if true, turns laser on, otherwise turns it off.
     */
    void setOn(boolean on);

    /**
     * Gets the current points per second speed.
     *
     * @return the current value.
     */
    int getPps();

    /**
     * Sets the current points per second laser speed. Actual setting is constraint between configured maximum and
     * minimum values.
     *
     * @param fps the value.
     */
    void setPps(int fps);

    /**
     * Gets the current sample rate in Hz. Negative number means unknown / not applicable.
     *
     * @return the sample rate.
     */
    float getSampleRate();

    /**
     * Gets the current buffer size. Can be updated by the driver implementation. Negative number means
     * unknown / not applicable.
     *
     * @return the number of samples in the buffer.
     */
    int getBufferSize();

    long getPathPlanTime();

    void setPathPlanTime(long planTime);

    PathPlanner getPathPlanner();
}
