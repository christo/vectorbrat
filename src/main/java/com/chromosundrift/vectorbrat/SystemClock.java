package com.chromosundrift.vectorbrat;

import java.util.function.Supplier;

public final class SystemClock implements Supplier<Long> {

    public static final SystemClock INSTANCE = new SystemClock();

    private SystemClock() {
    }

    @Override
    public Long get() {
        return System.nanoTime();
    }
}
