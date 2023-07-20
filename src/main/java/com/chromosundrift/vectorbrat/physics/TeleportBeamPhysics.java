package com.chromosundrift.vectorbrat.physics;

import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Beam physics with infinite acceleration and infinite velocity. Always takes zero time to reach demand signal.
 * Mostly useful for debugging.
 */
public class TeleportBeamPhysics implements BeamPhysics {
    @Override
    public long blackToWhite() {
        return 0;
    }

    @Override
    public long whiteToBlack() {
        return 0;
    }

    @Override
    public long nanosToBlack(Rgb from) {
        return 0;
    }

    @Override
    public long nanosToWhite(Rgb from) {
        return 0;
    }

    @Override
    public long nanosTo(Rgb from, Rgb to) {
        return 0;
    }

    @Override
    public long nanosTo(BeamState from, BeamState to) {
        return 0;
    }

    @Override
    public void timeStep(float demandX, float demandY, BeamState state, long nsTimeStep) {
        state.xPos = demandX;
        state.yPos = demandY;
    }
}
