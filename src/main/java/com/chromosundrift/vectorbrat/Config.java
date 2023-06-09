package com.chromosundrift.vectorbrat;

import java.util.Arrays;
import java.util.List;

import com.chromosundrift.vectorbrat.geom.Interpolation;


/**
 * Some config items are just constants.
 */
public final class Config {

    public static final String DEFAULT_TITLE = "Vector Brat";
    public static final String DEFAULT_TINY_TITLE = "VBrat";

    /**
     * The look and feel to use for the ui.
     */
    public static final String LAF = "Mac OS X";

    public static final float DEFAULT_SAMPLE_RATE = 96000;
    public static final int DEFAULT_ES9_CHANNEL_X = 9;
    public static final int DEFAULT_ES9_CHANNEL_Y = 10;
    public static final int DEFAULT_ES9_CHANNEL_R = 11;
    public static final int DEFAULT_ES9_CHANNEL_G = 12;
    public static final int DEFAULT_ES9_CHANNEL_B = 13;

    /**
     * The main ES-9 device
     */
    public static final String ES9 = "ES-9";

    /**
     * ES-8 device
     */
    public static final String ES8 = "ES-8";

    /**
     * X/Y aggregate device set up with AudioMidi.app
     */
    public static final String DEFAULT_XY = "es-9_X_Y";

    /**
     * Red and Z (spare) pair of channels.
     */
    public static final String DEFAULT_RZ = "es-9_R_Z";

    /**
     * Green and blue pair of channels.
     */
    public static final String DEFAULT_GB = "es-9_G_B";


    // laser config details

    public static final String LASER_MODEL = "PRO-1600RGB";
    public static final String LASER_MAKE = "LaserWorld";
    public static final int MAX_PPS = 40000;    // without low-pass filter

    /**
     * Deflection in degrees for maximum PPS
     */
    public static final int PPS_DEFLECTION_DEG = 4;

    /**
     * Arbitrary low number not too slow for holding model locks.
     */
    public static final int MIN_PPS = 5;

    public static final int MAX_DEFLECTION_DEG = 40;

    /**
     * Apparently there is a nonlinear relationship between the safe maximum pps capability of the laser at different
     * arcs of deflection. Specifications typically quote pps with accompanying deflection in degrees (typically 8
     * degrees, per ILDA). The laser's maximum deflection is much larger than the angle corresponding to the max pps.
     * Therefore the following linear extrapolation will be incorrect. Most advice proposes to find an appropriate pps
     * setting using the ILDA 30kpps test pattern and increasing the pps setting until either the test pattern
     * loses geometric fidelity or the galvanometers start to whine louder.
     */
    public static final int MAX_PPS_FULL_DEFLECTION_WRONG = MAX_PPS * PPS_DEFLECTION_DEG / MAX_DEFLECTION_DEG;

    /**
     * Minimum value for a sample.
     */
    public static final float SAMPLE_MIN = -1.0f;

    /**
     * Maximum value for a sample.
     */
    public static final float SAMPLE_MAX = 1.0f;

    /**
     * Peak to trough range in sample representation.
     */
    public static final float SAMPLE_RANGE = Math.abs(Config.SAMPLE_MAX - Config.SAMPLE_MIN);

    private static final int DEFAULT_PPS = 30000;
    private static final float DEFAULT_LINE_WIDTH = 5.5f;

    private static final float DEFAULT_POINTS_PER_POINT = 14f;
    private static final float DEFAULT_POINTS_PER_UNIT = 140f;
    private static final float DEFAULT_VERTEX_POINTS = 6f;
    private static final float DEFAULT_BLACK_POINTS = 11f;
    private static final float DEFAULT_POINTS_PER_UNIT_OFFSET = 10f;
    private static final Interpolation DEFAULT_INTERPOLATION = Interpolation.QUINTIC;
    private final Channel channelX;
    private final Channel channelY;
    private final Channel channelR;
    private final Channel channelG;
    private final Channel channelB;
    private final String title;
    private final String tinyTitle;
    private final boolean liveControls;
    private float pointsPerPoint = DEFAULT_POINTS_PER_POINT;
    private float pointsPerUnit = DEFAULT_POINTS_PER_UNIT;
    private float vertexPoints = DEFAULT_VERTEX_POINTS;
    private float blackPoints = DEFAULT_BLACK_POINTS;
    private float pointsPerUnitOffset = DEFAULT_POINTS_PER_UNIT_OFFSET;
    private Interpolation interpolation = DEFAULT_INTERPOLATION;
    private int pps;
    private boolean lockout;
    private float lineWidth;

    public Config(String title, String tinyTitle) {
        this.title = title;
        this.tinyTitle = tinyTitle;
        this.pps = DEFAULT_PPS;
        this.channelX = new Channel("X-channel", DEFAULT_ES9_CHANNEL_X);
        this.channelY = new Channel("Y-channel", DEFAULT_ES9_CHANNEL_Y);
        this.channelR = new Channel("R-channel", DEFAULT_ES9_CHANNEL_R);
        this.channelG = new Channel("G-channel", DEFAULT_ES9_CHANNEL_G);
        this.channelB = new Channel("B-channel", DEFAULT_ES9_CHANNEL_B);
        this.lineWidth = DEFAULT_LINE_WIDTH;
        this.liveControls = true;
    }

    public Config() {
        this(DEFAULT_TITLE, DEFAULT_TINY_TITLE);
    }

    public static List<String> requiredDevices() {
        return Arrays.asList(DEFAULT_XY, DEFAULT_RZ, DEFAULT_GB);
    }

    public static List<String> expectedDevices() {
        return Arrays.asList(ES9, DEFAULT_XY, DEFAULT_RZ, DEFAULT_GB);
    }

    public static List<String> knownDevices() {
        return Arrays.asList(ES9, ES8, DEFAULT_XY, DEFAULT_RZ, DEFAULT_GB);
    }

    public static boolean inSampleRange(float value) {
        return value >= SAMPLE_MIN && value <= SAMPLE_MAX;
    }

    public String getTitle() {
        return title;
    }

    public String logoUrl() {
        return "vectorbrat.png";
    }

    public int getPps() {
        return pps;
    }

    public void setPps(int pps) {
        this.pps = pps;
    }

    public Channel getChannelX() {
        return channelX;
    }

    public Channel getChannelY() {
        return channelY;
    }

    public Channel getChannelR() {
        return channelR;
    }

    public Channel getChannelG() {
        return channelG;
    }

    public Channel getChannelB() {
        return channelB;
    }

    public float getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public boolean liveControls() {
        return this.liveControls;
    }

    public String getLaf() {
        return LAF;
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

    public Interpolation getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    public float getPointsPerUnitOffset() {
        return this.pointsPerUnitOffset;
    }

    public void setPointsPerUnitOffset(float pointsPerUnitOffset) {
        this.pointsPerUnitOffset = pointsPerUnitOffset;
    }

    public String getTinyTitle() {
        return this.tinyTitle;
    }

    public float getMinimumLaserBrightness() {
        return 0.4f;
    }

    /**
     * Represents a configured audio channel with our name and 1-based index.
     *
     * @param name   the name
     * @param number the 1-based index local to the audio device.
     */
    public record Channel(String name, int number) {
    }
}
