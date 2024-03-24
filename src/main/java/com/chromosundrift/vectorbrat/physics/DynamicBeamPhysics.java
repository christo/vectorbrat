package com.chromosundrift.vectorbrat.physics;

/**
 * BeamPhysics implementation that applies acceleration based on positional demand delta as well as velocity. Like
 * {@link PropAccelBeamPhysics}, if the position delta to the demand point is small, the acceleration required is
 * smaller but also, if the present velocity in the direction of the demand point is large, acceleration
 * should not be as high as if it was small or negative.
 */
public class DynamicBeamPhysics implements BeamPhysics {

    /**
     * Make the acceleration proportional to the position delta and to the position-velocity delta. The coefficient of
     * each must be found by tuning.
     *
     * @param x          demand x.
     * @param y          demand y.
     * @param r          demand r.
     * @param g          demand g.
     * @param b          demand b.
     * @param state      state to be mutated.
     * @param nsTimeStep time increment in nanoseconds to calculate the new state for.
     */
    @Override
    public void timeStep(double x, double y, float r, float g, float b, BeamState state, long nsTimeStep) {
        // TODO
    }
}
