package com.chromosundrift.vectorbrat.swing;

public interface LaserController {
    boolean getOn();

    void setOn(boolean on);

    int getPps();

    void setPps(int fps);

    /**
     * Negative number means unknown / not applicable.
     */
    float getSampleRate();

    /**
     * Negative number means unknown / not applicable.
     */
    int getBufferSize();
}
