package com.chromosundrift.vectorbrat.physics;

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
    private final float colorRate;

    // TODO parametise with configuration the maximum and minimum values for colour and coordinates

    private static final float COLOUR_MAX_VALUE = 1.0f;
    private static final float COLOUR_MIN_VALUE = 0.0f;
    private static final float COLOUR_RANGE = (COLOUR_MAX_VALUE - COLOUR_MIN_VALUE);
    private static final float XY_MAX_VALUE = 1.0f;
    private static final float XY_MIN_VALUE = -1.0f;
    private static final float XY_RANGE = (XY_MAX_VALUE - XY_MIN_VALUE);

    /**
     * Scaling factor to convert units to nano units.
     */
    private static final int NANO = 1_000_000_000;

    /**
     * Construct with fixed linear coefficients of change over time. The arguments represent the fixed linear change in
     * the corresponding colour or coordinate units per second. Positive and negative change are symmetrical.
     *
     * The colour change needs to reflect the beam colour change speed in the laser but the trail drawn by the simulator
     * needs to incorporate the eye's persistence of vision.
     *
     * @param xyRate    x and y position in units per second.
     * @param colorRate rgb units per second.
     */
    public LinearBeamPhysics(float xyRate, float colorRate) {
        this.xyRate = xyRate;
        this.colorRate = colorRate;
    }

    @Override
    public long blackToWhite() {
        // time required to go from black to white is time required
        // to cross the full colour range
        return (long) (NANO / this.colorRate);
    }

    @Override
    public long whiteToBlack() {
        return (long) (this.colorRate * NANO);
    }

    @Override
    public long nanosToBlack(Rgb from) {
        // linear change means time for brightest value to change
        float r = from.red();
        float g = from.green();
        float b = from.blue();

        float max = Math.max(r, Math.max(g, b));
        return (long) (max * NANO / this.colorRate);
    }

    @Override
    public long nanosToWhite(Rgb from) {
        float min = Math.min(from.red(), Math.min(from.green(), from.blue()));
        return (long) (min * NANO / this.colorRate);

    }

    @Override
    public long nanosTo(Rgb from, Rgb to) {
        float deltaRed = Math.abs(from.red() - to.red());
        float deltaGreen = Math.abs(from.green() - to.green());
        float deltaBlue = Math.abs(from.blue() - to.blue());
        float deltaMax = Math.min(deltaRed, Math.min(deltaGreen, deltaBlue));
        return (long) (deltaMax * NANO / this.colorRate);
    }

    /**
     * Linear implementation ignores velocity (having infinite acceleration), beam always moves at the same rate.
     *
     * @param from source beam state.
     * @param to   target beam state.
     * @return the number of nanoseconds to arrive at target state.
     */
    @Override
    public long nanosTo(BeamState from, BeamState to) {
        float maxDistance = Math.max(Math.abs(from.xPos - to.xPos), Math.abs(from.yPos - to.xPos));
        // xyRate is units per second, convert to nanos
        return ((long) (maxDistance / xyRate)) * NANO; // this order reduces loss of precision
    }

    /**
     * Modifies the given beam state Using linear physics; position is determined by linear interpolation.
     *
     * @param state      state of beam (modified).
     * @param nsTimeStep time increment to calculate the new state for.
     */
    @Override
    public void timeStep(float demandX, float demandY, BeamState state, long nsTimeStep) {
        float oldX = state.xPos;
        float oldY = state.yPos;
        float maxXyDelta = xyRate * (1e9f/nsTimeStep);
        float demandDeltaX =  demandX - oldX;
        float demandDeltaY = demandY - oldY;
        state.xPos += Math.min(demandDeltaX, maxXyDelta);
        state.yPos += Math.min(demandDeltaY, maxXyDelta);
    }
}
