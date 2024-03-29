package com.chromosundrift.vectorbrat;

import java.util.Arrays;
import java.util.List;

import com.chromosundrift.vectorbrat.geom.Interpolation;
import com.chromosundrift.vectorbrat.laser.LaserSpec;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.physics.BeamPhysics;
import com.chromosundrift.vectorbrat.physics.ConstAccelBeamPhysics;
import com.chromosundrift.vectorbrat.physics.LinearBeamPhysics;


/**
 * Some config items are just constants.
 */
public final class Config {

    public static final String DEFAULT_TITLE = "Vector Brat";
    public static final String DEFAULT_TINY_TITLE = "VBrat";

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
     * Arbitrary low number not too slow for holding model locks.
     */
    public static final int MIN_PPS = 5;

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
    private static final float DEFAULT_LINE_WIDTH = 1.5f;

    private static final float DEFAULT_POINTS_PER_POINT = 13f;
    private static final float DEFAULT_POINTS_PER_UNIT = 110f;
    private static final float DEFAULT_VERTEX_POINTS = 5f;
    private static final float DEFAULT_BLACK_POINTS = 9f;
    private static final float DEFAULT_POINTS_PER_UNIT_OFFSET = 9f;

    private static final Interpolation DEFAULT_INTERPOLATION = Interpolation.QUINTIC;
    // TODO get this working with more advanced BeamPhysics implementations
    public static final BeamPhysics DEFAULT_BEAM_PHYSICS = new LinearBeamPhysics(1d, 1f);

    private final Channel channelX;
    private final Channel channelY;
    private final Channel channelR;
    private final Channel channelG;
    private final Channel channelB;
    private final String title;
    private final String tinyTitle;
    private final boolean liveControls;
    private BeamTuning beamTuning;

    private Interpolation interpolation = DEFAULT_INTERPOLATION;
    private final LaserSpec laserSpec;
    private float lineWidth;
    private BeamPhysics beamPhysics;

    private boolean invertX = false;
    private boolean invertY = true;

    public Config(String title, String tinyTitle) {
        this.title = title;
        this.tinyTitle = tinyTitle;
        this.channelX = new Channel("X-channel", DEFAULT_ES9_CHANNEL_X);
        this.channelY = new Channel("Y-channel", DEFAULT_ES9_CHANNEL_Y);
        this.channelR = new Channel("R-channel", DEFAULT_ES9_CHANNEL_R);
        this.channelG = new Channel("G-channel", DEFAULT_ES9_CHANNEL_G);
        this.channelB = new Channel("B-channel", DEFAULT_ES9_CHANNEL_B);
        this.lineWidth = DEFAULT_LINE_WIDTH;
        this.liveControls = true;
        this.beamTuning = new BeamTuning(
                DEFAULT_PPS,
                DEFAULT_POINTS_PER_POINT,
                DEFAULT_POINTS_PER_UNIT,
                DEFAULT_VERTEX_POINTS,
                DEFAULT_BLACK_POINTS,
                DEFAULT_POINTS_PER_UNIT_OFFSET);
        this.beamPhysics = DEFAULT_BEAM_PHYSICS;
        this.laserSpec = LaserSpec.laserWorld1600Pro();
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

    public BeamTuning getBeamTuning() {
        return beamTuning;
    }

    public void getBeamTuning(BeamTuning lt) {
        this.beamTuning = lt;
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

    public Interpolation getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    public String getTinyTitle() {
        return this.tinyTitle;
    }

    public LaserSpec getLaserSpec() {
        return this.laserSpec;
    }

    public void setBeamTuning(BeamTuning tuning) {
        this.beamTuning = tuning;
    }

    public BeamPhysics getBeamPhysics() {
        return beamPhysics;
    }

    public void setBeamPhysics(BeamPhysics beamPhysics) {
        this.beamPhysics = beamPhysics;
    }

    public boolean getInvertX() {
        return invertX;
    }

    public void setInvertX(boolean invertX) {
        this.invertX = invertX;
    }

    public boolean getInvertY() {
        return invertY;
    }

    public void setInvertY(boolean invertY) {
        this.invertY = invertY;
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
