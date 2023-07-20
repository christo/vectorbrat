package com.chromosundrift.vectorbrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * Needed for jna to load jack native libraries, must be called before jack initialisation. Depends on
     * libjack.dylib existing at the specified path, can be installed on macos with <code>brew install jack</code>
     */
    public static void setSystemLibraryPath() {
        logger.info("setting up native library paths");
        System.setProperty("jna.library.path", "/opt/homebrew/lib");
    }

    public static final void bridgeJulToSlf4j() {
        logger.info("bridging JUL logging to Slf4j");
        try (InputStream is = Util.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    public static String truncate(String name, int maxLen) {
        return name.substring(0, Math.min(name.length() - 1, maxLen - 1));
    }

}
