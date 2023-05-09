package com.chromosundrift.vectorbrat.swing;

public interface LaserController {
    void setOn(boolean on);

    boolean getOn();

    void setPps(int fps);

    int getPps();

    /**
     * Negative number means unknown / not applicable.
     */
    float getSampleRate();

    /**
     * Negative number means unknown / not applicable.
     */
    int getBufferSize();
}
