package com.chromosundrift.vectorbrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

    public static final long MILLI = 1000L;
    public static final long MICRO = 1000 * MILLI;
    public static final long NANO = 1000L * MICRO;
    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * Needed for jna to load jack native libraries, must be called before jack initialisation. Depends on
     * libjack.dylib existing at the specified path, can be installed on macos with <code>brew install jack</code>
     */
    public static void setSystemLibraryPath() {
        logger.info("setting up native library paths");
        System.setProperty("jna.library.path", "/opt/homebrew/lib");
    }

    public static float clamp(float x, float min, float max) {
        return Math.min(Math.max(min, x), max);
    }

}
