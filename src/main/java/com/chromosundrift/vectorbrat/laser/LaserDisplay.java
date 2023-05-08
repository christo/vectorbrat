package com.chromosundrift.vectorbrat.laser;

import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.audio.SoundBridge;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Point;

public final class LaserDisplay implements VectorDisplay, Runnable {

    public static final int MAX_PPS = 30000;
    public static final int MIN_PPS = 2;
    private final DoubleBufferedVectorDisplay vectorDisplay;
    private int pps = MAX_PPS;

    /**
     * Time in nanos to dwell on an isolated point.
     */
    private int pointDwellNano;

    /**
     * Time to dwell on a line endpoint.
     */
    private int endPointDwellNano;

    private final SoundBridge soundBridge;
    private volatile boolean running;

    public LaserDisplay(SoundBridge soundBridge) {
        this.soundBridge = soundBridge;
        this.vectorDisplay = new DoubleBufferedVectorDisplay();
    }

    private void renderConnectedLine(Point from, Point to) {
        // do quintic easing
    }

    private void renderPoint(Point point) {
        // use dwell time
    }

    /**
     * Renders the model while holding the lock for model updates.
     *
     * @param model the model to render
     * @return null
     */
    private Void render(Model model) {
        // TODO get audio device
        // TODO get x and y channels
        // TODO get timer from audio system
        // TODO path planner - nearest unrendered neighbour
        // get all the points from the polygon and sort them to render order
        // TODO render xy coordinates to the audio buffer - keep rendering the same point until time to move
        // TODO quintic easing
        // TODO pen down - colour
        // closed poly for now
        // TODO pen up - black
        return null;
    }

    /**
     * Renders continually at the configured rate.
     */
    public void run() {
        running = true;
        while (running) {
            vectorDisplay.withLockAndFlip(this::render);
        }
    }

    public void stop() {
        running = false;
    }

    public int getPps() {
        return pps;
    }

    public void setPps(int pps) {
        this.pps = pps;
    }

    /**
     * Will block until laser is finished any in-progress model rendering.
     *
     * @param model the model to update to
     */
    @Override
    public void setModel(Model model) {
        vectorDisplay.setModel(model);
    }


}
