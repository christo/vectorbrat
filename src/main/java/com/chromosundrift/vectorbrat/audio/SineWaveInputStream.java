package com.chromosundrift.vectorbrat.audio;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.InputStream;

import com.chromosundrift.vectorbrat.Config;

/**
 * wip for javasound
 */
public class SineWaveInputStream extends InputStream {

    private static final int BYTES_PER_SAMPLE = 4;
    private static final int BITS_PER_SAMPLE = BYTES_PER_SAMPLE * 8;
    /**
     * Per sample delta
     */
    private final double delta;
    /**
     * Per byte delta
     */
    private final double deltaByte;
    /**
     * Maximum positive sample value.
     */
    private final int amp = 0x01 << 31;
    private final int[] sampleBuffer = new int[4];
    private double theta = 0d;
    private int byteIndex = 0;

    /**
     * Function generator waveform output. 32 bits per sample, signed.
     */
    private SineWaveInputStream(double freqHz, double samplesPerSecond) {
        this.delta = getRadiansPerSample(freqHz, samplesPerSecond);
        this.deltaByte = delta / 4;

    }

    public static SineWaveInputStream create(double freqHz, double samplesPerSecond) {
        SineWaveInputStream swis = new SineWaveInputStream(freqHz, samplesPerSecond);
        AudioFormat audioFormat = new AudioFormat(Config.DEFAULT_SAMPLE_RATE, BITS_PER_SAMPLE, 2, true, true);
        return swis;
    }

    private static double getRadiansPerSample(double freqHz, double samplesPerSecond) {
        return Math.TAU * samplesPerSecond / freqHz;
    }

    @Override
    public int read() throws IOException {
        if (byteIndex > 3) {
            // next sample
            theta += deltaByte;
            int sample = (int) (Math.sin(theta) * amp);
            // big endian
            sampleBuffer[0] = (sample | 0xf000) >> 24;
            sampleBuffer[1] = (sample | 0x0f00) >> 16;
            sampleBuffer[2] = (sample | 0x00f0) >> 8;
            sampleBuffer[3] = (sample | 0x000f);
            byteIndex = 0;
        }
        return sampleBuffer[byteIndex++];

    }


}
