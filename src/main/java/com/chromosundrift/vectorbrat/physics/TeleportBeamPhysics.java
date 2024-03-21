package com.chromosundrift.vectorbrat.physics;

/**
 * Beam physics with infinite acceleration and infinite velocity. Always takes zero time to reach demand signal.
 * Mostly useful for debugging.
 */
public class TeleportBeamPhysics implements BeamPhysics {

    @Override
    public void timeStep(float demandX, float demandY, float demandR, float demandG, float demandB, BeamState state, long nsTimeStep) {
        state.xPos = demandX;
        state.yPos = demandY;
    }
}
