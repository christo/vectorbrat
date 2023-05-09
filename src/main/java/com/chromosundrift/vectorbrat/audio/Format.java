package com.chromosundrift.vectorbrat.audio;

import java.util.Arrays;
import java.util.List;

/**
 * Immutable representation of audio format.
 */
public final class Format {

    public static final float MAX_SAMPLE_RATE = 96000;
    public static final int MAX_CHANNELS = 16;
    public static final List<Integer> LEGIT_BITS = Arrays.asList(8, 16, 24, 32);
    private final float rate;
    private final Endianness endianness;
    private final Signitude signitude;
    private final int channels;

    public Format(float sampleRate, int bits, Endianness endianness, Signitude signitude, int channels) {
        if (sampleRate < 1 || sampleRate > MAX_SAMPLE_RATE) {
            throw new IllegalArgumentException("sample rate out of range");
        }
        if (!LEGIT_BITS.contains(bits)) {
            throw new IllegalArgumentException("bits not legit");
        }
        if (channels < 1 || channels > MAX_CHANNELS) {
            throw new IllegalArgumentException("bad number of channels");
        }
        this.rate = sampleRate;
        this.endianness = endianness;
        this.signitude = signitude;
        this.channels = channels;
    }

    public enum Signitude {SIGNED, UNSIGNED}

    public enum Endianness {BIG, LITTLE}

}
