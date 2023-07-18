package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Simple although unrealistic implementation of {@link BeamPhysics} intended to be useful for
 * testing and debugging the {@link LaserSimulator}.
 */
public class LinearBeamPhysics implements BeamPhysics {

    private final float xyRate;
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
        return (long) (this.colorRate * NANO);
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

        float max = Math.max(r, Math.max(from.green(), from.blue()));
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
        float maxDistance = Math.max(Math.abs(from.xPos() - to.xPos()), Math.abs(from.yPos() - to.xPos()));
        // xyRate is units per second, convert to nanos
        return ((long)(maxDistance / xyRate)) * NANO; // this order reduces loss of precision
    }
}
