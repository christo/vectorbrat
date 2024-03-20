package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Mutable representation of physical hardware state of hardware for an RBG scanning laser, includes x and y
 * velocity which may be used to determine impulse calculations, depending on the BeamPhysics implementation.
 * Not thread safe.
 */
final public class BeamState {

    float xPos;
    float yPos;
    float xVel;
    float yVel;
    Rgb rgb;

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
