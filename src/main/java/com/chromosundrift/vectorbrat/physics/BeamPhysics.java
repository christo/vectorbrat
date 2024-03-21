package com.chromosundrift.vectorbrat.physics;

/**
 * Encapsulates parametric constraints for a physical vector display. Generally supplies latency values in nanoseconds
 * for transitions between colours or 2D coordinates.
 * <p>
 * Underlying implementations can use formulaic approximations derived from calibration experiments. Returned latency
 * values should be minimum expected within some preconfigured accuracy. May contain inherent assumptions about laser
 * scanners.
 */
public interface BeamPhysics {

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
