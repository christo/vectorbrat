package com.chromosundrift.vectorbrat.laser;

import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.audio.JavaSoundBridge;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Polygon;

public final class LaserDisplay implements VectorDisplay, Runnable {

    public static final int MAX_PPS = 30000;
    public static final int MIN_PPS = 2;
    private final DoubleBufferedVectorDisplay vectorDisplay;
    private int pps = MAX_PPS;
    private final JavaSoundBridge audioLink;

    public LaserDisplay(JavaSoundBridge audioLink) {
        this.audioLink = audioLink;
        this.vectorDisplay = new DoubleBufferedVectorDisplay();
    }

    private void renderPoly(Polygon p) {
        // TODO get audio device
        // TODO get x and y channels
        // TODO timer
        // TODO path planner
        // TODO render xy coordinates to the audio buffer - keep rendering the same point until time to move
        // TODO quintic easing
        // TODO pen down - colour
        // closed poly for now
        // TODO pen up - black
    }

    /**
     * Renders continually at the configured rate.
     */
    public void run() {
        vectorDisplay.withLockAndFlip(model -> {
            model.polygons().forEachOrdered(p -> renderPoly(p));
            return null;
        });
    }

    public int getPps() {
        return pps;
    }

    public void setPps(int pps) {
        this.pps = pps;
    }

    @Override
    public VectorDisplay setModel(Model model) {
        vectorDisplay.setModel(model);
        return this;
    }
}
