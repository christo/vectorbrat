package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Implements acceleration proportional to the delta between BeamState and demand point.
 */
public class PropAccelBeamPhysics implements BeamPhysics {

    private final float maxAccel;
    private final float colourRate;

    public PropAccelBeamPhysics(float maxAccel, float colourRate) {
        this.maxAccel = maxAccel;
        this.colourRate = colourRate;
    }


    @Override
    public void timeStep(float demandX, float demandY, float demandR, float demandG, float demandB, BeamState state, long nsTimeStep) {
        float secondsToTimestep = nsTimeStep / Util.NANOS_F;

        // calculate a normalised delta to use as the coefficient of acceleration
        float kX = ((demandX + 1)/2 - (state.xPos + 1)/2) * this.maxAccel * secondsToTimestep;
        float kY = ((demandY + 1)/2 - (state.yPos + 1)/2) * this.maxAccel * secondsToTimestep;
        state.xVel += kX*kX*kX;
        state.yVel += kY*kY*kY;

        // now update beam position using beam velocity, but only for timestep, not units per second
        // if we hit the edge of the range of motion, hard slam clamping position and reset velocity to zero
        state.xPos = state.xPos + state.xVel * secondsToTimestep;
        state.yPos = state.yPos + state.yVel * secondsToTimestep;

        state.slamClamp();

        // interpolate colour change
        state.rgb = Rgb.boundedLerp(demandR, demandG, demandB, nsTimeStep, colourRate, state.rgb);
    }
}
