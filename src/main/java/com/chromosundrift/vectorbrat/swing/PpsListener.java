package com.chromosundrift.vectorbrat.swing;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.chromosundrift.vectorbrat.laser.LaserController;

class PpsListener implements ChangeListener {

    private final boolean live;
    private final LaserController lc;

    public PpsListener(boolean live, LaserController laserController) {
        this.live = live;
        lc = laserController;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (live || !source.getValueIsAdjusting()) {
            lc.getTuning().setPps(source.getValue());
        }
    }
}
