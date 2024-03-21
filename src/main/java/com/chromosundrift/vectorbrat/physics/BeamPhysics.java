package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Encapsulates parametric constraints for a physical vector display. Generally supplies latency values in nanoseconds
 * for transitions between colours or 2D coordinates.
 * <p>
 * Underlying implementations can use formulaic approximations derived from calibration experiments. Returned latency
 * values should be minimum expected within some preconfigured accuracy. May contain inherent assumptions about laser
 * scanners.
 * <p>
 * Future: introduce a heteromorphic monochromatic oscilloscope physics used as a type parameter in Simulator?
 */
@SuppressWarnings("unused")
public interface BeamPhysics {

    /**
     * Colour transition latency from completely black to completely white.
     *
     * @return expected latency in nanoseconds.
     */
    long blackToWhite();

    /**
     * Colour transition latency from completely white to completely black.
     *
     * @return expected latency in nanoseconds.
     */
    long whiteToBlack();

    /**
     * Colour transition latency from the given colour to completely black.
     *
     * @param from source colour.
     * @return expected latency in nanoseconds.
     */
    long nanosToBlack(Rgb from);

    /**
     * Colour transition latency from the given colour to completely white.
     *
     * @param from source colour.
     * @return expected latency in nanoseconds.
     */
    long nanosToWhite(Rgb from);

    /**
     * Estimated time required to change colour between the two given Rgb values.
     *
     * @param from source colour.
     * @param to   target colour.
     * @return expected latency in nanoseconds.
     */
    long nanosTo(Rgb from, Rgb to);

    /**
     * Estimated time required to reach the target location starting from the source location and velocity.
     * Implementations are expected to be preconfigured with some kind of accuracy error.
     *
     * @return expected minimum latency in nanoseconds.
     */
    long nanosTo(BeamState from, BeamState to);

    /**
     * Perform state transition calculations based on the physics model of the implmementation. The
     * state parameter is modified.
     *
     * @param demandR
     * @param demandG
     * @param demandB
     * @param state      state to be mutated.
     * @param nsTimeStep time increment in nanoseconds to calculate the new state for.
     */
    void timeStep(float demandX, float demandY, float demandR, float demandG, float demandB, BeamState state, long nsTimeStep);
}
