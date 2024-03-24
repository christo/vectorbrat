package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Mutable representation of physical hardware state of hardware for an RBG scanning laser, includes x and y
 * velocity which may be used to determine impulse calculations, depending on the BeamPhysics implementation.
 * Not thread safe.
 */
final public class BeamState {

    public double xPos;
    public double yPos;
    public double xVel;
    public double yVel;
    Rgb rgb;

    public BeamState(double xPos, double yPos, double xVel, double yVel, Rgb rgb) {
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

    /**
     * Update position and velocity to dead stop if boundaries exceeded.
     */
    public void slamClamp() {
        if (xPos > 1) {
            xVel = 0;
            xPos = 1;
        } else if (xPos < -1) {
            xVel = 0;
            xPos = -1;
        }
        if (yPos > 1) {
            yVel = 0;
            yPos = 1;
        } else if (yPos < -1) {
            yVel = 0;
            yPos = -1;
        }
    }

    @Override
    public String toString() {
        return "(%.2f,%.2f)Δ(%.2f,%.2f) %s".formatted(xPos, yPos, xVel, yVel, rgb);
    }
}
