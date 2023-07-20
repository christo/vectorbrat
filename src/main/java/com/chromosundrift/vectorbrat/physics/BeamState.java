package com.chromosundrift.vectorbrat.physics;

/**
 * Mutable struct for physical state of hardware for a scanning laser, includes x and y velocity which are used to
 * determine impulse calculations.
 * future: will need to include colour state
 */
final class BeamState {

    float xPos;
    float yPos;
    float xVel;
    float yVel;

    public BeamState(float xPos, float yPos, float xVel, float yVel) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVel = xVel;
        this.yVel = yVel;
    }

    /**
     * Zero-position state.
     */
    public BeamState() {
        this(0f, 0f, 0f, 0f);
    }
}
