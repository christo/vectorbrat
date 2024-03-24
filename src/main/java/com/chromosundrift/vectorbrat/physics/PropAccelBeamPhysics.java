package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Implements acceleration proportional to the delta between BeamState and demand point.
 */
public class PropAccelBeamPhysics implements BeamPhysics {

    private final double maxAccel;
    private final float colourRate;

    public PropAccelBeamPhysics(double maxAccel, float colourRate) {
        this.maxAccel = maxAccel;
        this.colourRate = colourRate;
    }


    @Override
    public void timeStep(double x, double y, float r, float g, float b, BeamState state, long nsTimeStep) {
        double secondsToTimestep = nsTimeStep / Util.NANOS_F;

        // calculate normalised position deltas to use as the coefficient of acceleration
        // if we are almost at the demand position the acceleration towards the point should be almost zero
        double kX = ((x + 1)/2 - (state.xPos + 1)/2) * this.maxAccel * secondsToTimestep;
        double kY = ((y + 1)/2 - (state.yPos + 1)/2) * this.maxAccel * secondsToTimestep;
        state.xVel += kX;
        state.yVel += kY;

        // now update beam position using beam velocity, but only for timestep, not units per second
        state.xPos = state.xPos + state.xVel * secondsToTimestep;
        state.yPos = state.yPos + state.yVel * secondsToTimestep;

        state.slamClamp();

        // interpolate colour change
        state.rgb = Rgb.boundedLerp(r, g, b, nsTimeStep, colourRate, state.rgb);
    }
}
