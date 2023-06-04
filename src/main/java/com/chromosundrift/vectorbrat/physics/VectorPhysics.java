package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Encapsulates parametric constraints for a physical vector display. Underlying implementations can use formulaic
 * approximations derived from calibration experiments. Returned latency values should be minimum expected within
 * some preconfigured accuracy.
 *
 */
public interface VectorPhysics {

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
     * @param to target colour.
     * @return expected latency in nanoseconds.
     */
    long nanosTo(Rgb from, Rgb to);

    /**
     * Estimated time required to reach the target location starting from the source location and velocity.
     * Implementations are expected to be preconfigured with some kind of accuracy error.
     *
     * @return expected minimum latency in nanoseconds.
     */
    long nanosTo(/* params should ~?? from velocity, from location, to location*/);
}
