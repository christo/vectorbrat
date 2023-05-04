package com.chromosundrift.vectorbrat;

public class MissingAudioDevice extends Exception {

    public MissingAudioDevice(String deviceName) {
        super("Missing audio device %s".formatted(deviceName));
    }

}
