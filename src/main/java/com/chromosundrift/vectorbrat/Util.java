package com.chromosundrift.vectorbrat;

public class Util {
    /**
     * Needed for jna to load jack native libraries, must be called before jack initialisation. Depends on
     * libjack.dylib existing at the specified path, can be installed on macos with <code>brew install jack</code>
     */
    public static void setSystemLibraryPath() {
        System.setProperty("jna.library.path", "/opt/homebrew/lib");
    }
}
