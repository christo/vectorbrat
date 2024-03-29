package com.chromosundrift.vectorbrat.audio.jack;

import java.nio.FloatBuffer;

import com.chromosundrift.vectorbrat.data.Maths;

public class SoundGenerator {

    public static void whiteNoise(final float amplitude, final FloatBuffer output) {
        whiteNoise(amplitude, new FloatBuffer[]{output});
    }

    public static void whiteNoise(final float amplitude, final FloatBuffer... outputs) {
        float amp = Maths.clamp(amplitude, 0f, 1.0f);
        for (FloatBuffer buf : outputs) {
            int size = buf.capacity();
            for (int i = 0; i < size; i++) {
                buf.put(i, ((float) Math.random() - 0.5f) * amp);
            }
        }
    }
}
