package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Simple although unrealistic implementation of {@link BeamPhysics} intended to be useful for
 * testing and debugging the {@link LaserSimulator}.
 */
public class LinearBeamPhysics implements BeamPhysics {

    /**
     * Maximum sample units per second (position sample range is -1-1).
     */
    private final float xyRate;

    /**
     * Maximum sample units per second (colour sample range is 0-1).
     */
    private final float colourRate;

    // TODO parametise with configuration the maximum and minimum values for colour and coordinates

    private static final float COLOUR_MAX_VALUE = 1.0f;
    private static final float COLOUR_MIN_VALUE = 0.0f;
    private static final float COLOUR_RANGE = (COLOUR_MAX_VALUE - COLOUR_MIN_VALUE);
    private static final float XY_MAX_VALUE = 1.0f;
    private static final float XY_MIN_VALUE = -1.0f;
    private static final float XY_RANGE = (XY_MAX_VALUE - XY_MIN_VALUE);

    /**
     * Construct with fixed linear coefficients of change over time. The arguments represent the fixed linear change in
     * the corresponding colour or coordinate units per second. Positive and negative change are symmetrical.
     * <p>
     * The colour change needs to reflect the beam colour change speed in the laser but the trail drawn by the simulator
     * needs to incorporate the eye's persistence of vision.
     *
     * @param xyRate     x and y position in units per second.
     * @param colourRate rgb units per second.
     */
    public LinearBeamPhysics(float xyRate, float colourRate) {
        this.xyRate = xyRate;
        this.colourRate = colourRate;
    }

    /**
     * Modifies the given beam state Using linear physics; position is determined by linear interpolation.
     *
     * @param demandX    target x value
     * @param demandY    target y value
     * @param demandR    target red value
     * @param demandG    target green value
     * @param demandB    target blue value
     * @param state      state of beam (modified).
     * @param nsTimeStep time increment in ns to calculate the new state for.
     */
    @Override
    public void timeStep(float demandX, float demandY, float demandR, float demandG, float demandB, BeamState state, long nsTimeStep) {
        // calculate the maximum change in x or y for the given time step
        // xyRate is in units/s, nsTimeStep is in ns, want units/nsTimeStep
        float demandDeltaX = demandX - state.xPos;
        float demandDeltaY = demandY - state.yPos;
        float maxXyDelta = xyRate * nsTimeStep / Util.NANOS_F;
        // handle negative deltas differently, want delta with minimum absolute value
        if (demandDeltaX > 0) {
            state.xPos += Math.min(demandDeltaX, maxXyDelta);
        } else {
            state.xPos += Math.max(demandDeltaX, -maxXyDelta);
        }
        if (demandDeltaY > 0) {
            state.yPos += Math.min(demandDeltaY, maxXyDelta);
        } else {
            state.yPos += Math.max(demandDeltaY, -maxXyDelta);
        }

        state.rgb = Rgb.boundedLerp(demandR, demandG, demandB, nsTimeStep, colourRate, state.rgb);
    }

}
