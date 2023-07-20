package com.chromosundrift.vectorbrat.laser;


/**
 * Represents the performance specification of a scanning RGB laser.
 */
public final class LaserSpec {

    public static final float DEG_DEFLECTION_ILDA_STANDARD = 8f;

    private float minRed = 0f;
    private float minGreen = 0f;
    private float minBlue = 0f;

    // TODO interpolation of colour change over time

    private int maxPps = 30000;

    /**
     * Points per second for ILDA specification.
     */
    private int ildaPps = 30000;

    /**
     * Deflection angle at which maxPps is specified. Default is 8 degrees.
     */
    private float ildaDeflection = DEG_DEFLECTION_ILDA_STANDARD;

    /**
     * Maxmimum deflection angle in degrees. ILDA spec standardises on eight degrees.
     */
    private final float maxDeflection;
    private final float mmBeamDivergence;
    private final float mRadBeamDivergence;


    public LaserSpec(int maxPps, float maxDeflection, float mmBeamDivergence, float mRadBeamDivergence) {
        this.maxPps = maxPps;
        this.maxDeflection = maxDeflection;
        this.mmBeamDivergence = mmBeamDivergence;
        this.mRadBeamDivergence = mRadBeamDivergence;
    }

    /**
     * Spec for target laser.
     */
    public static LaserSpec laserWorld1600Pro() {
        LaserSpec laserSpec = new LaserSpec(30000, 50, 3, 1.3f);
        laserSpec.minRed = 0.4f;
        laserSpec.minGreen = 0.4f;
        laserSpec.minBlue = 0.4f;
        laserSpec.ildaDeflection = 4f;
        laserSpec.ildaPps = 30000; // claimed! but at 4 degrees
        return laserSpec;
    }

    /**
     * With 60kpps scanner upgrade: <a href="https://www.kvantlasers.sk/products/clubmax-3000-fb4">details</a>.
     */
    public static LaserSpec kvantClubmax3000Fb4Saturn() {
        float saturn1MaxDeflection = 60f;
        int saturn1MaxPps = 60000;
        return new LaserSpec(saturn1MaxPps, saturn1MaxDeflection, 4.5f, 0.5f);
    }


    public float getMinRed() {
        return minRed;
    }

    public float getMinGreen() {
        return minGreen;
    }

    public float getMinBlue() {
        return minBlue;
    }

    public int getMaxPps() {
        return maxPps;
    }

    public int getIldaPps() {
        return ildaPps;
    }

    public float getIldaDeflection() {
        return ildaDeflection;
    }

    public float getMaxDeflection() {
        return maxDeflection;
    }

    public float getMmBeamDivergence() {
        return mmBeamDivergence;
    }

    public float getmRadBeamDivergence() {
        return mRadBeamDivergence;
    }
}
