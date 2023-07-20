package com.chromosundrift.vectorbrat.data;

import java.util.stream.Stream;

import static java.lang.Math.floorMod;

public class Maths {
    public static final long THOUSAND = 1000L;
    public static final long MILLION = THOUSAND * THOUSAND;
    public static final long BILLION = THOUSAND * MILLION;

    public static long nanoToMilli(long nano) {
        return nano / MILLION;
    }

    public static float clamp(float x, float min, float max) {
        return Math.min(Math.max(min, x), max);
    }

    public static float clampNormal(float red) {
        return clamp(red, 0f, 1f);
    }

    public static long millisToNanos(int msPeriod) {
        return msPeriod * MILLION;
    }

    public static long msToNanos(int ms) {
        return ms * MILLION;
    }

    /**
     * Returns a decreasing stream of size integers that start at the given number and wrap at zero. So, after zero,
     * comes the size-1 number, unless that number was the start
     *
     * @param size  the total count - must be larger than start.
     * @param start number to start at - must be less than size and at least zero.
     * @return the stream.
     */
    public static Stream<Integer> decRing(final int size, final int start) {
        if (start >= size) {
            throw new IllegalArgumentException("size must be greater than start");
        } else if (start < 0) {
            throw new IllegalArgumentException("start not be less than zero");
        }
        return Stream.iterate(start, i -> floorMod(i - 1, size)).limit(size);
    }
}
