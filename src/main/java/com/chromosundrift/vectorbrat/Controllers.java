package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.laser.LaserController;
import com.chromosundrift.vectorbrat.swing.DisplayController;

/**
 * Various controllers grouped together for UI convenience.
 */
public record Controllers(DisplayController displayController,
                          LaserController laserController,
                          AppController appController) { }
