package com.chromosundrift.vectorbrat.audio;

public class MissingAudioDevice extends Exception {

    public MissingAudioDevice(String deviceName) {
        super("Missing audio device %s".formatted(deviceName));
    }

}
