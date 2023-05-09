package com.chromosundrift.vectorbrat.swing;

public interface LaserController {
    void setOn(boolean on);
    boolean getOn();

    void setPps(int fps);
    int getPps();
}
