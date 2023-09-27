package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Mutable struct for physical state of hardware for a scanning laser, includes x and y velocity which are used to
 * determine impulse calculations.
 */
final class BeamState {

    float xPos;
    float yPos;
    float xVel;
    float yVel;
    private final Rgb rgb;

    public BeamState(float xPos, float yPos, float xVel, float yVel, Rgb rgb) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVel = xVel;
        this.yVel = yVel;
        this.rgb = rgb;
    }

    /**
     * Motionless origin state, white beam.
     */
    public BeamState() {
        this(0f, 0f, 0f, 0f, Rgb.WHITE);
    }
}
