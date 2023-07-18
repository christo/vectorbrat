package com.chromosundrift.vectorbrat.laser;


/**
 * Mutable set of tuning parameters for a laser-backed vector display. May also be useful for CRO and Vectrex but more
 * likely they'll need a different implementation. These should be surfaced in the UI and modified in order to set
 * preferred signal optimisation, balancing effective frame rate (vector models per second) and geometric fidelity
 * (through interpolation points.)
 * future: create tuning implementation for modelling vector display deflection in Vectrex (electromagnetic) and
 *   Cathode Ray Oscilloscope (electrostatic)
 */
public class BeamTuning {
    private int pps;
    private float pointsPerPoint;
    private float pointsPerUnit;
    private float vertexPoints;
    private float blackPoints;
    private float pointsPerUnitOffset;

    public static BeamTuning noInterpolation(int pps) {
        return new BeamTuning(pps, 1f, 0f, 1f, 0f, 0f);
    }

    public BeamTuning(
            int pps,
            float pointsPerPoint,
            float pointsPerUnit,
            float vertexPoints,
            float blackPoints,
            float pointsPerUnitOffset) {
        this.pps = pps;
        this.pointsPerPoint = pointsPerPoint;
        this.pointsPerUnit = pointsPerUnit;
        this.vertexPoints = vertexPoints;
        this.blackPoints = blackPoints;
        this.pointsPerUnitOffset = pointsPerUnitOffset;
    }

    public int getPps() {
        return pps;
    }

    public void setPps(int pps) {
        this.pps = pps;
    }

    public float getPointsPerPoint() {
        return pointsPerPoint;
    }

    public void setPointsPerPoint(float pointsPerPoint) {
        this.pointsPerPoint = pointsPerPoint;
    }

    public float getPointsPerUnit() {
        return pointsPerUnit;
    }

    public void setPointsPerUnit(float pointsPerUnit) {
        this.pointsPerUnit = pointsPerUnit;
    }

    public float getVertexPoints() {
        return vertexPoints;
    }

    public void setVertexPoints(float vertexPoints) {
        this.vertexPoints = vertexPoints;
    }

    public float getBlackPoints() {
        return blackPoints;
    }

    public void setBlackPoints(float blackPoints) {
        this.blackPoints = blackPoints;
    }

    public float getPointsPerUnitOffset() {
        return this.pointsPerUnitOffset;
    }

    public void setPointsPerUnitOffset(float pointsPerUnitOffset) {
        this.pointsPerUnitOffset = pointsPerUnitOffset;
    }

    public float getMinimumLaserBrightness() {
        return 0.4f;
    }
}
