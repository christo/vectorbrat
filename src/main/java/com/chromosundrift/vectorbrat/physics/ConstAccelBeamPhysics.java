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

    private final double xyAccel;
    private final double maxSpeed;
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
    public ConstAccelBeamPhysics(double xyAccel, double maxSpeed, float colourRate) {
        this.xyAccel = xyAccel;
        this.maxSpeed = maxSpeed;
        this.colourRate = colourRate;
    }

    /**
     * Applies the acceleration to the current beam state for the whole nsTimeStep. Colour is linearly interpolated
     * within colourRate bounds.
     *
     * @param x          new x coordinate
     * @param y          new y coordinate
     * @param r          new red value
     * @param g          new green value
     * @param b          new blue value
     * @param state      state to be mutated.
     * @param nsTimeStep time increment in nanoseconds to calculate the new state for.
     */
    @Override
    public void timeStep(double x, double y, float r, float g, float b, BeamState state, long nsTimeStep) {
        double secondsToTimestep = ((double) nsTimeStep) / Util.NANOS_D;

        // convert constant acceleration into timestep basis
        double accelPerTimetep = this.xyAccel * secondsToTimestep;
        // convert max speed into timestep basis
        double maxSpeedPerTimestep = maxSpeed * secondsToTimestep;
        // calculate new velocity components in units/s
        // first update beam velocity, maintaining units/s
        double newXvel = (x > state.xPos)
                ? Math.min(state.xVel + accelPerTimetep, maxSpeedPerTimestep)
                : Math.max(state.xVel - accelPerTimetep, -maxSpeedPerTimestep);

        double newYvel = (y > state.yPos)
                ? Math.min(state.yVel + accelPerTimetep, maxSpeedPerTimestep)
                : Math.max(state.yVel - accelPerTimetep, -maxSpeedPerTimestep);

        // now update beam position using beam velocity, but only for timestep, not units per second
        state.xPos += (newXvel * secondsToTimestep);
        state.yPos += (newYvel * secondsToTimestep);

        // update new velocities
        state.xVel = newXvel;
        state.yVel = newYvel;
        // if we hit the edge of the range of motion, hard slam clamping position and reset velocity to zero
        state.slamClamp();
        // interpolate colour change
        state.rgb = Rgb.boundedLerp(r, g, b, nsTimeStep, colourRate, state.rgb);
    }
}
