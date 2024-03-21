package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Implementation which applies a constant acceleration to the beam state. This can result in polynomial hysteresis
 * such as corner cutting and overshoot on xy motion. This is hopefully a useful approximation of real laser optics.
 * Colour change is linear with a fixed maximum rate of change.
 * <p>
 * And... no it doesn't. The result is dominated by hyperactive oscillation. Acceleration needs to be proportional to
 * the delta from beamstate to demand point. That implementation is in {@link PropAccelBeamPhysics}
 */
public class ConstAccelBeamPhysics implements BeamPhysics {

    private final float xyAccel;
    private final float maxSpeed;
    private final float colourRate;

    /**
     * Construct a {@link BeamPhysics} that implements {@link BeamState} changes by applying xyAccel units/s^2 of
     * positional change. Each second, xyAccel change in velocity is applied. Additionally maximum absolute velocity
     * (speed either +ve or -ve) will not be exceded.
     *
     * @param xyAccel    units/s^2 constant acceleration applied to {@link BeamState}
     * @param maxSpeed   velocity cap in units/s for x and y independently.
     * @param colourRate linear rate of change for colour in units/s
     */
    public ConstAccelBeamPhysics(float xyAccel, float maxSpeed, float colourRate) {
        this.xyAccel = xyAccel;
        this.maxSpeed = maxSpeed;
        this.colourRate = colourRate;
    }

    /**
     * Applies the acceleration to the current beam state for the whole nsTimeStep. Colour is linearly interpolated
     * within colourRate bounds.
     *
     * @param demandX    new x coordinate
     * @param demandY    new y coordinate
     * @param demandR    new red value
     * @param demandG    new green value
     * @param demandB    new blue value
     * @param state      state to be mutated.
     * @param nsTimeStep time increment in nanoseconds to calculate the new state for.
     */
    @Override
    public void timeStep(float demandX, float demandY, float demandR, float demandG, float demandB, BeamState state, long nsTimeStep) {

        float secondsToTimestep = nsTimeStep / Util.NANOS_F;

        float accelPerTimetep = this.xyAccel * secondsToTimestep;
        // calculate new velocity components in units/s
        // first update beam velocity, maintaining units/s
        if (demandX > state.xPos) {
            state.xVel = Math.min(state.xVel + accelPerTimetep, maxSpeed);
        } else if (demandX < state.xPos) {
            state.xVel = Math.max(state.xVel - accelPerTimetep, -maxSpeed);
        }
        if (demandY > state.yPos) {
            state.yVel = Math.min(state.yVel + accelPerTimetep, maxSpeed);
        } else if (demandY < state.yPos) {
            state.yVel = Math.max(state.yVel - accelPerTimetep, -maxSpeed);
        }

        // now update beam position using beam velocity, but only for timestep, not units per second
        // if we hit the edge of the range of motion, hard slam clamping position and reset velocity to zero
        state.xPos = state.xPos + state.xVel * secondsToTimestep;
        state.yPos = state.yPos + state.yVel * secondsToTimestep;
        state.slamClamp();
        // interpolate colour change
        state.rgb = Rgb.boundedLerp(demandR, demandG, demandB, nsTimeStep, colourRate, state.rgb);
    }
}
