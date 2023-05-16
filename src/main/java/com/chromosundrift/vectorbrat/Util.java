package com.chromosundrift.vectorbrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    public static final long MILLI = 1000L;
    public static final long MICRO = 1000* MILLI;
    public static final long NANO = 1000L * MICRO;

    /**
     * Needed for jna to load jack native libraries, must be called before jack initialisation. Depends on
     * libjack.dylib existing at the specified path, can be installed on macos with <code>brew install jack</code>
     */
    public static void setSystemLibraryPath() {
        logger.info("setting up native library paths");
        System.setProperty("jna.library.path", "/opt/homebrew/lib");
    }

    public static float clamp(float freqL, float min, float max) {
        return Math.min(Math.max(min, freqL), max);
    }

    /**
     * Input domain 0-1
     * @param x input value
     * @return quintic ease in and out
     */
    public static float quintic(float x) {
        return (float)(x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2);
    }

    /**
     * Input domain 0-1
     * @param x input value
     * @return quintic ease in and out
     */
    public static double quintic(double x) {
        return x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;
    }

}
