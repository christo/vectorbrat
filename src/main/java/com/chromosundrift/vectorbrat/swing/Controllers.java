package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.AppController;
import com.chromosundrift.vectorbrat.laser.LaserController;

public class Controllers {

    public final DisplayController displayController;
    public final LaserController laserController;
    public final AppController appController;

    public Controllers(DisplayController displayController,
                       LaserController laserController,
                       AppController appController) {
        this.displayController = displayController;
        this.laserController = laserController;
        this.appController = appController;
    }
}
