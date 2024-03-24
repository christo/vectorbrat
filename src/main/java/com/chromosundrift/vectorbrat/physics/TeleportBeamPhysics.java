package com.chromosundrift.vectorbrat.physics;

/**
 * Beam physics with infinite acceleration and infinite velocity. Always takes zero time to reach demand signal.
 * Mostly useful for debugging.
 */
public class TeleportBeamPhysics implements BeamPhysics {

    @Override
    public void timeStep(double x, double y, float r, float g, float b, BeamState state, long nsTimeStep) {
        state.xPos = x;
        state.yPos = y;
    }
}
