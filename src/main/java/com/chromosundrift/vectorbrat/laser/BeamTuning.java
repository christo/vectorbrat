package com.chromosundrift.vectorbrat.laser;


/**
 * Mutable set of tuning parameters for a laser-backed vector display. Contains parameters like vertex points,
 * points per point, black points etc. This affects the number of samples the demand signal dwells on geometric
 * features to accommodate for vector lag.
 * <p>
 * May also be useful for CRO and Vectrex but more likely they'll need a different implementation. These should be
 * surfaced in the UI and modified in order to set preferred signal optimisation, balancing effective frame rate
 * (vector models per second) and geometric fidelity (through interpolation points.)
 * <p>
 */
public class BeamTuning {
    // future: create tuning implementation for modelling vector display deflection in Vectrex (electromagnetic)
    //  and Cathode Ray Oscilloscope (electrostatic)
    private int pps;
    private float pointsPerPoint;
    private float pointsPerUnit;
    private float vertexPoints;
    private float blackPoints;
    private float pointsPerUnitOffset;

    /**
     * Constructs an instance with no interpolation, just the points per second.
     *
     * @param pps points per second.
     * @return a no-interpolation BeamTuning
     */
    public static BeamTuning noInterpolation(int pps) {
        return new BeamTuning(pps, 1f, 0f, 1f, 0f, 0f);
    }

    /**
     * Constructs a BeamTuning.
     *
     * @param pps                 points per second, as specified a laser.
     * @param pointsPerPoint      number of samples spent an isolated points (should be higher for brighter dots).
     * @param pointsPerUnit       the number of interpolated samples per normalised unit.
     * @param vertexPoints        the number of samples spent in dwell at geometric vertices.
     * @param blackPoints         the number of samples spent at points supposed to be black.
     * @param pointsPerUnitOffset offset number added to points per unit.
     */
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
        // this is a rough guestimate from experimenting with LW1600Pro
        return 0.4f;
    }


    /**
     * Based on points per second, give number of nanoseconds per point.
     *
     * @return time per point in ns.
     */
    public long getNsPerPoint() {
        return 1_000_000_000 / this.pps;
    }
}
